package edu.kit.ifv.domain.shared.location
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import org.locationtech.jts.geom.Point

data class ZonedRoadAccessLocationRecord(val zoneId: ZoneId, val roadAccess: RoadAccess, val position: Point)
