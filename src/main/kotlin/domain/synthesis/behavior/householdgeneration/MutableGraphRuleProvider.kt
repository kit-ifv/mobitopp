package domain.synthesis.behavior.householdgeneration

open class MutableGraphRuleProvider<T, H>(override val hierarchy: MutableHierarchicElement<T>) :
    GraphRuleProvider<T, H>(hierarchy) {
    fun prune() {
        val removeThese = hierarchy.getAllVertices().filter { isIrrelevant(it) }
        hierarchy.removeVertices(removeThese)
    }
}
