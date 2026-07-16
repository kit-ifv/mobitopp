package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.behavior.availability.rules.ModeAvailabilityByRule

/**
 * DSL entry point for creating a [ModeAvailabilityByRule] model.
 *
 * @param block the builder block
 * @return a configured [ModeAvailabilityByRule]
 */
fun availabilityRules(block: AvailabilityByRuleBuilder.() -> Unit): ModeAvailabilityByRule =
    AvailabilityByRuleBuilder().apply(block).build()

/**
 * Builder class for configuring mode availability rules.
 */
class AvailabilityByRuleBuilder {

    private val rulesByMode = mutableMapOf<Mode, AvailabilityRuleBuilder>()

    /**
     * Starts defining an availability rule for the given [mode].
     * @param mode the transport mode
     * @return a builder for static availability rules
     */
    fun availabilityOf(mode: Mode): StaticAvailabilityRuleBuilder {
        require(mode !in rulesByMode) { "Cannot define a rule for $mode as it is already defined." }
        return AvailabilityRuleBuilder(mode).also {
            rulesByMode[mode] = it
        }
    }

    /**
     * Builds the [ModeAvailabilityByRule] model.
     */
    fun build() = ModeAvailabilityByRule(rulesByMode.mapValues { it.value.build() })

    /**
     * Defines a simple availability rule for a [mode] that is always available
     * and has no provider agent and uses no resource (e.g. vehicle) for transportation.
     * @param mode the transport mode
     */
    fun simpleAvailabilityOf(mode: Mode) = availabilityOf(mode).staticRule {
        true
    }.providerRule {
        available()
    }.resourceRule {
        availableWithoutResource()
    }
}
