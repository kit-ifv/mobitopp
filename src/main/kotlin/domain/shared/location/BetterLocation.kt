package domain.shared.location

import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.SizebasedRegiostarClassification
import org.locationtech.jts.geom.Point

data class BetterLocation(
    override val position: Point,
    override val zoneID: ZoneId,
    override val roadAccess: RoadAccess,
    override val regionType: RegionType,
    override val sizebasedRegiostarClassification: SizebasedRegiostarClassification,
) : StandardLocation {

    companion object {
        fun fromPoint(point: Point) = BetterLocation(
            position = point,
            zoneID = ZoneId(-1),
            roadAccess = RoadAccess.INVALID,
            regionType = RegioStaR17.MEDIUM_CITY_METRO,
            sizebasedRegiostarClassification = SizebasedRegiostarClassification.CITY,
        )

        fun wgs(x: Double, y: Double): BetterLocation {
            return fromPoint(PointCreator.createWGS(x, y))
        }
    }
}