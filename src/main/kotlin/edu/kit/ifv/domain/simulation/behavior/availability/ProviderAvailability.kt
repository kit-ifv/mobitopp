package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * Represents the provider availability of a transport [mode].
 *
 * @property mode the transport mode
 * @property providers the collection of resource providers (e.g., sharing stations, DRT providers) that are affected
 */
data class ProviderAvailability(val mode: Mode, val providers: Collection<Any>? = null) {
    /** True if the mode is available from at least one provider. */
    val isAvailable: Boolean = (providers != null)
    /** True if the mode is not available. */
    val isNotAvailable: Boolean = (providers == null)
}

/** Returns a [ProviderAvailability] indicating that the mode is not available. */
val Mode.notAvailable get() = ProviderAvailability(this, null)
/** Creates a [ProviderAvailability] indicating that the mode is available with the given [providers]. */
fun Mode.available(providers: Collection<Any> = emptyList()) = ProviderAvailability(this, providers)
/** Creates a [ProviderAvailability] indicating that the mode is available with the given [providers]. */
fun Mode.available(vararg providers: Any) = ProviderAvailability(this, providers.toSet())

/**
 * Flattens a collection of [ProviderAvailability] into a pair of modes and their unique resource providers.
 * @return a pair where the first element is the collection of modes and the second is the unique resource providers
 */
fun Collection<ProviderAvailability>.flatten() = mapNotNull {
    it.providers?.let { provider -> it.mode to provider }
}.run {
    val modes = map { it.first }
    val resources = flatMap { it.second }.toSet() // ensure uniqueness
    modes to resources
}

/**
 * Retrieves all currently affected resource providers for the given [modes] in a specific choice situation.
 *
 * @param modes the modes to check
 * @param agent the person making the choice
 * @param time the time of the choice
 * @param destination the potential destination
 * @return a list of affected resource providers
 */
fun ModeAvailabilityModel.currentlyAffectedProviders(
    modes: Collection<Mode>,
    agent: PersonAgent,
    time: AbsoluteTime,
    destination: StandardLocation,
): List<Any> = modes.map { providerAvailability(it, agent, time, destination) }.flatten().second.toList()
