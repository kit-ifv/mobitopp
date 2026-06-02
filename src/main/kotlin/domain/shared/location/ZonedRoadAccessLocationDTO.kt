package domain.shared.location

import domain.shared.location.zone.ZoneId
import org.locationtech.jts.geom.Point

data class ZonedRoadAccessLocationDTO(val zoneId: ZoneId, val roadAccess: RoadAccess, val position: Point)
