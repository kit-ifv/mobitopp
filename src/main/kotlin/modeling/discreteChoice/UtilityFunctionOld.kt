package modeling.discreteChoice

import Situation
import Term

import utils.collections.select
import java.util.PriorityQueue
import kotlin.math.exp
import kotlin.math.ln
import kotlin.random.Random

fun interface UtilityFunctionOld<X, P> {
    // TODO debate whether double is the correct return type.
    fun calculateUtility(alternative: X, parameters: P): Double
}



/*
Devcode below---------------------------------------------------------
 */

//class ParametrizedMultinomialLogit<X, S : ChoiceSituation<X>, Q>(private val utilityFunctions: Map<X, ParameterizedUtilityFunction<S, Q>>) {
//    val alternatives = utilityFunctions.keys
//
//    companion object {
//        class LoEgitBuilder<X, S : ChoiceSituation<X>, Q> {
//            private val options: MutableMap<X, ParameterizedUtilityFunction<S, Q>> = mutableMapOf()
//
//            fun option(alternative: X, generator: Q.(S) -> Term<S>) =
//                option(alternative, { this }, generator)
//            fun <T> option(alternative: X, parameters: Q.() -> T, generator: T.(S) -> Term<S>) {
//                options[alternative] = ParameterizedUtilityFunction { x, q ->
//
//                    q.parameters().generator(x.choice).invoke(x.choice)
//
//                }
//
//
//            }
//
//            fun build(): ParametrizedMultinomialLogit<X, S, Q> {
//                return ParametrizedMultinomialLogit(options)
//            }
//        }
//
//        fun <X, S : ChoiceSituation<X>, Q> build(lambda: LoEgitBuilder<X, S, Q>.() -> Unit): ParametrizedMultinomialLogit<X, S, Q> {
//            val builder = LoEgitBuilder<X, S, Q>()
//            builder.apply(lambda)
//            return builder.build()
//        }
//    }
//}
//
//
//class Dbug(
//    val a: Double
//)
//class Wrappa(
//    val sit: ModeSit,
//    val param: PBomb
//)
//open class Pate(
//    protected open val e: Double
//)
//
//class Pete(override val e: Double) : Pate(e) {
//
//}
//
//interface PBomb {
//    val d: Double
//    val AGE: Int
//    fun TRAVEL_TIME(lambda: (ModeSit) -> Double): Double {
//
//
//        return 0.0
//    }
//}
///*
//b + X_1 * b_1 etc.
// */
//
//
//class InversePPa(val p: PBomb)
//data class ModeSit(
//    override val choice: LegacyMode, val otherInfo: OtherInfo,
//    val origin: Zone,
//    val destination: Zone,
//    val impedance: Metrics,
//    val time: Time,
//    val travelTimeShift: Double,
//    val travelCostShift: Double
//) : ChoiceSituation<LegacyMode> {
//    val TRAVEL_TIME = 1.2
//    val TRAVELTIME: (ModeSit) -> Double = {
//        10.0
//    }
//}
//
//data class OtherInfo(
//    val age: Int
//)
//
//private val TRAVEL_TIME = property<ModeSit> {
//    impedance.duration(origin.centroid, destination.centroid, choice, time).inWholeMinutes
//}
//
//
//
//
///**
// * Solves the problem of not being able to write utility function terms using only numbers. Not really though
// */
//private val `Utility：` = property<ModeSit> {
//    0.0
//}
//
//inline fun <T> prop(crossinline compute: T.() -> Number) = property<T>(compute)
//fun tMain() {
//    ParametrizedMultinomialLogit.build<LegacyMode, ModeSit, PBomb> {
//
//        option(LegacyMode.TAXI) {
//            `Utility：` + 1.0 + TRAVEL_TIME
//        }
//        option(LegacyMode.CAR, parameters = { d }) { mode ->
//           U + mode.TRAVEL_TIME
//        }
//        other<PedBomb>(LegacyMode.CAR) {
//            p + p + p + d
//        }
//        other<Dbug>()
//    }
//}

/*
Devcode above----------------------------------------------------------
 */
fun interface DistributionFunctionOld<X, P> {
    fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
        utilityFunction: UtilityFunctionOld<X, P>
    ): Map<X, Double>
}

interface SufficientDistributionFunction<X, P> : DistributionFunctionOld<X, P> {
    val alternatives: Set<X>
    fun calculateProbabilities(alternatives: Set<X>, parameters: P): Map<X, Double>
    fun calculateProbabilities(parameters: P): Map<X, Double> = calculateProbabilities(alternatives, parameters)
    override fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
        utilityFunction: UtilityFunctionOld<X, P>
    ): Map<X, Double> {
        return calculateProbabilities(alternatives, parameters)
    }
}

fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}


class Logit<X, P> : DistributionFunctionOld<X, P> {
    override fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
        utilityFunction: UtilityFunctionOld<X, P>
    ): Map<X, Double> {


        val currentExp = alternatives.associateWith {
            exp(utilityFunction.calculateUtility(it, parameters))
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }

}

class MultinomialLogit<X, P>(private val utilityFunctions: Map<X, UtilityFunctionOld<X, P>>) :
    SufficientDistributionFunction<X, P> {
    override val alternatives: Set<X> = utilityFunctions.keys
    override fun calculateProbabilities(alternatives: Set<X>, parameters: P): Map<X, Double> {
        val utilities = alternatives.associateWith {
            utilityFunctions[it]?.calculateUtility(it, parameters)?.let { utility -> exp(utility) }
                ?: throw NoSuchElementException("Cannot find the requested alternative in the set")
        }
        val sum = utilities.values.sum()
        return utilities.mapValues { it.value / sum }
    }

    companion object {
        class LogitBuilder<X, P> {
            private val options: MutableMap<X, UtilityFunctionOld<X, P>> = mutableMapOf()
            fun option(alternative: X, utilityFunction: UtilityFunctionOld<X, P>) {

                options[alternative] = utilityFunction
            }

            fun <V : Situation<X>> optionE(alternative: V, generator: V.() -> Term<X>) {

                options[alternative.choice] = UtilityFunctionOld { x, _ -> alternative.generator().invoke(x) }
            }

            fun <V : Situation<X>> constant(alternative: V, generator: V.() -> Double) {

                options[alternative.choice] = UtilityFunctionOld { x, _ -> alternative.generator() }
            }

            fun build(): MultinomialLogit<X, P> {
                return MultinomialLogit(options)
            }
        }

        fun <X, P> build(lambda: LogitBuilder<X, P>.() -> Unit): MultinomialLogit<X, P> {
            val builder = LogitBuilder<X, P>()
            builder.apply(lambda)
            return builder.build()
        }
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

    inner class Leaf<X, P>(
        val x: X, private val utilityFunction: UtilityFunctionOld<X, P>,
    ) : Node<X, P>() {
        override var parent: Node<X, P>? = null
        override val childs: Collection<Node<X, P>> = emptySet()
        override var utility: Double? = 0.0
        override var probability: Double = 1.0
        override val level = 0
        override fun calculateUtility(options: Set<X>, parameters: P): Node<X, P>? {
            // If the option is unavailable it should set the utility to null and return no parent
            if (!isAvailable(options)) {
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

        ) : Node<X, P>() {
        override var parent: Node<X, P>? = null
        override var probability: Double = 1.0
        override val level = childs.maxOf { it.level } + 1
        override var utility: Double? = 0.0
        var sum: Double = 0.0
        var maxUtility = 0.0
        override fun calculateUtility(options: Set<X>, parameters: P): Node<X, P>? {
            if (childs.none { it.isAvailable(options) }) {
                utility = null
                return null
            }
            maxUtility = childs.mapNotNull { it.utility }.maxOrNull() ?: 0.0

            val x = childs.filter { it.isAvailable(options) }
                .mapNotNull { it.utility }
                .sumOf { exp((it - maxUtility) / lambdaParameter) }

            utility = maxUtility + lambdaParameter * ln(x)
            sum = x
            return parent?.also { it.calculateUtility(options, parameters) }
        }

        override fun isAvailable(options: Set<X>): Boolean {
            return childs.any { it.isAvailable(options) }
        }

        override fun calculateProbability() {
            childs.forEach {
                val d1 =
                    this.probability * (it.utility?.let { d -> exp((d - maxUtility) / lambdaParameter) / sum } ?: 0.0)
                it.probability = d1
            }
            childs.filter { it.probability > 0.0 }.forEach {
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

    fun add(element: X, function: UtilityFunctionOld<X, P>) {
        val leaf = Leaf(element, function)
        addLeaf(element, leaf)
    }


    operator fun Pair<X, UtilityFunctionOld<X, P>>.unaryPlus() {
        val element = Leaf(this.first, this.second)
        addLeaf(this.first, element)

    }

    private fun addLeaf(element: X, leaf: Leaf<X, P>) {
        childs.add(leaf)
        if (map.contains(element)) {
            // TODO add error behaviour determining either print or crash
            println("Nest structure already contains leaf $element , in a nested logit this will cause inconsistency")
        }
        map[element] = leaf
    }
}


/**
 * TODO this class is not capable of parallel calculations.
 */
class NestedLogitOld<X, P>(nestStructure: NestBuilder<X, P>) : SufficientDistributionFunction<X, P> {

    val map = nestStructure.map
    override val alternatives = map.values.map { it.x }.toSet()
    override fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
    ): Map<X, Double> {
        val leafs = map.values.filter { it.x in alternatives }
        val queue = PriorityQueue<NestBuilder.Node<X, P>>({ a, b -> a.level - b.level })
        lateinit var lastElement: NestBuilder.Node<X, P>
        queue.addAll(leafs)
        while (queue.isNotEmpty()) {
            val n = queue.poll()
            lastElement = n
            val parent = n.calculateUtility(alternatives, parameters)
            parent?.let { queue.add(it) }
        }
        lastElement.calculateProbability()
        return leafs.associate { it.x to it.probability }

    }

    companion object {
        fun <X, P> root(lambda: NestBuilder<X, P>.() -> Unit): NestedLogitOld<X, P> {
            val builder = NestBuilder<X, P>()
            builder.nest(1.0) {
                lambda()
            }

            return NestedLogitOld(builder)
        }
    }
}

fun <X> NestedLogitOld<X, Unit>.calculateProbabilities(alternatives: Set<X>): Map<X, Double> {
    return calculateProbabilities(alternatives, Unit)
}

val GlobalRandomizer = Random(1)

class OtherDiscreteChoiceModel<X, P>(
    private val distributionFunction: DistributionFunctionOld<X, P>,
    private val selectionFunction: SelectionFunction<X> = SelectionFunction { it.select(GlobalRandomizer.nextDouble()) },
    private val utilityFunction: UtilityFunctionOld<X, P>,
) {

    fun select(alternatives: Set<X>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters,
                utilityFunction
            )
        )
    }

    fun select(alternatives: Set<X>, parameters: P, utilityFunction: UtilityFunctionOld<X, P>): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters,
                utilityFunction
            )
        )
    }
}

class DiscreteChoiceModel<X, P>(
    private val distributionFunction: SufficientDistributionFunction<X, P>,
    private val selectionFunction: SelectionFunction<X> = SelectionFunction { it.select(GlobalRandomizer.nextDouble()) },
) {
    fun select(alternatives: Set<X>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters
            )
        )
    }

    fun select(parameters: P): X {
        return selectionFunction.calculateSelection(distributionFunction.calculateProbabilities(parameters))
    }

    fun selectVerbose(parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(parameters).also { println(it) })
    }


}


