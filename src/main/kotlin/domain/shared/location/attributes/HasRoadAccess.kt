package domain.shared.location.attributes

import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.ZoneId
import domain.shared.location.ZonedRoadAccessLocation
import domain.shared.location.ZonedRoadAccessLocationImpl

interface HasRoadAccess : Location {
    val roadAccess: RoadAccess

    override fun withZone(zoneId: ZoneId): ZonedRoadAccessLocation =
        ZonedRoadAccessLocationImpl(position, zoneId, roadAccess)
}
