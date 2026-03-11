package domain.synthesis.behavior.householdgeneration

open class GraphRuleProvider<AREA, H>
private constructor(
    override val hierarchy: HierarchicElementDeprecated<AREA>,
    protected val rules: MutableMap<AREA, MutableList<Rule<H>>>,
) :
    HierarchicalRuleProviderDeprecated<AREA, H> {

    constructor(hierarchy: HierarchicElementDeprecated<AREA>) : this(hierarchy, mutableMapOf())

    override fun getAllRuleLogics(): List<NamedCountRule<H>> {
        val logicSet = mutableSetOf<NamedCountRule<H>>()
        rules.values.forEach { activeRules ->
            activeRules.forEach { rule ->
                logicSet.add(rule.logic)
            }
        }
        return logicSet.toList().sorted()
    }

    fun addRules(element: AREA, newRules: List<Rule<H>>) {
        rules.getOrPut(element) { mutableListOf() }.addAll(newRules)
    }

    fun addMultipleRules(map: Map<AREA, List<Rule<H>>>) {
        map.entries.forEach { (a, b) ->
            addRules(a, b)
        }
    }

    override operator fun contains(area: AREA): Boolean {
        return area in hierarchy.getAllVertices()
    }

    /**
     * Get all rules registered in this object and return all areas that have at least 1 rule attached
     */
    override fun getAllRules(): Map<AREA, Collection<Rule<H>>> {
        return rules.filterValues { it.isNotEmpty() }
    }

    /**
     * A node is irrelevant when neither itself, nor any parent defines any rule set.
     * TODO And also if the rule set were to be completely dominated by the descendents
     */
    fun isIrrelevant(area: AREA): Boolean {
        val allAscendingNodes = hierarchy.getAllAncestors(area) + area
        return allAscendingNodes.all { rules[it]?.isEmpty() ?: true }
    }

    override fun partition(predicate: (AREA) -> Boolean):
        Pair<HierarchicalRuleProviderDeprecated<AREA, H>, HierarchicalRuleProviderDeprecated<AREA, H>> {
        val (setA, setB) = hierarchy.partition(predicate)
        val orig = GraphRuleProvider(setA, rules.filterKeys { it in setA.getAllVertices() }.toMutableMap())
        val other = GraphRuleProvider(setB, rules.filterKeys { it in setB.getAllVertices() }.toMutableMap())
        return orig to other
    }

    override fun getRules(target: AREA): Collection<Rule<H>> {
        return rules[target] ?: emptyList()
    }
}
