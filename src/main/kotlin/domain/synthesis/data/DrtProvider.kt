package domain.synthesis.data

import Mutable
import domain.shared.enums.Mode
import domain.shared.location.zone.ZoneId
import utils.Identifiable

@JvmInline
value class DrtProviderId(val value: Long)

// TODO remove I from all domain entity interfaces, use pattern as in this file:
// interface = entity name
// mutable long term data class = entity name + "Data"
// dynamic agent class = entity name + "Agent"
interface DrtProvider : Identifiable<DrtProviderId> {
    val name: String
    val mode: Mode
    val serviceArea: List<ZoneId>
    val operatingHours: IntRange // TODO more detailed hours
}

@Mutable
abstract class DrtProviderData(final override val id: DrtProviderId) : DrtProvider {

    abstract val initVehicles: Map<ZoneId, Int>
}
