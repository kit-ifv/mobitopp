package edu.kit.ifv.domain.simulation.behavior.availability.rules

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.availability.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.ProviderAvailability
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * Implementation of [ModeAvailabilityModel] that uses a map of [AvailabilityRule]s to determine availability.
 *
 * @property lookup a lookup from transport mode to its corresponding [AvailabilityRule]
 */
class ModeAvailabilityByRule(ruleByMode: Map<Mode, AvailabilityRule>) : ModeAvailabilityModel {

    private val lookup = ModeRuleLookup.from(ruleByMode)

    override fun staticAvailability(mode: Mode, person: IPerson): Boolean = lookup[mode].staticAvailability(person)

    override fun providerAvailability(
        mode: Mode,
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation,
    ): ProviderAvailability = lookup[mode].providerAvailability(agent, time, destination)

    override fun resourceAvailability(mode: Mode, characteristics: ModeChoiceCharacteristics): ModeResource? =
        lookup[mode].resourceAvailability(characteristics)
}

/**
 * Internal lookup table for [AvailabilityRule]s optimized for performance using an array.
 *
 * Instead of a map, this class uses an array where the index is derived from the [Mode.code].
 * To handle cases where mode codes do not start at zero, a [codeOffset] is subtracted from the mode's code.
 *
 * @property lookup the array containing the rules, indexed by `mode.code - codeOffset`
 * @property codeOffset the minimum mode code used as an offset for array indexing
 */
private class ModeRuleLookup(val lookup: Array<AvailabilityRule?>, private val codeOffset: Int) {

    /**
     * Retrieves the [AvailabilityRule] for the given [mode].
     * @param mode the transport mode to look up
     * @return the corresponding availability rule
     * @throws IllegalArgumentException if no rule is defined for the given mode
     */
    operator fun get(mode: Mode): AvailabilityRule = requireNotNull(lookup[mode.code - codeOffset]) {
        "No rule defined for mode $mode (at index ${mode.code - codeOffset}).\n" +
            "Lookup table: ${lookup.withIndex().associate { it.index to it.value }}}"
    }

    companion object {
        /**
         * Creates a [ModeRuleLookup] from a map of rules.
         *
         * The function computes the [codeOffset] and array size as follows:
         * 1. It finds the minimum and maximum [Mode.code] among the provided modes.
         * 2. The [codeOffset] is set to the `minCode`.
         * 3. The array size is calculated as `maxCode - minCode + 1` to cover the entire range of codes.
         * 4. Each rule is placed in the array at `index = mode.code - minCode`.
         *
         * This ensures a compact array if mode codes are mostly contiguous, while providing O(1) lookup.
         *
         * @param ruleByMode a map from [Mode] to its corresponding [AvailabilityRule]
         * @return a configured [ModeRuleLookup]
         * @throws IllegalArgumentException if [ruleByMode] is empty
         */
        fun from(ruleByMode: Map<Mode, AvailabilityRule>): ModeRuleLookup {
            require(ruleByMode.isNotEmpty()) {
                "Cannot create a mode -> rule lookup from an empty map!"
            }

            val maxCode = ruleByMode.keys.maxOfOrNull { it.code } ?: 0
            val minCode = ruleByMode.keys.minOfOrNull { it.code } ?: 0
            val range = maxCode - minCode + 1

            val ruleByIndex = ruleByMode.mapKeys { it.key.code - minCode }

            val values = Array<AvailabilityRule?>(range) { index ->
                ruleByIndex[index]
            }

            return ModeRuleLookup(values, minCode)
        }
    }
}
