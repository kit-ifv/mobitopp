package edu.kit.ifv.domain.shared.location.zone.attributes
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.utils.Identifiable

interface HasZoneId : Identifiable<ZoneId> {
    @Deprecated("Use zoneId to avoid ambiguity")
    override val id: ZoneId get() = zoneId
    val zoneId: ZoneId
}
