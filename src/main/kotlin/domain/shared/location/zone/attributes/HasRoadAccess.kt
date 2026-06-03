package domain.shared.location.zone.attributes

import domain.shared.location.road.RoadAccess

interface HasRoadAccess {
    val roadAccess: RoadAccess
}
