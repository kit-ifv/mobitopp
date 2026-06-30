package edu.kit.ifv.domain.shared.location.attributes
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.areatype.SizebasedRegiostarClassification
import edu.kit.ifv.domain.shared.enums.areatype.toSizebasedClassification
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType

class ZoneDerivedLocationAttributes<Z>(private val zone: Zone<Z>, override val roadAccess: RoadAccess) :
    StandardLocationAttributes where Z :
          HasRegionType {
    override val regionType: RegionType get() = zone.attributes.regionType
    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification
        get() = zone.attributes
            .regionType.toRegioStaR17().toSizebasedClassification()
    override val zoneId: ZoneId
        get() = zone.zoneId
}
