package domain.shared.location.attributes

import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.ZoneId
import domain.shared.location.ZonedRoadAccessLocation
import domain.shared.location.ZonedRoadAccessLocationImpl
import utils.Identifiable

interface HasZoneId : Location , Identifiable<ZoneId>{
    val zoneId: ZoneId
    override val id: ZoneId
        get() = zoneId
    override fun withRoadAccess(access: RoadAccess): ZonedRoadAccessLocation {
        return ZonedRoadAccessLocationImpl(position, zoneId, access)
    }
}
