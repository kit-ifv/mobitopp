package domain.shared.location

import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.toSizebasedClassification
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.zone.Zone
import org.locationtech.jts.geom.Point

data class StandardLocationImpl constructor(
    override val position: Point,
    val zone: Zone<HasRegionType>,
    override val roadAccess: RoadAccess,
) : StandardLocation {
    override val zoneId: ZoneId get() = zone.id

    override val regionType: RegionType
        get() = zone.attributes.regionType

    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification
        get() = regionType.toRegioStaR17().toSizebasedClassification()

    override fun toString(): String = "${position.x},${position.y},${zone.id}"
}
