@file:Suppress("Filename")

package domain.synthesis.rules

import edu.kit.ifv.populationsynthesis.hierarchy.ForestHierarchyGraph
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProviderImpl
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
fun <AREA, T> RuleProvider<AREA, T>.flatHierarchy(): HierarchicRuleProvider<AREA, T> {
    val ruleProvider = MapRuleProvider<AREA, T>()
    getAllRules().forEach { (area, rules) ->
        ruleProvider.addRules(area, rules)
    }
    val hierarchy = ForestHierarchyGraph<AREA>()
    getAllRules().keys.forEach {
        hierarchy.addVertex(it)
    }

    return HierarchicRuleProviderImpl(ruleProvider, hierarchy)
}
