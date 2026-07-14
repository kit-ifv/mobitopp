package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.behavior.availability.NoResourceMode
import edu.kit.ifv.domain.simulation.behavior.availability.rules.AvailabilityRule
import edu.kit.ifv.domain.simulation.behavior.availability.rules.ModeAvailabilityByRule

fun availabilityRules(block: AvailabilityByRuleBuilder.() -> Unit): ModeAvailabilityByRule =
    AvailabilityByRuleBuilder().apply(block).build()

class AvailabilityByRuleBuilder {

    private val rulesByMode = mutableMapOf<Mode, AvailabilityRule>()

    fun availabilityOf(mode: Mode): StaticAvailabilityRuleBuilder {
        require(mode !in rulesByMode) { "Cannot define a rule for $mode as it is already defined." }
        return AvailabilityRuleBuilder(mode)
    }

    fun build() = ModeAvailabilityByRule(rulesByMode)

    fun simpleAvailabilityOf(mode: Mode) = availabilityOf(mode).staticRule {
        true
    }.providerRule {
        available()
    }.resourceRule {
        availableWithoutResource()
    }

}


val test = availabilityRules {

    simpleAvailabilityOf(LegacyMode.TAXI)



    availabilityOf(LegacyMode.TAXI).staticRule {
        true
    }.providerRule {
        available()
    }.resourceRule {
        NoResourceMode(LegacyMode.TAXI)
    }

}