package domain.synthesis.behavior.householdgeneration

import org.jgrapht.Graph
import org.jgrapht.graph.AsSubgraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.BreadthFirstIterator

open class HierarchyGraph<T>(
    protected open val parentGraph: Graph<T, DefaultEdge>,
    protected open  val childGraph: Graph<T, DefaultEdge>,
): HierarchicElement<T> {

    init {
        val nonTreeNodes = parentGraph.vertexSet().filter { parentGraph.outDegreeOf(it) <= 0 }
        require(nonTreeNodes.isEmpty()) {
            "There should never be two parents for the same child. We want a tree like graph."
        }

        require(parentGraph.vertexSet() == childGraph.vertexSet()) {
            "The parent and child graph should contain the same vertices."
        }
    }
    override fun getParent(element: T): T? {
        val outEdges = parentGraph.outgoingEdgesOf(element)
        if (outEdges.isEmpty()) return null
        return parentGraph.getEdgeTarget(outEdges.first())
    }

    override fun partition(predicate: (T) -> Boolean): Pair<MutableHierarchicElement<T>, MutableHierarchicElement<T>> {
        val (setA, setB) = getAllVertices().partition(predicate).run {
            Pair(first.toSet(), second.toSet())
        }
        val one =  HierarchyGraph(AsSubgraph(parentGraph, setA),
            AsSubgraph(childGraph, setA))
        val two = HierarchyGraph(AsSubgraph(parentGraph, setB),
            AsSubgraph(childGraph, setB))

    }

    override fun getAllAncestors(element: T): Collection<T> {
        return getAllAncestorsInclusive(element) - element
    }
    private fun getAllAncestorsInclusive(element: T): Set<T> {
        return BreadthFirstIterator(parentGraph, element).asSequence().toSet()
    }
    override fun getChildren(element: T): List<T> {
        return parentGraph.incomingEdgesOf(element).map { parentGraph.getEdgeSource(it) }
    }

    override fun getAllVertices(): Set<T> {
        return parentGraph.vertexSet()
    }

    override fun getAllLeafs(): List<T> {
        return childGraph.vertexSet().filter { childGraph.outDegreeOf(it) == 0 }
    }

    /**
     * Return all descendant nodes, except the element node.
     */
    override fun getAllDescendants(element: T): Collection<T> {
        return getAllDescendantsInclusive(element) - element
    }

    private fun getAllDescendantsInclusive(element: T): Collection<T> {
        return BreadthFirstIterator(childGraph, element).asSequence().toSet()
    }

    override fun traceRoots(elements: Collection<T>): Map<T, Collection<T>> {
        return groupByHighestSharedAncestor(elements.toSet())
    }

    private fun groupByHighestSharedAncestor(targets: Set<T>): Map<T, Set<T>> {
        val activeNodes = allParentNodes(targets)
        val roots = activeNodes.filter { getParent(it) == null }
        return roots.associateWith { node -> getAllAncestorsInclusive(node).filter { it in targets }.toSet() }
    }

    private fun allParentNodes(targets: Set<T>): Set<T> {
        return BreadthFirstIterator(parentGraph, targets).asSequence().toSet()
    }
}