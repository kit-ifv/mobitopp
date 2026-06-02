package domain.shared.location.zone

import domain.shared.location.StandardLocation
import domain.shared.location.zone.attributes.HasZoneId

interface Zone<out T> : HasZoneId {
    val attributes: T
    val centroidLocation: StandardLocation
    operator fun contains(location: HasZoneId): Boolean = zoneId == location.zoneId
}

