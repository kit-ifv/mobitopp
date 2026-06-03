package domain.shared.location

import domain.shared.location.attributes.StandardLocationAttributes
import domain.shared.location.attributes.ZoneDerivedLocationAttributes
import domain.shared.location.road.RoadAccess
import domain.shared.location.zone.Zone
import domain.shared.location.zone.attributes.HasRegionType
import org.locationtech.jts.geom.Point

data class StandardLocationImpl constructor(
    override val position: Point,
    val zone: Zone<HasRegionType>,
    val roadAccess: RoadAccess = RoadAccess.INVALID,
) : StandardLocation {

    override val attributes: StandardLocationAttributes = ZoneDerivedLocationAttributes(zone, roadAccess)

    override fun toString(): String = "${position.x},${position.y},${zone.zoneId}"
}
