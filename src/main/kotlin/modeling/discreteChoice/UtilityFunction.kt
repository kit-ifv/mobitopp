package modeling.discreteChoice

import units.UnitIntervalValue
import java.util.PriorityQueue
import kotlin.math.exp
import kotlin.math.ln

fun interface UtilityFunction<X, P> {
    // TODO debate whether double is the correct return type.
    fun calculateUtility(alternative: X, parameters: P): Double
}

fun interface SingleElementUtilityFunction<X, P> {
    fun X.calculateUtility(parameters: P): Double
}

fun interface DistributionFunction<X, P> {
    fun calculateProbabilities(alternatives: Set<X>, parameters: P, utilityFunction: UtilityFunction<X, P>): Map<X, Double>
}
interface SufficientDistributionFunction<X, P> {
    val alternatives: Set<X>
    fun calculateProbabilities(alternatives: Set<X>, parameters: P): Map<X, Double>
    fun calculateProbabilities(parameters: P): Map<X, Double> = calculateProbabilities(alternatives, parameters)
}
fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}


class Logit<X, P> : DistributionFunction<X, P> {
    override fun calculateProbabilities(alternatives: Set<X>, parameters: P, utilityFunction: UtilityFunction<X, P>): Map<X, Double> {


        val currentExp = alternatives.associateWith {
            exp(utilityFunction.calculateUtility(it, parameters))
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }

}

class NestBuilder<X, P> {
    val map: MutableMap<X, Leaf<X, P>> = mutableMapOf()
    val childs: MutableList<Node<X, P>> = mutableListOf()
    abstract class Node<X, P> {
        abstract val level: Int
        abstract var probability: Double
        abstract val childs: Collection<Node<X, P>>
        abstract var parent: Node<X, P>?
        abstract var utility: Double?
        abstract fun isAvailable(options: Set<X>): Boolean
        abstract fun calculateUtility(options: Set<X>, parameters: P): Node<X, P>?
        abstract fun calculateProbability()
    }
    inner class Leaf<X, P>(val x: X, private val utilityFunction: UtilityFunction<X, P>,
    ): Node<X, P>() {
        override var parent: Node<X, P>? = null
        override val childs: Collection<Node<X, P>> = emptySet()
        override var utility: Double? = 0.0
        override var probability: Double = 1.0
        override val level = 0
        override fun calculateUtility(options: Set<X>, parameters: P): Node<X, P>? {
            // If the option is unavailable it should set the utility to null and return no parent
            if(!isAvailable(options)) {
                utility = null
                return null
            }
            utility = utilityFunction.calculateUtility(x, parameters)
            return parent?.also { it.calculateUtility(options, parameters) }

        }

        override fun isAvailable(options: Set<X>): Boolean {
            return x in options
        }

        override fun calculateProbability() {
            return
        }
    }

    inner class Intermediate<X, P>(
        override val childs: Collection<Node<X, P>>, val lambdaParameter: Double,

        ): Node<X, P>() {
        override var parent: Node<X, P>? = null
        override var probability: Double = 1.0
        override val level = childs.maxOf{it.level} + 1
        override var utility: Double? = 0.0
        var sum: Double = 0.0
        var maxUtility = 0.0
        override fun calculateUtility(options: Set<X>, parameters: P): Node<X, P>? {
            if(childs.none { it.isAvailable(options) }) {
                utility = null
                return null
            }
            // TODO increase numeric stability.

            maxUtility = childs.mapNotNull { it.utility }.maxOrNull() ?: 0.0

            val x = childs.filter { it.isAvailable(options) }
                .mapNotNull { it.utility }
                .sumOf { exp((it - maxUtility) / lambdaParameter) }

            utility = maxUtility + lambdaParameter * ln(x)
            sum = x
            return parent?.also {it.calculateUtility(options, parameters)}
        }

        override fun isAvailable(options: Set<X>): Boolean {
            return childs.any { it.isAvailable(options) }
        }

        override fun calculateProbability() {
            childs.forEach {
                val d1 = this.probability * (it.utility?.let { d -> exp((d - maxUtility) / lambdaParameter) / sum } ?: 0.0)
                it.probability = d1
            }
            childs.filter{it.probability > 0.0}.forEach {
                it.calculateProbability()
            }

        }
    }
    fun build(): List<Node<X, P>> {
        return childs
    }
    fun nest(lambda: Double, functor: NestBuilder<X, P>.() -> Unit): Node<X, P> {
        val builder = NestBuilder<X, P>()
        builder.functor()
        val childNodes = builder.build()
        map.putAll(builder.map)
        val nest = Intermediate(lambdaParameter = lambda, childs = childNodes)
        childNodes.forEach { it.parent = nest }
        childs.add(nest)
        return nest
    }
    fun add(element: X, function: UtilityFunction<X, P>) {
        val leaf = Leaf(element, function)
        addLeaf(element, leaf)
    }


    operator fun Pair<X, UtilityFunction<X, P>>.unaryPlus() {
        val element = Leaf(this.first, this.second)
        addLeaf(this.first, element)

    }
    private fun addLeaf(element: X, leaf: Leaf<X, P>) {
        childs.add(leaf)
        if(map.contains(element)) {
            // TODO add error behaviour determining either print or crash
            println("Nest structure already contains leaf $element , in a nested logit this will cause inconsistency")
        }
        map[element] = leaf
    }
}


/**
 * TODO this class is not capable of parallel calculations.
 */
class NestedLogit<X, P> (nestStructure: NestBuilder<X, P>): SufficientDistributionFunction<X, P> {

    val map = nestStructure.map
    override val alternatives = map.values.map{it.x}.toSet()
    override fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
    ): Map<X, Double> {
        val leafs = map.values.filter {it.x in alternatives}
        val queue = PriorityQueue<NestBuilder.Node<X, P>>( {a, b -> a.level - b.level})
        lateinit var lastElement: NestBuilder.Node<X, P>
        queue.addAll(leafs)
        while(queue.isNotEmpty()) {
            val n = queue.poll()
            lastElement = n
            val parent = n.calculateUtility(alternatives, parameters)
            parent?.let { queue.add(it) }
        }
        lastElement.calculateProbability()
        return leafs.associate{ it.x to it.probability }

    }

    companion object {
        fun <X, P> root(lambda: NestBuilder<X, P>.() -> Unit): NestedLogit<X, P> {
            val builder = NestBuilder<X, P>()
            builder.nest(1.0) {
                lambda()
            }

            return NestedLogit(builder)
        }
    }
}

fun <X> NestedLogit<X, Unit>.calculateProbabilities(alternatives: Set<X>): Map<X, Double> {
    return calculateProbabilities(alternatives, Unit)
}

class DiscreteChoiceModel<X, P>(
    private val distributionFunction: SufficientDistributionFunction<X, P>,
    private val selectionFunction: SelectionFunction<X>,
) {
    fun select(alternatives: Set<X>, parameters: P): X {
        return selectionFunction.calculateSelection(distributionFunction.calculateProbabilities(alternatives, parameters))
    }
    fun select(parameters: P): X {
        return selectionFunction.calculateSelection(distributionFunction.calculateProbabilities(parameters))
    }

    fun selectVerbose(parameters: P): X {
        return selectionFunction.calculateSelection(distributionFunction.calculateProbabilities(parameters).also{println(it)})
    }


}


