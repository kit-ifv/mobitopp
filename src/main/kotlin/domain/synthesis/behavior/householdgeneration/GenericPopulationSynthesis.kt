package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.domain.SynthesisHousehold
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.BreadthFirstIterator
import utils.collections.partitionValues

interface GenericPopulationSynthesis<AREA, T> {
    val ruleProvider: RuleProvider<AREA, T>
    fun synthesize(targetAreas: List<AREA>): Map<AREA, List<SynthesisHousehold<out T>>>
}

interface HierarchicalPopulationSynthesis<AREA, T>: GenericPopulationSynthesis<AREA, T> {

    override val ruleProvider: HierarchicalRuleProvider<AREA, T>
    override fun synthesize(
        targetAreas: List<AREA>,
    ): Map<AREA, List<SynthesisHousehold<out T>>> {
        val hierarchy = ruleProvider.hierarchy
        // Trace roots runs up to the highest ancestor, but we dont need that, rather having each district
        // for example.
        val rootRegions = hierarchy.traceRoots(targetAreas)
        val independentRegions = separateIrrelevantRegions(rootRegions)


        val ex = independentRegions.flatMap { (root, childs) ->
            synthesize(root, hierarchy, childs).entries
        }.associate{it.key to it.value}

        return ex.filterKeys { it in targetAreas }
    }
    private fun Map<AREA, *>.hasIrrelevantKeys(): Boolean {
        return keys.any { isIrrelevant(it)}
    }

    private fun Map<AREA, *>.irrelevantKeys() = keys.filter { isIrrelevant(it) }
    private fun separateIrrelevantRegions(original: Map<AREA, Collection<AREA>>): Map<AREA, Collection<AREA>> {
        val workspace = original.toMutableMap()
        // TODO when none of the higher elements in the hierarchy define rules then the target area should
        //   point to itself, but right now it will po
        val flatTargets = original.values.flatten()
        while(workspace.hasIrrelevantKeys()) {
            val irrelevantKeys = workspace.irrelevantKeys()
            irrelevantKeys.forEach { key ->
                println("Killing $key because no rules")
                // Just to be safe that all values from the irrelevant key are touched by the children. Theoretically this
                // should never be able to occur, based on the graph structure and inputs
                val safetyCheck = workspace[key]!!.filter{it in flatTargets }
                workspace.remove(key)
                val children = ruleProvider.getSubAreas(key)

                val newInserts = children.associateWith { child ->
                    val grandChildren = ruleProvider.getAllDescendants(child)
                    grandChildren
                }
                workspace.putAll(newInserts)

                require(newInserts.values.flatten().containsAll(safetyCheck)) {
                    "This should not happen ever"
                }
            }
        }

        return workspace.mapValues { it.value.filter { it in flatTargets } }
    }
    // We don't need to bother catering to areas that have no rules attached to them. TODO also if their ruleset is entirely dominated by the descendants.
    private fun isIrrelevant(area: AREA) = ruleProvider.getRules(area).isEmpty()

    fun synthesizeAll(): Map<AREA, List<SynthesisHousehold<out T>>> {
        return synthesize(ruleProvider.getAllLeafs())
    }

    fun synthesize(targetArea: AREA): Map<AREA, List<SynthesisHousehold<out T>>> {
        val targets = ruleProvider.getAllDescendants(targetArea).filter { ruleProvider.isFinal(it) }
        return synthesize(targetArea, ruleProvider.hierarchy, targets)
    }

    fun synthesize(highestArea: AREA, hierarchy: HierarchicElement<AREA>, targetAreas: Collection<AREA>): Map<AREA, List<SynthesisHousehold<out T>>>

}

interface RuleProvider<AREA, T> {
    fun getRules(target: AREA): Collection<Rule<T>>
}

fun interface HandleRuleConflicts<AREA> {
    fun removeConflicts(conflictingAreas: Collection<AREA>, hierarchicElement: HierarchicElement<AREA>): Collection<AREA>
}

class UseLowestCoveredLeaf<AREA>: HandleRuleConflicts<AREA> {
    override fun removeConflicts(
        conflictingAreas: Collection<AREA>,
        hierarchicElement: HierarchicElement<AREA>,
    ): Collection<AREA> {
        val leafs = conflictingAreas.filter { hierarchicElement.isLeaf(it) }
        val completelyCovered = mutableSetOf<AREA>()
        completelyCovered.addAll(leafs)
        var nextRound = leafs.mapNotNull { hierarchicElement.getParent(it) }
        while(nextRound.isNotEmpty()) {
            nextRound.forEach { node ->
                val isCovered = hierarchicElement.getChildren(node).all { it in completelyCovered }
                if(isCovered) {
                    completelyCovered.add(node)
                }
            }
            nextRound = nextRound.mapNotNull { hierarchicElement.getParent(it) }
        }

        val (removable, unremovable) = conflictingAreas.filter { it !in leafs }.partition { it in completelyCovered }
        val mustRemove = unremovable.flatMap { hierarchicElement.getAllDescendants(it) }.toSet()

        return conflictingAreas.filter { it !in removable && it !in mustRemove }

    }
}

interface HierarchicalRuleProvider<AREA, T> : RuleProvider<AREA, T> {
    val hierarchy : HierarchicElement<AREA>

    fun getAllDescendants(target: AREA) = hierarchy.getAllDescendants(target)
    fun getAllDescendantRules(target: AREA) = getAllDescendants(target).associateWith { getRules(it) }
    fun getAllRules(target: AREA): Map<AREA, Collection<Rule<T>>> {
        val rules = getAllDescendantRules(target)
        return rules + (target to getRules(target))
    }

    fun getAllLeafs() = hierarchy.getAllLeafs()
    /**
     * Prepare the rules so that no hierarchical dependency defines the same rule twice. It is ok if all child nodes
     * have a shared rule logic, or an ancestor for the entire group, but never should a node and any of its ancestors
     * share a rule logic.
     *
     * However the rules returned should contain all rules that occur in either this area or any subareas so that no
     * rule is lost.
     */
    fun getConflictFreeRules(target: AREA, conflictResolution: HandleRuleConflicts<AREA> = UseLowestCoveredLeaf()) : List<Rule<T>> {
        val rules = getAllRules(target)

        val logicSeparated = rules.entries.flatMap { (k, v) ->
            v.map { it.logic to k }
        }
        val mappedRules = rules.mapValues { (_, v) -> v.associateBy { it.logic } }
        val groupedLogics = logicSeparated.groupBy({ it.first }, { it.second })
        val (conflicts, safe) = groupedLogics.partitionValues { hierarchy.getDependencies(it).isNotEmpty()}

        val updates = conflicts.entries.associate { it.key to conflictResolution.removeConflicts(it.value, hierarchy) }

        val fusedRules = (updates + safe).map { (k, v) ->
            val allActiveRules = v.map { mappedRules[it]!![k]!! }
            allActiveRules.fuse("Fused Rules for ${k.ruleDescription} summing ${allActiveRules.size} elements")
        }
        return fusedRules
    }
    /**
     * AN area without sub areas can be considered final
     */
    fun isFinal(target: AREA) = getSubAreas(target).isEmpty()
    /**
     * Get all rules and sum them together to form a flat set of rules.
     */
    fun getLeveledRules(target: AREA): List<Rule<T>> {
        // TODO write test case for different flattening behaviour [highest, lowest, throw]
        val rules = getAllRules(target)

        require(rules.none {
            val activeRules = getRules(it.key).map { it.logic }.toSet()
            getAllDescendantRules(it.key).flatMap { it.value.map { it.logic } }.toSet().intersect(activeRules).isEmpty()

        }) {
            "There is a rule overlap that needs to be handled."
        }
        val values = rules.values.flatten().groupBy { it.logic }.mapValues {
            it.key.ruleDescription
            it.value.fuse("Fused Rules for ${it.key.ruleDescription} using sum of subrules ${it.key.logic}")
        }.values
        return values.toList()
    }

    fun getSubAreas(target: AREA) = hierarchy.getChildren(target)
}

fun <T> Collection<Rule<T>>.fuse(descriptor: String): ZoneRule<T> {
    require(isNotEmpty()) {
        "Cannot fuse empty"
    }
    val logic = first().logic
    require(all { it.logic == logic }) {
        "Need to have same logic. "
    }

    val sum = sumOf { it.target }
    return ZoneRule(description = descriptor, sum, logic)
}





interface HierarchicElement<T> {
    fun getParent(element: T): T?
    fun getAllAncestors(element: T): Collection<T>
    fun getChildren(element: T): List<T>
    fun getAllVertices(): Collection<T>
    fun getAllLeafs(): List<T>
    /**
     * Return all descendant nodes, except the element node.
     */
    fun getAllDescendants(element: T): Collection<T>
    fun getAllLeafsFrom(element: T): Collection<T> = getAllDescendants(element).filter { isLeaf(it) }

    fun isLeaf(element: T)  = getChildren(element).isEmpty()
    fun traceRoots(elements: Collection<T>): Map<T, Collection<T>>

    fun getDependencies(elements: Collection<T>): Map<T, List<T>> {
        return elements.associateWith {
            val descendants = getAllDescendants(it).toSet()
            elements.filter {it in descendants}
        }.filterValues {
            it.isNotEmpty()
        }
    }
}

interface MutableHierarchicElement<T> : HierarchicElement<T> {
    fun addRelationship(child: T, parent: T)
    fun addVertex(target: T)
    fun removeVertex(target: T)
    fun removeVertices(targets: Collection<T>)
}

class HierarchyGraph<T>: MutableHierarchicElement<T> {
    private val parentGraph = DefaultDirectedGraph<T, DefaultEdge>(DefaultEdge::class.java)
    private val childGraph = DefaultDirectedGraph<T, DefaultEdge>(DefaultEdge::class.java)
    override fun addRelationship(child: T, parent: T) {

        if (parentGraph.containsVertex(child) && parentGraph.outgoingEdgesOf(child).isNotEmpty()) return
        Graphs.addEdgeWithVertices(parentGraph, child, parent)
        Graphs.addEdgeWithVertices(childGraph, parent, child)
    }
    override fun removeVertex(target: T) {
        parentGraph.removeVertex(target)
        childGraph.removeVertex(target)
    }
    override fun removeVertices(targets: Collection<T>) {
        parentGraph.removeAllVertices(targets)
        childGraph.removeAllVertices(targets)
    }

    override fun addVertex(target: T) {
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
    // TODO cache this calculation to avoid recalculating it every single time. Oh and maybe extract the add relationship
    //   to a builder so that once the graph is constructed it is immutable.
    override fun getAllDescendants(element: T): Set<T> {
        return allChilds(element) - element
    }

    override fun getParent(element: T): T?  = getAncestor(element)
    override fun getAllAncestors(element: T): Set<T> {
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

    override fun traceRoots(elements: Collection<T>): Map<T, Set<T>> {
        val highest = groupByHighestSharedAncestor(elements.toSet())

        return highest
    }

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
        TODO("Currently bugged, returning multiple sets, such as D{1, 2} D{3, 4} and C{1,2 , 3, 4}")
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


