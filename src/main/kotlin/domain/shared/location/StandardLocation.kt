package domain.shared.location

import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.attributes.StandardLocationAttributes
import domain.shared.location.road.RoadAccess
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.attributes.HasZoneId
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

/**
 * The bog-standard location used in mobitopp. That means we know the zone,  and a RoadAccess.
 */

interface StandardLocation :
    Location<StandardLocationAttributes>,
    HasZoneId {
    override val zoneId get() = attributes.zoneId
    val regionType get() = attributes.regionType

    fun toRecord(): ZonedRoadAccessLocationRecord = ZonedRoadAccessLocationRecord(
        this.zoneId,
        this.attributes.roadAccess,
        this.position,
    )

    companion object {
        @Suppress("MagicNumber")
        private val invalidPoint = object : Point(Coordinate(0.0, .0), PrecisionModel(), 4326) {}

        private val invalidZone: Zone<HasRegionType> = object : Zone<HasRegionType> {
            override val zoneId: ZoneId = ZoneId(-1L)
            override val attributes: HasRegionType = object : HasRegionType {
                override val regionType: RegionType = RegioStaR17.LARGE_CITY_METRO
            }
            override val centroidLocation: StandardLocation
                get() = error("Accessing centroid on unknown location should not work")
        }

        operator fun invoke(position: Point, zone: Zone<HasRegionType>, roadAccess: RoadAccess): StandardLocation =
            StandardLocationImpl(position, zone, roadAccess)

        val LOCATIONUNKNOWN = StandardLocation(
            invalidPoint,
            invalidZone,
            RoadAccess.INVALID,
        )

        fun fromId(zoneId: Number) = StandardLocation(
            invalidPoint,
            zoneLookup.getOrPut(ZoneId(zoneId.toLong())) {
                invalidZone
            },
            RoadAccess.INVALID,
        )

        fun fromPoint(point: Point) = StandardLocation(point, invalidZone, RoadAccess.INVALID)

        private val zoneLookup: MutableMap<ZoneId, Zone<HasRegionType>> = mutableMapOf()
    }
}
