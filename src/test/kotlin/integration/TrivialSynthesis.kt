package integration

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import edu.kit.ifv.populationsynthesis.synthesis.RuleBasedPopulationSynthesis

class TrivialSynthesis<AREA, H>(private val targets: List<H>, private val areas: Collection<AREA> = emptyList()) :
    RuleBasedPopulationSynthesis<AREA, H> {

    override val ruleProvider: RuleProvider<AREA, H> = object : RuleProvider<AREA, H> {
        override fun get(target: AREA, logicIdentifier: LogicIdentifier): Rule<H>? = null

        override fun getAllRules(): Map<AREA, RuleSet<H>> = emptyMap()

        override fun getRules(target: AREA): RuleSet<H> = emptySet<Rule<H>>().toRuleSet()
    }

    override fun synthesizeAll(): Map<AREA, List<H>> = areas.associateWith { targets.toMutableList() }

    override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<H>> = targetAreas.associateWith {
        targets.toMutableList()
    }
}