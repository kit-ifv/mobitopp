package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.utils.units.AbsoluteTime

data class ProviderAvailability(val mode: Mode, val providers: Collection<Any>? = null) {
    val isAvailable: Boolean = (providers != null)
    val isNotAvailable: Boolean = (providers == null)
}

val Mode.notAvailable get() = ProviderAvailability(this, null)
fun Mode.available(resources: Collection<Any> = emptyList()) = ProviderAvailability(this, resources)
fun Mode.available(vararg resources: Any) = ProviderAvailability(this, resources.toSet())

fun Collection<ProviderAvailability>.flatten() = mapNotNull {
    it.providers?.let { provider -> it.mode to provider }
}.run {
    val modes = map { it.first }
    val resources = flatMap { it.second }
    modes to resources
}

fun ModeAvailabilityModel.currentlyAffectedProviders(
    modes: Collection<Mode>,
    agent: PersonAgent,
    time: AbsoluteTime,
    destination: StandardLocation,
): List<Any> = modes.mapNotNull { providerAvailability(it, agent, time, destination).providers }.flatten()
