package edu.kit.ifv.domain.simulation.behavior.availability.rules

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.ProviderAvailability
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * Represents a rule set for checking the availability of a transport [mode] at different stages.
 */
interface AvailabilityRule {
    /** The transport mode this rule set applies to. */
    val mode: Mode

    /**
     * Checks whether the mode is generally available to a person based on their attributes (e.g., age, license).
     * @param person the person to check
     * @return true if statically available
     */
    fun staticAvailability(person: IPerson): Boolean

    /**
     * Checks whether the [mode] is available in the agent's current situation
     * and additionally computes which resource providers can be asked for a mode resource.
     * @param agent the person making the choice
     * @param time the time of the choice
     * @param destination the potential destination
     * @return [ProviderAvailability] containing availability status and affected resource providers
     */
    fun providerAvailability(
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation,
    ): ProviderAvailability

    /**
     * Checks the dynamic availability of a specific resource (e.g., a vehicle) for the mode.
     * When using shared memory parallel simulation this must be called in a protected code block!
     * @param characteristics the characteristics of the mode choice situation
     * @return [ModeResource] if available, `null` otherwise
     */
    fun resourceAvailability(characteristics: ModeChoiceCharacteristics): ModeResource?
}
