package domain.shared.location

import domain.shared.location.zone.ZoneId
import org.locationtech.jts.geom.Point

data class ZonedRoadAccessLocationImpl(
    override val position: Point,
    override val zoneId: ZoneId,
    override val roadAccess: RoadAccess,
) : ZonedRoadAccessLocation
