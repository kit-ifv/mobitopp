package domain.synthesis.data

import Mutable
import domain.shared.enums.Mode
import domain.shared.location.zone.ZoneId
import utils.Identifiable

@JvmInline
value class DrtProviderId(val value: Long)

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
