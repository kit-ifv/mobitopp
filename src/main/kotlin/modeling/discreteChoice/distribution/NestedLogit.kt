package modeling.discreteChoice.distribution

import modeling.discreteChoice.DistributionFunction
import modeling.models.ChoiceAlternative
import utils.collections.printAsTree
import java.util.*
import kotlin.math.exp
import kotlin.math.ln

class NestedLogit<R : Any, A, P>(
    private val structure: NestStructureData<R, A, P>,
) : DistributionFunction<A, P> where A : ChoiceAlternative<R> {

    override fun calculateProbabilities(utilities: Map<A, Double>, parameters: P): Map<A, Double> {
        val (root, leafs) = structure

        return synchronized(root) {
            root.reset() // Reset the calculation tree to reset the relevantForCalculation flags.
            val relevantLeaves = utilities.entries.map {
                AssociatedSituation(
                    it.key,
                    leafs[it.key.choice]!!,
                    it.value
                )
            }
            runQueue(relevantLeaves, parameters)
            relevantLeaves.associate { it.sit to it.probability }
        }
    }
}

data class NestStructureData<R : Any, A, P>(
    val root: NestStructure<P>.Node,
    val leafs: Map<R, NestStructure<P>.Leaf>,
) where A : ChoiceAlternative<R>

fun interface NestedStructureDataBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {
    fun buildStructure(): NestStructureData<R, A, P>
}

// TODO can we implement this stateless?
fun <SIT, PARAMS> runQueue(
    situations: List<AssociatedSituation<SIT, PARAMS>>,
    parameters: PARAMS
) {
    val nextNests = situations.mapNotNull { it.initializeUtility() }
    val queue = PriorityQueue<NestStructure<PARAMS>.Nest> { a, b -> a.level - b.level }

    lateinit var lastElement: NestStructure<PARAMS>.Nest
    queue.addAll(nextNests)
    while (queue.isNotEmpty()) {
        val n = queue.poll()
        lastElement = n
        val parent = n.calculateUtility(parameters)
        parent?.let { queue.add(it) }
    }
    lastElement.probability = 1.0
    lastElement.calculateProbability(parameters)
}

/**
 * We need to cross-reference an arbitrary situation [SIT] to the corresponding [leaf]. This class maintains
 * this object state until we release the probability calculation
 */
class AssociatedSituation<SIT, PARAMS>(
    val sit: SIT,
    val leaf: NestStructure<PARAMS>.Leaf,
    val utility: Double
) {
    val probability get() = leaf.probability

    /**
     * set the utility of the leaf to the already calculated utility and set the calculation flags.
     */
    fun initializeUtility(): NestStructure<PARAMS>.Nest? {
        return leaf.initializeUtility(utility)
    }
}

class NestStructure<PARAMS> {

    fun build(leafs: Set<Leaf>) {
        leafs.groupBy { it.parent }
    }

    abstract inner class Node {
        /**
         * Level represents the depth of the alternative in the Nest Structure, lower level nests need to
         * be calculated for their utility first.
         */
        abstract val extractAlphaParameter: (PARAMS) -> Double
        abstract val level: Int
        var relevantForCalculation = false
        abstract var parent: Nest?
        abstract fun reset()
        var utility: Double = 0.0
        var probability: Double = 0.0
        abstract val name: String
        open fun calculateProbability(parameters: PARAMS) {
            // println(this)
        }

        fun treePrint(): Unit = printAsTree(
            root = this,
            label = { it.toString() },
            getChildren = { it.children() }
        )

        abstract fun children(): List<Node>
        abstract fun leafs(): List<Leaf>
    }

    inner class Leaf(
        override val extractAlphaParameter: (PARAMS) -> Double = {
            1.0
        },
        override val name: String = hashCode().toString()
    ) : Node() {
        override var parent: Nest? = null
        override val level: Int = 0

        override fun reset() {
            relevantForCalculation = false
        }

        fun initializeUtility(utility: Double): Nest? {
            this.utility = utility
            relevantForCalculation = true
            parent?.relevantForCalculation = true
            return parent
        }

        override fun toString(): String {
            return "Leaf $name: [U: $utility, P: $probability]"
        }

        override fun children(): List<NestStructure<PARAMS>.Node> = emptyList()
        override fun leafs(): List<NestStructure<PARAMS>.Leaf> = listOf(this)
    }

    inner class Nest(
        private val childNodes: Collection<Node>,
        override val name: String = hashCode().toString(),
        val extractLambdaParameter: (PARAMS) -> Double
    ) :
        Node() {
        override var parent: Nest? = null
        override val level = childNodes.maxOf { it.level } + 1

        /**
         * @property maxUtility keep track of the highest utility found in the children, to subtract that value from
         * each utility calculation, to turn big utilities to small numbers, and numeric problems with exp and ln()
         * from a potential infinity to a 0.0 which is better handleable.
         */
        private var maxUtility = 0.0
        private var sum = 0.0

        override val extractAlphaParameter: (PARAMS) -> Double = {
            1.0
        } // Alpha parameter is only relevant for leaves. and thus not in the constructor
        override fun reset() {
            relevantForCalculation = false
            childNodes.forEach { it.reset() }
        }

        fun calculateUtility(parameters: PARAMS): Nest? {
            val lambda = extractLambdaParameter(parameters)
            val relevantChilds = childNodes.filter { it.relevantForCalculation }
            if (relevantChilds.isEmpty()) {
                error("Never should a calculate Utility be called when the childs are irrelevant")
            }
            maxUtility = relevantChilds.maxOf { ln(it.extractAlphaParameter(parameters)) + it.utility }
            // println(relevantChilds.joinToString(prefix="calcMax = ") { "${it.name} -> ${ln(it.extractAlphaParameter(parameters)) + it.utility}" })
            val x = relevantChilds
                .map { ln(it.extractAlphaParameter(parameters)) + it.utility }
                .sumOf { exp((it - maxUtility) / lambda) }

            utility = maxUtility + lambda * ln(x)
            sum = x
            // println("calcUtility $name: max=$maxUtility x=$x util=$utility sum=$sum")
            return parent
        }

        override fun calculateProbability(parameters: PARAMS) {
            val lambda = extractLambdaParameter(parameters)
            val relevantChilds = childNodes.filter { it.relevantForCalculation }
            relevantChilds.forEach {
                val actualUtil = ln(it.extractAlphaParameter(parameters)) + it.utility
                val utilCalculation = actualUtil.let { d -> exp((d - maxUtility) / lambda) / sum }
                val childProbability =
                    this.probability * utilCalculation
                it.probability = childProbability
            }
            relevantChilds.forEach { it.calculateProbability(parameters) }
        }

        override fun children(): List<Node> = childNodes.toList()

        override fun leafs(): List<NestStructure<PARAMS>.Leaf> = childNodes.flatMap { it.leafs() }

        override fun toString(): String {
            return "$name: (U: $utility P: $probability): Childs: ${childNodes.joinToString(
                prefix = "[",
                postfix = "]"
            ) { it.name}}"
        }
    }
}
