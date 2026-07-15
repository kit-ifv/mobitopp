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

private class ModeRuleLookup(val lookup: Array<AvailabilityRule?>, private val codeOffset: Int) {

    operator fun get(mode: Mode): AvailabilityRule = requireNotNull(lookup[mode.code - codeOffset]) {
        "No rule defined for mode $mode (at index ${mode.code - codeOffset}).\n" +
            "Lookup table: ${lookup.withIndex().associate { it.index to it.value }}}"
    }

    companion object {
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
