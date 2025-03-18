package modeling.discreteChoice

import java.util.*
import kotlin.math.exp
import kotlin.math.ln

/**
 * A nested logit is an allocated distribution function, as the class holds the necessary data to associate an
 * element of type [X] or [SIT] to the appropriate utility function. This class uses a two way calculation to determine
 * the distribution probabilities. First an upwards calculation where the utility of options and nests is calculated and
 * propagated towards the parents. Then after the highest common parent is evaluated, a backwards propagation is performed
 * where the probability of the child nodes is set by the parents. The Probability of a target alternative is always
 * <probability to be picked in parent nest * probability to be picked in grandparent nest * ...>
 *
 * Since the available set of options is known beforehand, only the necessary options and nests are taken into calculation
 *
 * @property name A name for the choice model to allow better error messages.
 * @property rules A collection of rules which decide whether an element [SIT] should be associated to a target utility function
 * @property leafs holds a reference to the leaves to trigger the initial nest calculation.
 * @property root holds a reference to the root node of the nest structure to quickly reset the flags
 */
class NestedLogit<X : Any, SIT : ChoiceSituation<X>, PARAMS>(
    override val name: String = "Unnamed Nested Logit",
    private val leafs: Map<X, NestStructure<PARAMS>.Leaf>,
    private val root: NestStructure<PARAMS>.Nest,
    override val translation: Map<X, UtilityFunction<SIT, PARAMS>>
) :
    OptionDistributionFunction<X, SIT, PARAMS> {

    override val options: Set<X> = leafs.keys

    /**
     * We need to cross-reference an arbitrary situation [SIT] to the corresponding [leaf]. This class maintains
     * this object state until we release the probability calculation
     */
    private inner class AssociatedSituation(
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

    override fun calculateProbabilities(
        evaluators: Map<SIT, Double>,
        parameters: PARAMS
    ): Map<SIT, Double> {
        root.reset() // Reset the calculation tree to reset the relevantForCalculation flags.
        val relevantLeaves = evaluators.entries.map { AssociatedSituation(it.key, leafs[it.key.choice]!!, it.value) }
        val nextNests = relevantLeaves.mapNotNull { it.initializeUtility() }
        val queue = PriorityQueue<NestStructure<PARAMS>.Nest> { a, b -> a.level - b.level }
        lateinit var lastElement: NestStructure<PARAMS>.Nest
        queue.addAll(nextNests)
        // TODO queue may calculate an element twice. Doesn't harm the result but is moderately inefficient. Maybe hold
        //   a set of visited alternatives?
        while (queue.isNotEmpty()) {
            val n = queue.poll()
            lastElement = n
            val parent = n.calculateUtility(parameters)
            parent?.let { queue.add(it) }
        }
        lastElement.probability = 1.0
        lastElement.calculateProbability(parameters)
        return relevantLeaves.associate { it.sit to it.probability }
    }

    companion object {

        private const val UNNAMED_NEST_MODEL = "Unnamed Nested Logit model"

        /**
         * The build structure class for a nested logit system. The map and rules are attributes propagated by the eldest
         * builder object. Thus the primary constructor is private, to avoid potential errors. All child builders need to
         * have the same reference to the rule and map sets so that the eldest builder can return the entire set of options
         * and rules after build completion.
         */
        class NestedLogitBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS> private constructor(
            val map: MutableMap<X, NestStructure<PARAMS>.Leaf>,
            val translation: MutableMap<X, UtilityFunction<SIT, PARAMS>>
        ) : OptionBasedSituationBuilder<X, SIT, PARAMS> {
            constructor() : this(mutableMapOf(), mutableMapOf())

            private val childs: MutableList<NestStructure<PARAMS>.Node> = mutableListOf()

            /**
             * Generate a nest block with
             *
             * @param lambdaParameterExtraction a function defining how this nest block should extract the lambda parameter
             from the input object
             * @param functor a description to build the nest structure along the builder.
             */
            fun nest(
                lambdaParameterExtraction: PARAMS.() -> Double,
                name: String = "Unnamed Nest",
                functor: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
            ): NestStructure<PARAMS>.Nest {
                val builder = NestedLogitBuilder(map, translation)
                builder.functor()
                val childNodes = builder.build()
                map.putAll(builder.map)
                require(childNodes.isNotEmpty()) {
                    "Cannot create an empty nest. You must add at least one option in a nest block using " +
                        "the option(...) { } syntax. This includes the implicit root nest block. "
                }
                val nest = NestStructure<PARAMS>().Nest(childNodes, name,  lambdaParameterExtraction)
                childNodes.forEach { it.parent = nest }
                childs.add(nest)
                return nest
            }

            /**
             * A convenience function to hardcode a lambda parameter to a nest.
             */
            fun nest(
                lambda: Double,
                functor: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
            ): NestStructure<PARAMS>.Nest {
                return nest({ lambda },"Unnamed", functor)
            }

            override fun addUtilityFunctionByIdentifier(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
                translation[x] = utilityFunction
                val element = NestStructure<PARAMS>().Leaf()
                childs.add(element)
                require(!map.containsKey(x)) {
                    "A utility function for $x has already been defined in this nest structure. Current elements" +
                        " ${map.keys} have a utility function associated. "
                }
                map[x] = element
            }

            fun build(): MutableList<NestStructure<PARAMS>.Node> {
                return childs
            }
        }

        /**
         * Create a nested logit structure by parsing the instruction and building an implicit "ROOT-Nest" with a lambda
         * 1.0 parameter
         */
        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> build(
            name: String = UNNAMED_NEST_MODEL,
            lambda: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): NestedLogit<X, SIT, PARAMS> {
            val builder = NestedLogitBuilder<X, SIT, PARAMS>()
            val root = builder.nest(1.0) {
                lambda()
            }

            return NestedLogit(name, builder.map, root, builder.translation)
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> root(
            name: String = UNNAMED_NEST_MODEL,
            lambda: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): NestedLogit<X, SIT, PARAMS> = build(name, lambda)
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
            //println(this)
        }
    }

    inner class Leaf(override val extractAlphaParameter: (PARAMS) -> Double = { 1.0 }, override val name: String = hashCode().toString()) : Node() {
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

    }

    inner class Nest(private val childNodes: Collection<Node>, override val name: String = hashCode().toString(), val extractLambdaParameter: (PARAMS) -> Double) :
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

        override val extractAlphaParameter: (PARAMS) -> Double = { 1.0 }// Alpha parameter is only relevant for leaves. and thus not in the constructor
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
            //println(relevantChilds.joinToString(prefix="calcMax = ") { "${it.name} -> ${ln(it.extractAlphaParameter(parameters)) + it.utility}" })
            val x = relevantChilds
                .map { ln(it.extractAlphaParameter(parameters)) + it.utility }
                .sumOf { exp((it - maxUtility) / lambda) }

            utility = maxUtility + lambda * ln(x)
            sum = x
            //println("calcUtility $name: max=$maxUtility x=$x util=$utility sum=$sum")
            return parent
        }

        override fun calculateProbability(parameters: PARAMS) {
            val lambda = extractLambdaParameter(parameters)
            val relevantChilds = childNodes.filter { it.relevantForCalculation }
            relevantChilds.forEach {
                val actualUtil = ln(it.extractAlphaParameter(parameters)) + it.utility
//                println("Probability Calculation for ${it.name} : util = ${it.utility} maxUtil = ${maxUtility} lambda = $lambda sum = $sum (${exp((actualUtil - maxUtility) / lambda) / sum})")
                val utilCalculation = actualUtil.let { d -> exp((d - maxUtility) / lambda) / sum }
                val childProbability =
                    this.probability * utilCalculation
                it.probability = childProbability
            }
//            println("Probability for $name [${relevantChilds.map { "(${it.name} -> ${it.probability})" }}")
            relevantChilds.forEach { it.calculateProbability(parameters) }
        }

        override fun toString(): String {
            return "${name}: (U: $utility P: $probability): Childs: ${childNodes.joinToString(prefix = "[", postfix = "]") { it.name}}"
        }
    }
}
