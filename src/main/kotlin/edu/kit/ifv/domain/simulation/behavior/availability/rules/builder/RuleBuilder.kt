package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.NoResourceMode
import edu.kit.ifv.domain.simulation.behavior.availability.ProviderAvailability
import edu.kit.ifv.domain.simulation.behavior.availability.notAvailable
import edu.kit.ifv.domain.simulation.behavior.availability.rules.AvailabilityRule
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.utils.units.AbsoluteTime

/** Static rule logic: (person: IPerson) -> Boolean */
typealias StaticRule = (person: IPerson) -> Boolean
/** Provider rule logic: (agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation) -> ProviderAvailability */
typealias ProviderRule = (agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation) -> ProviderAvailability
/** Resource rule logic: (characteristics: ModeChoiceCharacteristics) -> ModeResource? */
typealias ResourceRule = (characteristics: ModeChoiceCharacteristics) -> ModeResource?

/** DSL definition for static rules. */
typealias StaticRuleDefinition = StaticRuleScope.() -> Boolean
/** DSL definition for provider rules. */
typealias ProviderRuleDefinition = ProviderRuleScope.() -> ProviderAvailability
/** DSL definition for wrapped provider rules. */
typealias ProviderWrappedRuleDefinition = ProviderRuleScope.() -> Boolean
/** DSL definition for resource rules. */
typealias ResourceRuleDefinition = ResourceRuleScope.() -> ModeResource?

/** Interface for building the static availability part of a rule. */
sealed interface StaticAvailabilityRuleBuilder {
    /** Sets the [rule] for static availability. */
    fun staticRule(rule: StaticRuleDefinition): ProviderAvailabilityRuleBuilder
}

/** Interface for building the provider availability part of a rule. */
sealed interface ProviderAvailabilityRuleBuilder {
    /** Sets the [rule] for provider availability. */
    fun providerRule(rule: ProviderRuleDefinition): ResourceAvailabilityRuleBuilder
    /** Sets the [rule] for provider availability, indicating availability of [providers] if the rule evaluates to true. */
    fun providersIf(vararg providers: Any, rule: ProviderWrappedRuleDefinition): ResourceAvailabilityRuleBuilder
}

/** Interface for building the resource availability part of a rule. */
sealed interface ResourceAvailabilityRuleBuilder {
    /** Sets the [rule] for resource availability. */
    fun resourceRule(rule: ResourceRuleDefinition)
}

/** Sets a default static rule (always true) and returns the next builder. */
fun StaticAvailabilityRuleBuilder.default() = staticRule { true }.default()
/** Sets a default provider rule (always available) and returns the next builder. */
fun ProviderAvailabilityRuleBuilder.default() = providerRule { available() }.default()
/** Sets a default resource rule (available without specific resource). */
fun ResourceAvailabilityRuleBuilder.default() = resourceRule { NoResourceMode(mode) }

/**
 * Implementation of rule builders that tracks the transport [mode] and its rules.
 */
class AvailabilityRuleBuilder(val mode: Mode) :
    StaticAvailabilityRuleBuilder,
    ProviderAvailabilityRuleBuilder,
    ResourceAvailabilityRuleBuilder {
    private lateinit var static: StaticRule
    private lateinit var provider: ProviderRule
    private lateinit var resource: ResourceRule

    override fun staticRule(rule: StaticRuleDefinition) = apply {
        static = { person -> StaticRuleScope(mode, person).rule() }
    }

    override fun providerRule(rule: ProviderRuleDefinition) = apply {
        provider = { agent, time, destination -> ProviderRuleScope(mode, agent, time, destination).rule() }
    }

    override fun providersIf(vararg providers: Any, rule: ProviderWrappedRuleDefinition) = apply {
        provider = { agent, time, destination ->
            ProviderRuleScope(mode, agent, time, destination).let {
                if (it.rule()) {
                    it.available(providers)
                } else {
                    it.notAvailable()
                }
            }
        }
    }

    override fun resourceRule(rule: ResourceRuleDefinition) {
        resource = { characteristics -> ResourceRuleScope(mode, characteristics).rule() }
    }

    internal fun build(): AvailabilityRule {
        require(::static.isInitialized) { "Cannot build availability rule for $mode as no static rule was defined!" }
        require(
            ::provider.isInitialized,
        ) { "Cannot build availability rule for $mode as no provider rule was defined!" }
        require(
            ::resource.isInitialized,
        ) { "Cannot build availability rule for $mode as no resource rule was defined!" }
        return DefaultRule(mode, static, provider, resource)
    }
}

/**
 * Default implementation of [AvailabilityRule] that delegates availability checks to provided rule lambdas.
 *
 * This class implements the three-stage availability model:
 * 1. **Static Availability**: Checks if the mode is generally available to the person (e.g., license, vehicle ownership).
 * 2. **Provider Availability**: Checks if the mode is available in the current situation, considering
 *    resource providers (e.g., sharing stations, DRT areas). It also ensures that the person is not
 *    already locked into a different mode.
 * 3. **Resource Availability**: Checks the availability of specific resources (e.g., a specific car or bike)
 *    and selects the best one if applicable.
 *
 * @property mode the transport mode this rule applies to
 * @property staticRule the lambda for static availability checks
 * @property providerRule the lambda for provider availability checks
 * @property resourceRule the lambda for resource availability checks
 */
private class DefaultRule(
    override val mode: Mode,
    private val staticRule: StaticRule,
    private val providerRule: ProviderRule,
    private val resourceRule: ResourceRule,
) : AvailabilityRule {

    override fun staticAvailability(person: IPerson): Boolean = staticRule(person)

    override fun providerAvailability(
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation,
    ): ProviderAvailability {
        if (!staticRule(agent)) return mode.notAvailable
        // If the agent is already using a different resource (e.g. during a trip), other modes are not available.
        return if (isFixedToDifferentMode(agent, mode)) {
            mode.notAvailable
        } else {
            providerRule(agent, time, destination)
        }
    }

    /**
     * Checks if the agent is currently using a resource associated with a different mode.
     */
    private fun isFixedToDifferentMode(agent: PersonAgent, mode: Mode): Boolean =
        agent.modeResource?.mode?.let { it != mode } ?: false

    override fun resourceAvailability(characteristics: ModeChoiceCharacteristics): ModeResource? {
        // Only check resource availability if the mode is among the currently considered choices.
        if (mode !in characteristics.currentChoices) return null
        // If only one mode is left, we assume it's available without further resource check (e.g. for forced modes).
        if (characteristics.currentChoices.size == 1) return NoResourceMode(mode) // TODO check
        return resourceRule(characteristics)
    }
}
