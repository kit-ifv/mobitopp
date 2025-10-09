package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.domain.SynthesisHousehold
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.BreadthFirstIterator

 interface GenericPopulationSynthesis<AREA, T> {
    val ruleProvider: RuleProvider<AREA, T>
    fun synthesize(targetAreas: List<AREA>): Map<AREA, List<SynthesisHousehold<T>>>
}

interface HierarchicalPopulationSynthesis<AREA, T>: GenericPopulationSynthesis<AREA, T> {

    override val ruleProvider: HierarchicalRuleProvider<AREA, T>
    override fun synthesize(
        targetAreas: List<AREA>,
    ): Map<AREA, List<SynthesisHousehold<T>>> {
        val hierarchy = ruleProvider.hierarchy
        val independentRegions = hierarchy.traceRoots(targetAreas)
        val ex = independentRegions.flatMap { (root, childs) ->
            synthesize(root, hierarchy).entries
        }.associate{it.key to it.value}

        return ex.filterKeys { it in targetAreas }
    }

    fun synthesize(highestArea: AREA, hierarchy: HierarchicElement<AREA>): Map<AREA, List<SynthesisHousehold<T>>>


}

interface RuleProvider<AREA, T> {
    fun generateRules(target: AREA): Collection<NamedCountRule<T>>
}

interface HierarchicalRuleProvider<AREA, T> : RuleProvider<AREA, T> {
    val hierarchy : HierarchicElement<AREA>


}







interface HierarchicElement<T> {
    fun getParent(element: T): T?
    fun getChildren(element: T): List<T>

    fun traceRoots(elements: Collection<T>): Map<T, Collection<T>>

}

class HierarchyGraph<T>: HierarchicElement<T> {
    private val parentGraph = DefaultDirectedGraph<T, DefaultEdge>(DefaultEdge::class.java)
    private val childGraph = DefaultDirectedGraph<T, DefaultEdge>(DefaultEdge::class.java)
    fun addRelationship(child: T, parent: T) {

        if (parentGraph.containsVertex(child) && parentGraph.outgoingEdgesOf(child).isNotEmpty()) return
        Graphs.addEdgeWithVertices(parentGraph, child, parent)
        Graphs.addEdgeWithVertices(childGraph, parent, child)


    }

    fun addVertex(target: T) {
        parentGraph.addVertex(target)
        childGraph.addVertex(target)
    }

    fun getAncestor(target: T): T? {
        val outEdges = parentGraph.outgoingEdgesOf(target)
        if (outEdges.isEmpty()) return null
        require(outEdges.size <= 1) {
            "How does this work, you have multiple parents?"
        }
        return parentGraph.getEdgeTarget(outEdges.first())
    }

    override fun getParent(element: T): T?  = getAncestor(element)

    override fun getChildren(element: T): List<T> {
        return parentGraph.incomingEdgesOf(element).map { parentGraph.getEdgeSource(it) }
    }

    override fun traceRoots(elements: Collection<T>): Map<T, Set<T>> = groupByHighestSharedAncestor(elements.toSet())

    fun allChilds(target: T): Set<T> {
        return BreadthFirstIterator(childGraph, target).asSequence().toSet()
    }

    fun getAncestors(target: T): Set<T> {
        var currentElement = getAncestor(target)
        val ancestors = mutableSetOf<T>()
        while (currentElement != null) {
            ancestors.add(currentElement)
            currentElement = getAncestor(currentElement)
        }
        return ancestors

    }

    fun groupByHighestSharedAncestor(targets: Set<T>): Map<T, Set<T>> {
        val activeNodes = allParentNodes(targets)
        val roots = activeNodes.filter { getAncestor(it) == null }
        return roots.associateWith { node -> allChilds(node).filter { it in targets }.toSet() }
    }

    fun groupByLowestSharedAncestor(targets: Set<T>): Map<T, Set<T>> {
        val activeNodes = allParentNodes(targets)
        val filter = activeNodes.filter { activeNode ->
            getChildren(activeNode).filter { it in activeNodes }.size > 1
        }
        val associatedNodes = filter.associateWith { node -> allChilds(node).filter { it in targets }.toSet() }
        val handledTargetNodes = associatedNodes.values.flatten()
        val unhandledTargetNodes = targets.filter { it !in handledTargetNodes }.associateWith { setOf(it) }
        return associatedNodes + unhandledTargetNodes
    }

    private fun allParentNodes(targets: Set<T>): Set<T> {
        return BreadthFirstIterator(parentGraph, targets).asSequence().toSet()
    }
}


