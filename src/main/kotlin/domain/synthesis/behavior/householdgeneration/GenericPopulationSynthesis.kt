package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.domain.SynthesisHousehold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.html.AREA
import org.apache.poi.ss.formula.functions.T
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import utils.collections.partitionValues
import utils.collections.standardProgressBar

fun interface GenericPopulationSynthesis<AREA, T> {

    fun synthesize(targetAreas: List<AREA>): Map<AREA, List<SynthesisHousehold<out T>>>
}

interface RuleBasedPopulationSynthesis<AREA, T> : GenericPopulationSynthesis<AREA, T> {
    val ruleProvider: RuleProvider<AREA, T>

    fun synthesizeAll(): Map<AREA, List<SynthesisHousehold<out T>>>
}

interface HierarchicalPopulationSynthesis<AREA, T> : RuleBasedPopulationSynthesis<AREA, T> {

    override val ruleProvider: HierarchicalRuleProvider<AREA, T>
    override fun synthesize(
        targetAreas: List<AREA>,
    ): Map<AREA, List<SynthesisHousehold<out T>>> {
        val hierarchy = ruleProvider.hierarchy
        // Trace roots runs up to the highest ancestor, but we dont need that, rather having each district
        // for example.
        val rootRegions = hierarchy.traceRoots(targetAreas)
        val independentRegions = separateIrrelevantRegions(rootRegions)

        val progress = standardProgressBar("Hierarchical IPU", independentRegions.size.toLong())

        val out = runBlocking {
            independentRegions.entries.map { (root, childs) ->
                async(Dispatchers.Default) {
                    val result = synthesize(root, hierarchy, childs)
                    progress.step()
                    result
                }
            }.awaitAll()
                .flatMap { it.entries }
                .associate { it.key to it.value }
        }
        return out.filterKeys { it in targetAreas }
    }

    private fun Map<AREA, *>.hasIrrelevantKeys(): Boolean {
        return keys.any { isIrrelevant(it) }
    }

    private fun Map<AREA, *>.irrelevantKeys() = keys.filter { isIrrelevant(it) }
    private fun separateIrrelevantRegions(original: Map<AREA, Collection<AREA>>): Map<AREA, Collection<AREA>> {
        val workspace = original.toMutableMap()
        // TODO when none of the higher elements in the hierarchy define rules then the target area should
        //   point to itself, but right now it will point
        val flatTargets = original.values.flatten()
        while (workspace.hasIrrelevantKeys()) {
            val irrelevantKeys = workspace.irrelevantKeys()
            irrelevantKeys.forEach { key ->
                println("Killing $key because no rules")
                // Just to be safe that all values from the irrelevant key are touched by the children. Theoretically this
                // should never be able to occur, based on the graph structure and inputs
                val safetyCheck = workspace[key]!!.filter { it in flatTargets }
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

    override fun synthesizeAll(): Map<AREA, List<SynthesisHousehold<out T>>> {
        return synthesize(ruleProvider.getAllLeafs())
    }

    fun synthesize(targetArea: AREA): Map<AREA, List<SynthesisHousehold<out T>>> {
        val targets = ruleProvider.getAllDescendants(targetArea).filter { ruleProvider.isFinal(it) }
        return synthesize(targetArea, ruleProvider.hierarchy, targets)
    }

    /**
     * Synthesize the population for a list of areas called [targetAreas], where the highest shared area is passed along
     * with a hierarchy to traverse area dependenciey
     */
    fun synthesize(
        highestArea: AREA,
        hierarchy: HierarchicElement<AREA>,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<SynthesisHousehold<out T>>>
}

interface RuleProvider<AREA, T> {
    fun getRules(target: AREA): Collection<Rule<T, ISurveyHousehold<out T>>>
    fun getAllRules(): Map<AREA, Collection<Rule<T, ISurveyHousehold<out T>>>>
}

class MapRuleProvider<AREA, T>(
    private val ruleMap: MutableMap<AREA, List<Rule<T, ISurveyHousehold<out T>>>> = mutableMapOf()
) :
    RuleProvider<AREA, T> {

    override fun getRules(target: AREA): Collection<Rule<T, ISurveyHousehold<out T>>> {
        return ruleMap[target] ?: emptyList()
    }

    override fun getAllRules(): Map<AREA, Collection<Rule<T, ISurveyHousehold<out T>>>> {
        return ruleMap
    }

    companion object {
        fun fromMap(map: Map<AREA, List<Rule<T, ISurveyHousehold<out T>>>>): MapRuleProvider<AREA, T> = MapRuleProvider(map.toMutableMap())
    }
}

fun interface HandleRuleConflicts<AREA> {
    fun removeConflicts(
        conflictingAreas: Collection<AREA>,
        hierarchicElement: HierarchicElement<AREA>,
    ): Collection<AREA>
}

class UseLowestCoveredLeaf<AREA> : HandleRuleConflicts<AREA> {
    override fun removeConflicts(
        conflictingAreas: Collection<AREA>,
        hierarchicElement: HierarchicElement<AREA>,
    ): Collection<AREA> {
        val leafs = conflictingAreas.filter { hierarchicElement.isLeaf(it) }
        val completelyCovered = mutableSetOf<AREA>()
        completelyCovered.addAll(leafs)
        var nextRound = leafs.mapNotNull { hierarchicElement.getParent(it) }
        while (nextRound.isNotEmpty()) {
            nextRound.forEach { node ->
                val isCovered = hierarchicElement.getChildren(node).all { it in completelyCovered }
                if (isCovered) {
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
    val hierarchy: HierarchicElement<AREA>

    fun partition(predicate: (AREA) -> Boolean): Pair<
        HierarchicalRuleProvider<AREA, T>,
        HierarchicalRuleProvider<AREA, T>
        >

    fun getAllDescendants(target: AREA) = hierarchy.getAllDescendants(target)
    fun getAllDescendantRules(target: AREA) = getAllDescendants(target).associateWith { getRules(it) }

    @Deprecated("Use conflict free rules instead.")
    fun getAllRulesFor(target: AREA): Map<AREA, Collection<Rule<T, ISurveyHousehold<out T>>>> {
        val rules = getAllDescendantRules(target)
        return rules + (target to getRules(target))
    }

    fun getAllRuleLogics(): List<NamedCountRule<RawSurveyInfo>>
    operator fun contains(area: AREA): Boolean

    /**
     * Get all rules registered in this object and return all areas that have at least 1 rule attached
     */

    fun getAllLeafs() = hierarchy.getAllLeafs()

    /**
     * Prepare the rules so that no hierarchical dependency defines the same rule twice. It is ok if all child nodes
     * have a shared rule logic, or an ancestor for the entire group, but never should a node and any of its ancestors
     * share a rule logic.
     *
     * However the rules returned should contain all rules that occur in either this area or any subareas so that no
     * rule is lost.
     */
    fun getConflictFreeRules(
        target: AREA,
        conflictResolution: HandleRuleConflicts<AREA> = UseLowestCoveredLeaf(),
    ): List<Rule<T, ISurveyHousehold<out T>>> {
        val rules = getAllRulesFor(target)

        val logicSeparated = rules.entries.flatMap { (k, v) ->
            v.map { it.logic to k }
        }
        val mappedRules = rules.mapValues { (_, v) -> v.associateBy { it.logic } }
        val groupedLogics = logicSeparated.groupBy({ it.first }, { it.second })
        val (conflicts, safe) = groupedLogics.partitionValues { hierarchy.getDependencies(it).isNotEmpty() }

        val updates = conflicts.entries.associate { it.key to conflictResolution.removeConflicts(it.value, hierarchy) }

        val fusedRules = (updates + safe).map { (k, v) ->
            val allActiveRules = v.map { mappedRules[it]!![k]!! }
            allActiveRules.fuse("Fused Rules for ${k.ruleDescription} summing ${allActiveRules.size} elements")
        }
        // TODO the return of rules should be implicitly sorted and not dependent on the implementation
        return fusedRules.sortedBy { it.logic }
    }

    /**
     * AN area without sub areas can be considered final
     */
    fun isFinal(target: AREA) = getSubAreas(target).isEmpty()

    fun getSubAreas(target: AREA) = hierarchy.getChildren(target)
}

fun <T> Collection<Rule<T, ISurveyHousehold<out T>>>.fuse(descriptor: String): ZoneRule<T, ISurveyHousehold<out T>> {
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

    fun partition(predicate: (T) -> Boolean): Pair<HierarchicElement<T>, HierarchicElement<T>>

    /**
     * Return all descendant nodes, except the element node.
     */
    fun getAllDescendants(element: T): Collection<T>
    fun getAllLeafsFrom(element: T): Collection<T> = getAllDescendants(element).filter { isLeaf(it) }

    fun isLeaf(element: T) = getChildren(element).isEmpty()
    fun traceRoots(elements: Collection<T> = getAllLeafs()): Map<T, Collection<T>>

    fun getDependencies(elements: Collection<T>): Map<T, List<T>> {
        return elements.associateWith {
            val descendants = getAllDescendants(it).toSet()
            elements.filter { it in descendants }
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

class MutableHierarchyGraph<T> private constructor(
    override val parentGraph: DefaultDirectedGraph<T, DefaultEdge>,
    override val childGraph: DefaultDirectedGraph<T, DefaultEdge>,
) : HierarchyGraph<T>(parentGraph, childGraph), MutableHierarchicElement<T> {
    constructor() : this(DefaultDirectedGraph(DefaultEdge::class.java), DefaultDirectedGraph(DefaultEdge::class.java))

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
}
