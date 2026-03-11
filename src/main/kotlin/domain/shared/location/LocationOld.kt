package domain.shared.location

import domain.shared.enums.areatype.RegionType
import edu.kit.ifv.units.KCoordinate
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.UTMPosition
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import edu.kit.ifv.units.toDistance
import org.geotools.api.referencing.cs.CoordinateSystem
import org.geotools.referencing.GeodeticCalculator
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.operation.distance.DistanceOp

data class RoadAccess(val roadId: Long, val position: UnitIntervalValue, val lateralDistance: Distance = 0.meters)
interface LocationOld {
    val coordinate: KCoordinate
    val zone: Zone?
    val roadAccess: RoadAccess?
    fun regionType(): RegionType = requireZone().regionType
    fun zoneID(): ZoneId?
    fun inSameZone(other: LocationOld) = this.zone == other.zone
    fun withZone(zone: Zone): LocationOld {
        require(this.zone == null) {
            "Cannot add '$zone' to location '$this', as zone is already defined!"
        }

        return LocationImpl(coordinate, zone, roadAccess)
    }
    fun requireZone(): Zone = requireNotNull(zone) {
        "Expected Location $this to specify a zone, but found null!"
    }
    fun withRoadAccess(access: RoadAccess): LocationOld {
        require(this.roadAccess == null) {
            "Cannot add '$access' to location '$this', as roadAccess is already defined!"
        }

        return LocationImpl(coordinate, zone, roadAccess = access)
    }
    fun copy(zone: Zone? = null): LocationOld {
        return LocationImpl(coordinate, zone ?: this.zone, roadAccess)
    }

    companion object {
        operator fun invoke(
            coordinate: KCoordinate,
            zone: Zone? = null,
            roadAccess: RoadAccess? = null
        ): LocationOld {
            return LocationImpl(coordinate, zone, roadAccess)
        }
    }
}

interface Location {
    val position: Point
}
fun Location.distance(other: Location): Distance = JTSDistanceCalculator.distance(position, other.position)
interface ZonedLocation: Location {
    val zoneId: ZoneId
}

interface GeographicZonedLocation: ZonedLocation {
    val area: Geometry
}

interface RoadAccessedLocation: Location {
    val roadAccess: RoadAccess
}

object JTSDistanceCalculator {

    private val crsLookup: MutableMap<Int, DistanceUnit?> = mutableMapOf()


    private fun isProjectable(srid: Int): DistanceUnit? {
        val crs = JTSConverter.epsg(srid)

        val axisUnits = crs.coordinateSystem.axisUnits()
        if(axisUnits.size == 1) {
            return when(axisUnits.first()) {
                "Metre" -> DistanceUnit.METERS
                else -> null.also { println("Cannot decode ${axisUnits.first()}") }
            }
        }
        return null
    }
    private val geodeticCalculator by lazy {
        GeodeticCalculator()
    }

    operator fun get(srid: Int) = crsLookup.getOrPut(srid) { isProjectable(srid) }
    fun distance(geom1: Geometry, geom2: Geometry): Distance {
        val distanceUnit = get(geom1.srid)
        if(geom1.srid == geom2.srid && distanceUnit != null) {
            return geom1.distance(geom2).toDistance(distanceUnit)
        }

        val wgsGeom1 = JTSConverter.convertGeometry(geom1, 4326)
        val wgsGeom2 = JTSConverter.convertGeometry(geom2, 4326)

        val distanceOp = DistanceOp(wgsGeom1, wgsGeom2)
        val (p1, p2) = distanceOp.nearestPoints()

        geodeticCalculator.setStartingGeographicPoint(p1.x, p1.y)
        geodeticCalculator.setDestinationGeographicPoint(p2.x, p2.y)

        return geodeticCalculator.orthodromicDistance.meters
    }


}

fun CoordinateSystem.axisUnits(): Set<String> {
    return (0 until dimension).map { this.getAxis(it).unit.name }.toSet()
}

data class LocationImpl(
    override val coordinate: KCoordinate,
    override val zone: Zone?,
    override val roadAccess: RoadAccess?,
) : LocationOld {
    override fun zoneID(): ZoneId? {
        return zone?.id
    }

    override fun toString(): String {
        return "Location(coordinate=$coordinate, zone=${zone?.id?.value}, roadAccess=$roadAccess)"
    }
//    fun requireZone(): Zone = requireNotNull(zone) {
//        "Expected Location $this to specify a zone, but found null!"
//    }

//    fun withZone(zone: Zone): Location {
//        require(this.zone == null) {
//            "Cannot add '$zone' to location '$this', as zone is already defined!"
//        }
//
//        return this.copy(zone = zone)
//    }

//    fun withRoadAccess(access: RoadAccess): Location {
//        require(this.roadAccess == null) {
//            "Cannot add '$access' to location '$this', as roadAccess is already defined!"
//        }
//
//        return this.copy(roadAccess = access)
//    }
}

/**
 * Parse the legacy mobiTopp String format of coordinate and road access:
 *
 * @return the parsed Location
 */
@Suppress("MagicNumber")
fun String.parseRoadPosition(): LocationOld {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    require(res.size == 4) {
        "Cannot parse '$this' as RoadPosition: expected format LONG:LAT,ROAD_ID,ROAD_POS"
    }

    return LocationOld(
        coordinate = WGS84Coordinate.decimalDegree(res[1].toDouble(), res[0].toDouble()),
        zone = null,
        roadAccess = RoadAccess(
            roadId = res[2].toLongOrNull() ?: Long.MIN_VALUE,
            position = (res[3].toDoubleOrNull() ?: 0.5).share(),
        )
    )
}

val LOCATIONUNKNOWN = LocationOld(
    coordinate = WGS84Coordinate.decimalDegree(0.0, 0.0),
    zone = null,
    roadAccess = null,
)

fun KCoordinate.toUTM(): UTMPosition {
    return WGS84Coordinate.decimalDegree(y, x).toUTM()
}
