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

typealias StaticRule   = (person: IPerson) -> Boolean
typealias ProviderRule = (agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation) -> ProviderAvailability
typealias ResourceRule = (characteristics: ModeChoiceCharacteristics) -> ModeResource?


typealias StaticRuleDefinition = StaticRuleScope.() -> Boolean
typealias ProviderRuleDefinition = ProviderRuleScope.() -> ProviderAvailability
typealias ProviderWrappedRuleDefinition = ProviderRuleScope.() -> Boolean
typealias ResourceRuleDefinition = ResourceRuleScope.() -> ModeResource?


interface StaticAvailabilityRuleBuilder {
    fun staticRule(rule: StaticRuleDefinition): ProviderAvailabilityRuleBuilder
}

interface ProviderAvailabilityRuleBuilder {
    fun providerRule(rule: ProviderRuleDefinition): ResourceAvailabilityRuleBuilder
    fun providersIf(vararg providers: Any, rule: ProviderWrappedRuleDefinition): ResourceAvailabilityRuleBuilder
}

interface ResourceAvailabilityRuleBuilder {
    fun resourceRule(rule: ResourceRuleDefinition)
}

fun StaticAvailabilityRuleBuilder.default() = staticRule { true }.default()
fun ProviderAvailabilityRuleBuilder.default() = providerRule { available() }.default()
fun ResourceAvailabilityRuleBuilder.default() = resourceRule { NoResourceMode(mode) }

class AvailabilityRuleBuilder(
    val mode: Mode,
): StaticAvailabilityRuleBuilder, ProviderAvailabilityRuleBuilder, ResourceAvailabilityRuleBuilder {
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
                } else it.notAvailable()
            }
        }
    }

    override fun resourceRule(rule: ResourceRuleDefinition) {
        resource = { characteristics -> ResourceRuleScope(mode, characteristics).rule() }
    }

    internal fun build(): AvailabilityRule {
        require(::static.isInitialized) { "Cannot build availability rule for $mode as no static rule was defined!" }
        require(::provider.isInitialized) { "Cannot build availability rule for $mode as no provider rule was defined!" }
        require(::resource.isInitialized) { "Cannot build availability rule for $mode as no resource rule was defined!" }
        return DefaultRule(mode, static, provider, resource)
    }

}

private class DefaultRule(
    override val mode: Mode,
    private val staticRule: StaticRule,
    private val providerRule: ProviderRule,
    private val resourceRule: ResourceRule
) : AvailabilityRule {

    override fun staticAvailability(person: IPerson): Boolean =
        staticRule(person)

    override fun providerAvailability(
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation,
    ): ProviderAvailability {
        if (!staticRule(agent)) return mode.notAvailable
        return if (isFixedToDifferentMode(agent, mode)) mode.notAvailable
            else providerRule(agent, time, destination)
    }

    private fun isFixedToDifferentMode(agent: PersonAgent, mode: Mode): Boolean =
        agent.modeResource?.mode?.let { it != mode } ?: false

    override fun resourceAvailability(
        characteristics: ModeChoiceCharacteristics,
    ): ModeResource? {
        if (mode !in characteristics.currentChoices) return null
        if (characteristics.currentChoices.size == 1) return NoResourceMode(mode) //TODO check
        return resourceRule(characteristics)
    }
}






