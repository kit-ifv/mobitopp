package domain.shared.location.attributes

import domain.shared.location.road.RoadAccess

interface HasRoadAccess {
    val roadAccess: RoadAccess
}
