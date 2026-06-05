package edu.kit.ifv.domain.simulation.data.drt
import Mutable
import edu.kit.ifv.domain.shared.location.zone.ZoneId

@Mutable
abstract class DrtProviderData(final override val id: DrtProviderId) : DrtProvider {

    abstract val initVehicles: Map<ZoneId, Int>
}
