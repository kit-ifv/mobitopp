package domain.shared.location.attributes

import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.ZoneId
import domain.shared.location.ZonedRoadAccessLocation
import domain.shared.location.ZonedRoadAccessLocationImpl

interface HasZoneID : Location {
    val zoneID: ZoneId

    override fun withRoadAccess(access: RoadAccess): ZonedRoadAccessLocation {
        return ZonedRoadAccessLocationImpl(position, zoneID, access)
    }
}
