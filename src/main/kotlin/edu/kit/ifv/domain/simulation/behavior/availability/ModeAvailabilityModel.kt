@file:Suppress("FunctionNameMaxLength")

package edu.kit.ifv.domain.simulation.behavior.availability
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.DestinationAlternative
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * ModeAvailabilityFilter is a [ChoiceFilter] for [edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceAlternative]s
 * that additionally breaks down availability into three parts:
 *  - static availability refers to the modes generally available to a person
 *    based on their attributes like age, license, etc.
 *  - provider availability refers to the modes available for a person in their current situation
 *    including abstract availability information of resource provides (s.a. sharing stations, drt provider)
 *    but not yet considering availability of single resources.
 *    This also determines which mode related (shared) resource providers might be affected that need to be locked
 *    by mutex before checking the availability of individual resources.
 *  - resource availability refers to modes available in a choice situation defined by [edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics]
 *    with a selected destination considering the current availability of resources
 *    e.g., filtering out bike sharing if there are no vehicles available.
 *    This should only be called if mutex-lock for resource provider was acquired!
 *
 * This distinction allows to reuse the model at different stages of decision-making.
 * The three stages should be implemented building on top of each other: resource > uses > provider > uses > static.
 * Also, static availability could be precomputed per person and provider availability per choice
 * situation/characteristics to reduce computation time.
 *
 */
interface ModeAvailabilityModel {
    /**
     * Converts this model to a [ChoiceFilter] for [IPerson] that checks static availability.
     * @return a choice filter for static availability
     */
    fun asStaticAvailabilityFilter() = ChoiceFilter<Mode, IPerson> { mode ->
        staticAvailability(mode, contextOf<IPerson>())
    }

    /**
     * Converts this model to a [ChoiceFilter] for [DestinationAlternative] that checks provider availability.
     * @return a choice filter for provider availability
     */
    fun asProviderAvailabilityFilter() = ChoiceFilter<Mode, DestinationAlternative> { mode ->
        val destinationAlternative = contextOf<DestinationAlternative>()
        providerAvailability(
            mode,
            destinationAlternative.person,
            destinationAlternative.time,
            destinationAlternative.choice,
        ).isAvailable
    }

    /**
     * Converts this model to a [ChoiceFilter] for [ModeChoiceCharacteristics] that checks resource availability.
     * @return a choice filter for resource availability
     */
    fun asResourceAvailabilityFilter() = ChoiceFilter<Mode, ModeChoiceCharacteristics> { mode ->
        resourceAvailability(mode, contextOf<ModeChoiceCharacteristics>())?.let { true } ?: false
    }

    /**
     * Computes static availability of the given mode for the person provided as context:
     * i.e., whether the mode is generally available to a person based on their attributes like age, license, etc.
     *
     * @receiver person the person to compute the static mode availability for
     * @param mode the mode to be checked for static availability
     * @return whether the [mode] is statically available to the person
     */
    fun staticAvailability(mode: Mode, person: IPerson): Boolean

    /**
     * Computes the provider availability (availability and possibly affected shared resource providers)
     * of the given [mode] in the current choice situation.
     *
     * @param mode the mode for which to check availability and affected resources
     * @param agent the person to check provider availability for
     * @param time the time of the choice situation
     * @param destination the potential destination for the mode choice
     * @return [ProviderAvailability] containing availability status and affected resource providers
     */
    fun providerAvailability(
        mode: Mode,
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation,
    ): ProviderAvailability

    /**
     * Checks the availability of a given mode choice alternative
     * in the context of a choice situation defined by [ModeChoiceCharacteristics].
     *
     * @param mode the mode to be checked for availability
     * @param characteristics the characteristics of the mode choice situation
     * @return [ModeResource] if the mode is available, `null` otherwise
     */
    fun resourceAvailability(mode: Mode, characteristics: ModeChoiceCharacteristics): ModeResource?
}
