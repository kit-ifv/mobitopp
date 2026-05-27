package domain.shared.location.attributes

import domain.shared.location.ZoneId
import utils.Identifiable

interface HasZoneId :  Identifiable<ZoneId> {
    @Deprecated("Use zoneId to avoid ambiguity")
    override val id: ZoneId get() = zoneId
    val zoneId: ZoneId
}
