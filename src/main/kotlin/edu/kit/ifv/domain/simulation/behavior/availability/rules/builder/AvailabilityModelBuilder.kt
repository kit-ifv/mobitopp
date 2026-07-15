package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.behavior.availability.rules.ModeAvailabilityByRule

fun availabilityRules(block: AvailabilityByRuleBuilder.() -> Unit): ModeAvailabilityByRule =
    AvailabilityByRuleBuilder().apply(block).build()

class AvailabilityByRuleBuilder {

    private val rulesByMode = mutableMapOf<Mode, AvailabilityRuleBuilder>()

    fun availabilityOf(mode: Mode): StaticAvailabilityRuleBuilder {
        require(mode !in rulesByMode) { "Cannot define a rule for $mode as it is already defined." }
        return AvailabilityRuleBuilder(mode).also {
            rulesByMode[mode] = it
        }
    }

    fun build() = ModeAvailabilityByRule(rulesByMode.mapValues { it.value.build() })

    fun simpleAvailabilityOf(mode: Mode) = availabilityOf(mode).staticRule {
        true
    }.providerRule {
        available()
    }.resourceRule {
        availableWithoutResource()
    }
}
