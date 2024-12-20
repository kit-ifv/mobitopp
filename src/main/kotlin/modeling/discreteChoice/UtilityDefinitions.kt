package modeling.discreteChoice

import java.util.*
import kotlin.math.exp
import kotlin.math.ln

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<SIT, PARAMS> {
    fun calculateUtility(alternative: SIT, parameterObject: PARAMS): Double
}


interface AllocatedDistributionFunction<SIT : ChoiceSituation<*>, PARAMS> : DistributionFunction<SIT, PARAMS> {
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {

        return calculateProbabilities(
            alternatives.associateWith { translation(it).calculateUtility(it, parameters) },
            parameters
        )
    }


}


interface ParameterizedDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> {
    /**
     * Specific implementations of discrete choice models need a layer of abstraction to determine the correct utility
     * function that should be applied to a target situation. As Example take mode choice, where each mode is typically
     * associated with its own utility funciton, whereas destination choice usually has a single function applied to all
     * targets individually. By providing the layer of abstraction any discrete choice scenario should be representable
     */
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>

    // TODO maybe change return type from X to S, that way X could be dropped from the class generics?
    fun calculateProbabilities(
        alternatives: Set<SIT>,
        parameters: PARAMS,
    ): Map<X, Double>

}

/**
 * A distribution function takes in a collection of situations with their associated utility functions already calculated,
 * a parameter object and returns a map of calculated probabilities from the given alternatives
 */
fun interface DistributionFunction<SIT, PARAMS> {
    fun calculateProbabilities(
        evaluators: Map<SIT, Double>,
        parameters: PARAMS
    ): Map<SIT, Double>
}

fun interface UtilityFunctionAssociation<SIT, PARAMS> {
    fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS>
}

interface MapBasedAssociation<SIT, PARAMS> : UtilityFunctionAssociation<SIT, PARAMS> {
    val map: Map<SIT, UtilityFunction<SIT, PARAMS>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        return map[to] ?: throw NoSuchElementException("No utility function located for $to")
    }
}

interface RuleBasedAssociation<SIT : ChoiceSituation<*>, PARAMS> : UtilityFunctionAssociation<SIT, PARAMS>,
    AllocatedDistributionFunction<SIT, PARAMS> {
    val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        return rules.first { it.first.invoke(to) }.second
    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return associateFunction(target)
    }
}

class NestedLogit<X : Any, SIT : ChoiceSituation<X>, PARAMS>(
    override val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>> = emptyList(),
    private val leafs: Map<X, NestStructure<SIT, PARAMS>.Leaf>,
    private val root: NestStructure<SIT, PARAMS>.Nest
) :
    RuleBasedAssociation<SIT, PARAMS>, AllocatedDistributionFunction<SIT, PARAMS> {


    private inner class AssociatedSituation(
        val sit: SIT,
        val leaf: NestStructure<SIT, PARAMS>.Leaf,
        val utility: Double
    ) {
        val probability get() = leaf.probability
        fun initializeUtility(): NestStructure<SIT, PARAMS>.Nest? {
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
        val queue = PriorityQueue<NestStructure<SIT, PARAMS>.Nest> { a, b -> a.level - b.level }
        lateinit var lastElement: NestStructure<SIT, PARAMS>.Node
        queue.addAll(nextNests)

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
        class NestedLogitBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS>(
            val map: MutableMap<X, NestStructure<SIT, PARAMS>.Leaf>,
            val rules: MutableList<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>>
        ) {

            val childs: MutableList<NestStructure<SIT, PARAMS>.Node> = mutableListOf()


            fun nest(
                lambda: PARAMS.() -> Double,
                functor: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
            ): NestStructure<SIT, PARAMS>.Nest {
                val builder = NestedLogitBuilder(map, rules)
                builder.functor()
                val childNodes = builder.build()
                map.putAll(builder.map)
                val nest = NestStructure<SIT, PARAMS>().Nest(childNodes, lambda)
                childNodes.forEach { it.parent = nest }
                childs.add(nest)
                return nest
            }

            fun nest(
                lambda: Double,
                functor: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
            ): NestStructure<SIT, PARAMS>.Nest {
                return nest({ lambda }, functor)
            }

            fun option(x: X, generator: PARAMS.(SIT) -> Double) {
                val utilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
                    generator.invoke(
                        parameterObject,
                        alternative
                    )
                }
                addUtilityFunction(x, utilityFunction)
            }

            fun <P> option(x: X, parameters: PARAMS.() -> P, generator: P.(SIT) -> Double) {
                val utilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
                    generator.invoke(
                        parameterObject.parameters(),
                        alternative
                    )
                }
                addUtilityFunction(x, utilityFunction)
            }

            fun option(option: SIT, generator: PARAMS.(SIT) -> Double) {
                option(option.choice, generator)
            }

            fun <P> option(option: SIT, parameters: PARAMS.() -> P, generator: P.(SIT) -> Double) {
                option(option.choice, parameters, generator)
            }

            private fun addUtilityFunction(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
                rules.add({ sit: SIT -> sit.choice == x } to utilityFunction)
                val element = NestStructure<SIT, PARAMS>().Leaf()
                childs.add(element)
                map[x] = element
            }

            fun build(): MutableList<NestStructure<SIT, PARAMS>.Node> {
                return childs
            }


        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> build(lambda: NestedLogitBuilder<X, SIT, PARAMS>.() -> Unit): NestedLogit<X, SIT, PARAMS> {
            val builder = NestedLogitBuilder<X, SIT, PARAMS>(mutableMapOf(), mutableListOf())
            val root = builder.nest(1.0) {
                lambda()
            }


            return NestedLogit(builder.rules, builder.map, root)
        }

    }
}


class NestStructure<X, PARAMS> {


    fun build(leafs: Set<Leaf>) {
        leafs.groupBy { it.parent }
    }

    abstract inner class Node {
        /**
         * Level represents the depth of the alternative in the Nest Structure, lower level nests need to
         * be calculated for their utility first.
         */
        abstract val level: Int
        var relevantForCalculation = false
        abstract var parent: Nest?
        abstract fun reset()
        var utility: Double = 0.0
        var probability: Double = 0.0

        //        abstract fun calculateUtility(parameters: PARAMS): Node?
        abstract fun calculateProbability(parameters: PARAMS)
    }

    inner class Leaf() : Node() {
        override var parent: Nest? = null
        override val level: Int = 0

        override fun reset() {
            relevantForCalculation = false
        }

        override fun calculateProbability(parameters: PARAMS) {
            // A leaf cannot feasibly calculate its own probability.
            return
        }

        fun initializeUtility(utility: Double): Nest? {
            this.utility = utility
            relevantForCalculation = true
            parent?.relevantForCalculation = true
            return parent
        }
    }

    inner class Nest(val childNodes: Collection<Node>, val extractLambdaParameter: (PARAMS) -> Double) : Node() {
        override var parent: Nest? = null
        override val level = childNodes.maxOf { it.level } + 1
        var maxUtility = 0.0
        var sum = 0.0
        override fun reset() {
            relevantForCalculation = false
            childNodes.forEach { it.reset() }
        }

        fun calculateUtility(parameters: PARAMS): Nest? {
            val lambda = extractLambdaParameter(parameters)
            val relevantChilds = childNodes.filter { it.relevantForCalculation }
            if (relevantChilds.isEmpty()) {
                throw IllegalStateException("Never should a calculate Utility be called when the childs are irrelevant")
            }


            maxUtility = relevantChilds.maxOf { it.utility }

            val x = relevantChilds
                .map { it.utility }
                .sumOf { exp((it - maxUtility) / lambda) }

            utility = maxUtility + lambda * ln(x)
            sum = x
            return parent
        }

        override fun calculateProbability(parameters: PARAMS) {
            val lambda = extractLambdaParameter(parameters)
            val relevantChilds = childNodes.filter { it.relevantForCalculation }
            relevantChilds.forEach {
                val d1 =
                    this.probability * (it.utility.let { d -> exp((d - maxUtility) / lambda) / sum })
                it.probability = d1
            }
            relevantChilds.forEach { it.calculateProbability(parameters) }
        }

    }
}