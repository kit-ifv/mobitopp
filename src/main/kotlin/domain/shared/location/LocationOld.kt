package domain.shared.location

import JTSConverter
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.toSizebasedClassification
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.KCoordinate
import edu.kit.ifv.units.UTMPosition
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import edu.kit.ifv.units.toDistance
import org.geotools.api.referencing.cs.CoordinateSystem
import org.geotools.referencing.GeodeticCalculator
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.operation.distance.DistanceOp

data class RoadAccess(val roadId: Long, val position: UnitIntervalValue, val lateralDistance: Distance = 0.meters) {
    companion object {
        val INVALID = RoadAccess(Long.MIN_VALUE, 0.5.share())
    }
}

interface LocationOld {
    val coordinate: KCoordinate
    val zone: Zone?
    val roadAccess: RoadAccess?
    fun regionType(): RegionType = requireZone().regionType
    fun zoneID(): ZoneId?
    fun inSameZone(other: LocationOld) = this.zone == other.zone
    fun withZone(zone: Zone): LocationImplOld {
        require(this.zone == null) {
            "Cannot add '$zone' to location '$this', as zone is already defined!"
        }

        return LocationImplOld(coordinate, zone, roadAccess)
    }

    fun requireZone(): Zone = requireNotNull(zone) {
        "Expected Location $this to specify a zone, but found null!"
    }

    fun withRoadAccess(access: RoadAccess): LocationOld {
        require(this.roadAccess == null) {
            "Cannot add '$access' to location '$this', as roadAccess is already defined!"
        }

        return LocationImplOld(coordinate, zone, roadAccess = access)
    }

    fun copy(zone: Zone? = null): LocationOld {
        return LocationImplOld(coordinate, zone ?: this.zone, roadAccess)
    }

    companion object {
        operator fun invoke(
            coordinate: KCoordinate,
            zone: Zone? = null,
            roadAccess: RoadAccess? = null,
        ): LocationOld {
            return LocationImplOld(coordinate, zone, roadAccess)
        }
    }
}


interface Location {
    val position: Point

    fun withZone(zoneId: ZoneId): HasZone {
        return ZoneIDLocation(position, zoneId)
    }

    fun withRoadAccess(access: RoadAccess): HasRoadAccess {
        return RoadAccessLocationImpl(position, access)
    }

    fun distance(other: Location): Distance = JTSDistanceCalculator.distance(position, other.position)

    companion object {
        fun of(point: Point): Location {
            return LocationImpl(point)
        }

        fun utm(x: Double, y: Double): Location {
            return of(GeometryFactory(PrecisionModel(), 25832).createPoint(Coordinate(x, y)))
        }

        fun utm(string: String): Location {
            val (x, y) = string.split(",").take(2)
            return utm(x.toDouble(), y.toDouble())

        }

        fun wgs(coord: WGS84Coordinate) = wgs(coord.x, coord.y)
        fun wgs(x: Double, y: Double): Location {
            return of(PointCreator.createWGS(x, y))
        }

        val BIELEFELD by lazy {
            wgs(8.531007, 52.019101)
        }
    }
}

fun WGS84Coordinate.toPoint(): Point {
    return GeometryFactory(PrecisionModel(), 4326).createPoint(Coordinate(x, y))
}

object PointCreator {
    private val wgsFactory = GeometryFactory(PrecisionModel(), 4326)
    private val utmFactory = GeometryFactory(PrecisionModel(), 28532)
    fun createWGS(coord: KCoordinate) = createWGS(coord.x, coord.y)

    fun createWGS(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createWGS(x.toDouble(), y.toDouble())
    }

    fun createWGS(x: Double, y: Double): Point {
        return wgsFactory.createPoint(Coordinate(x, y))
    }

    fun createUTM(x: Double, y: Double): Point {
        return utmFactory.createPoint(Coordinate(x, y))
    }

    fun createUTM(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createUTM(x.toDouble(), y.toDouble())
    }
}


data class LocationImpl(override val position: Point) : Location
interface HasZone : Location {
    val zoneID: ZoneId

    override fun withRoadAccess(access: RoadAccess): ZonedRoadAccessLocation {
        return ZonedRoadAccessLocationImpl(position, zoneID, access)
    }
}

data class ZonedRoadAccessLocationImpl(
    override val position: Point,
    override val zoneID: ZoneId,
    override val roadAccess: RoadAccess,

): ZonedRoadAccessLocation
/**
 * The bog-standard location used in mobitopp. That means we know the zone,  and a RoadAccess.
 */
data class StandardLocation(
    override val position: Point,
    val zone: Zone,
    override val roadAccess: RoadAccess,
) : ZonedRoadAccessLocation, HasRegionType, HasSizebasedClassification {
    override val zoneID: ZoneId get() = zone.id

    override val regionType: RegionType
        get() = zone.regionType

    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification
        get() = regionType.toRegioStaR17().toSizebasedClassification()


    companion object {
        private val invalidPoint = object : Point(Coordinate(0.0, .0), PrecisionModel(), 4326) {

        }

        private class ZoneMock(
            zoneId: ZoneId,
            location: Location = LocationImpl(invalidPoint),
            seed: Long = -1,
        ) : Zone(zoneId, location, seed) {
            constructor(number: Number) : this(ZoneId(number.toLong()))

            override val visumId: Long
                get() = throw NoSuchElementException("Cannot get visum Id on unknown Location")
            override val name: String
                get() = throw NoSuchElementException("Cannot get name on unknown Location")
            override val regionType: RegionType
                get() = throw NoSuchElementException("Cannot get regionType on unknown Location")
            override val classification: ZoneClassification
                get() = throw NoSuchElementException("Cannot get classification on unknown Location")
            override val parkingPlaces: Int
                get() = throw NoSuchElementException("Cannot get parkingPlaces on unknown Location")
            override val isDestination: Boolean
                get() = throw NoSuchElementException("Cannot get isDestination on unknown Location")
            override val relief: Distance
                get() = throw NoSuchElementException("Cannot get relief on unknown Location")
        }
        private val zoneLookup: MutableMap<ZoneId, ZoneMock> = mutableMapOf()
        private val invalidZone: Zone = ZoneMock(ZoneId(-1))
        val LOCATIONUNKNOWN = StandardLocation(
            invalidPoint,
            invalidZone,
            RoadAccess.INVALID
        )

        fun fromWGS(wgsCoord: WGS84Coordinate): StandardLocation {
            val point = wgsCoord.toPoint()
            return fromPoint(point)


        }
        fun fromID(zoneID: Number) = StandardLocation(invalidPoint, zoneLookup.getOrPut(ZoneId(zoneID.toLong())) {
            ZoneMock(zoneID)
        }, RoadAccess.INVALID)
        fun fromWGS(x: Double, y: Double) = fromWGS(WGS84Coordinate.decimalDegree(x, y))

        fun fromPoint(point: Point) = StandardLocation(point, invalidZone, RoadAccess.INVALID)
    }
}
data class ZoneIDLocation(
    override val position: Point,
    override val zoneID: ZoneId,
): HasZone


data class ZoneLocation(
    val zone: Zone,
    override val position: Point,
) : HasZone {
    override val zoneID: ZoneId get() = zone.id
}

interface HasRegionType : Location {
    val regionType: RegionType
}

interface HasRoadAccess : Location {
    val roadAccess: RoadAccess

    override fun withZone(zoneId: ZoneId): ZonedRoadAccessLocation {
        return ZonedRoadAccessLocationImpl(position, zoneId, roadAccess)
    }
}

data class RoadAccessLocationImpl(
    override val position: Point,
    override val roadAccess: RoadAccess,
): HasRoadAccess

interface HasSizebasedClassification : Location {
    val sizebasedRegiostarClassification: SizebasedRegiostarClassification
}

interface ZonedRoadAccessLocation : HasRoadAccess, HasZone


interface ZonedLocation : Location {
    val zoneId: ZoneId
}

class ImprovedZone<T>(
    private val area: Geometry,
    override val zoneId: ZoneId,
    bonusInfo: T,
) : ZonedLocation {
    init {
        area.userData = bonusInfo
    }

    val info get() = area.userData as T
    override val position: Point
        get() = area.centroid
}

interface GeographicZonedLocation : ZonedLocation {
    val area: Geometry
}

interface RoadAccessedLocation : Location {
    val roadAccess: RoadAccess
}

object JTSDistanceCalculator {

    private val crsLookup: MutableMap<Int, DistanceUnit?> = mutableMapOf()


    private fun isProjectable(srid: Int): DistanceUnit? {
        val crs = JTSConverter.epsg(srid)

        val axisUnits = crs.coordinateSystem.axisUnits()
        if (axisUnits.size == 1) {
            return when (axisUnits.first()) {
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
        if (geom1.srid == geom2.srid && distanceUnit != null) {
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

    // Is supposed to take care of projectioin, and checking which srid is present etc.
    fun randomPoint(input: Point, distance: Distance): Point {
        return TODO()
    }


}

fun Point.randomPoint(distance: Distance = 100.meters): Point {
    return JTSDistanceCalculator.randomPoint(this, distance)
}

fun CoordinateSystem.axisUnits(): Set<String> {
    return (0 until dimension).map { this.getAxis(it).unit.name }.toSet()
}

data class LocationImplOld(
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
fun String.parseRoadPositionWGS(): HasRoadAccess {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    require(res.size == 4) {
        "Cannot parse '$this' as RoadPosition: expected format LONG:LAT,ROAD_ID,ROAD_POS"
    }
    return Location.wgs(
        res[0].toDouble(),
        res[1].toDouble()
    ).withRoadAccess(
        RoadAccess(
            roadId = res[2].toLongOrNull() ?: Long.MIN_VALUE,
            position = (res[3].toDoubleOrNull() ?: 0.5).share(),
        )
    )
}

val LOCATIONUNKNOWN: StandardLocation get() = StandardLocation.LOCATIONUNKNOWN

fun KCoordinate.toUTM(): UTMPosition {
    return WGS84Coordinate.decimalDegree(y, x).toUTM()
}
