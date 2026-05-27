package domain.shared.location

import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.attributes.HasSizebasedClassification
import domain.shared.location.zone.Zone
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.WGS84Coordinate
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
@Suppress("MagicNumber")
private val invalidPoint = object : Point(Coordinate(0.0, .0), PrecisionModel(), 4326) {
}

@Suppress("UnusedPrivateClass")
private class ZoneMock(zoneId: ZoneId, location: Location = LocationImpl(invalidPoint), seed: Long = -1) :
    DeprecatedZone(zoneId, location, seed) {
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

/**
 * The bog-standard location used in mobitopp. That means we know the zone,  and a RoadAccess.
 */

interface StandardLocation :
    ZonedRoadAccessLocation,
    HasRegionType,
    HasSizebasedClassification {

    companion object {

        private val invalidZone: Zone<HasRegionType> = object : Zone<HasRegionType> {
            override val id: ZoneId = ZoneId(-1L)
            override val attributes: HasRegionType = object : HasRegionType {
                override val regionType: RegionType = RegioStaR17.LARGE_CITY_METRO
            }
        }

        operator fun invoke(position: Point, zone: Zone<HasRegionType>, roadAccess: RoadAccess): StandardLocation =
            StandardLocationImpl(position, zone, roadAccess)

        val LOCATIONUNKNOWN = StandardLocation(
            invalidPoint,
            invalidZone,
            RoadAccess.INVALID,
        )

        fun fromWGS(wgsCoord: WGS84Coordinate): StandardLocation {
            val point = wgsCoord.toPoint()
            return fromPoint(point)
        }

        fun fromId(zoneId: Number) = StandardLocation(
            invalidPoint,
            zoneLookup.getOrPut(ZoneId(zoneId.toLong())) {
                invalidZone
            },
            RoadAccess.INVALID,
        )

        fun fromWGS(x: Double, y: Double) = fromWGS(WGS84Coordinate.Companion.decimalDegree(x, y))

        fun fromPoint(point: Point) = StandardLocation(point, invalidZone, RoadAccess.INVALID)

        private val zoneLookup: MutableMap<ZoneId, Zone<HasRegionType>> = mutableMapOf()
    }
}
