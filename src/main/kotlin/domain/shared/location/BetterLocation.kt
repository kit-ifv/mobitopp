package domain.shared.location

import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.areatype.toSizebasedClassification
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.zone.Zone
import org.locationtech.jts.geom.Point

data class BetterLocation constructor(
    override val position: Point,
    override val zoneId: ZoneId,
    override val roadAccess: RoadAccess,
    override val regionType: RegionType,
    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification = regionType.toRegioStaR17().toSizebasedClassification(),
) : StandardLocation {
    constructor(position: Point, zone: Zone<HasRegionType>, roadAccess: RoadAccess) : this(position, zone.id, roadAccess, zone.attributes.regionType)

    companion object {
        fun fromPoint(point: Point) = BetterLocation(
            position = point,
            zoneId = ZoneId(-1),
            roadAccess = RoadAccess.INVALID,
            regionType = RegioStaR17.MEDIUM_CITY_METRO,
            sizebasedRegiostarClassification = SizebasedRegiostarClassification.CITY,
        )

        fun wgs(x: Double, y: Double): BetterLocation {
            return fromPoint(PointCreator.createWGS(x, y))
        }
    }
}
