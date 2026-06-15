package edu.kit.ifv.domain.shared.location.zone.attributes
import edu.kit.ifv.domain.shared.location.road.RoadAccess
@Deprecated("Road Access is an attribute of location and not the zone")
interface HasRoadAccess {
    val roadAccess: RoadAccess
}
