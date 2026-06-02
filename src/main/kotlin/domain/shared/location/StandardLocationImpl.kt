package domain.shared.location

import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.toSizebasedClassification
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import org.locationtech.jts.geom.Point

data class StandardLocationImpl constructor(
    override val position: Point,
    val zone: Zone<HasRegionType>,
    val roadAccess: RoadAccess
) : StandardLocation {

    override val attributes: StandardLocationAttributes = ZoneDerivedLocationAttributes(zone)

    override fun toString(): String = "${position.x},${position.y},${zone.id}"
}
