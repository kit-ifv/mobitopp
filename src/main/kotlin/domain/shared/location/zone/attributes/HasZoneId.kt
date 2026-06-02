package domain.shared.location.zone.attributes

import domain.shared.location.zone.ZoneId
import utils.Identifiable

interface HasZoneId : Identifiable<ZoneId> {
    @Deprecated("Use zoneId to avoid ambiguity")
    override val id: ZoneId get() = zoneId
    val zoneId: ZoneId
}
