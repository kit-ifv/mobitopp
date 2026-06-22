package edu.kit.ifv.domain.shared.location.zone
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId

interface Zone<out T> : HasZoneId {
    val attributes: T
    val centroidLocation: StandardLocation
    operator fun contains(location: HasZoneId): Boolean = zoneId == location.zoneId
}
