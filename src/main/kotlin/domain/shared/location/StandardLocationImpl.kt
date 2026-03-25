package domain.shared.location

import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.toSizebasedClassification
import org.locationtech.jts.geom.Point

data class StandardLocationImpl constructor(
    override val position: Point,
    val zone: Zone,
    override val roadAccess: RoadAccess,
) : StandardLocation {
    override val zoneID: ZoneId get() = zone.id

    override val regionType: RegionType
        get() = zone.regionType

    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification
        get() = regionType.toRegioStaR17().toSizebasedClassification()


    companion object {


    }

    override fun toString(): String {
        return "${position.x},${position.y},${zone.id}"
    }
}