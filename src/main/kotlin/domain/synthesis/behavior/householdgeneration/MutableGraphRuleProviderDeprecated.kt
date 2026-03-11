package domain.synthesis.behavior.householdgeneration

open class MutableGraphRuleProviderDeprecated<T, H>(override val hierarchy: MutableHierarchicElementDeprecated<T>) :
    GraphRuleProvider<T, H>(hierarchy) {
    fun prune() {
        val removeThese = hierarchy.getAllVertices().filter { isIrrelevant(it) }
        hierarchy.removeVertices(removeThese)
    }
}
