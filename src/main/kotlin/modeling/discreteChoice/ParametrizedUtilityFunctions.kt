package modeling.discreteChoice

import Lambdas
import domain.enums.Mode
import usecases.LegacyMode
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.exp
import kotlin.math.ln
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit


/**
 * A choice situation may contain additional appended information but is basically only a wrapper for [X]
 * Thus the equals and hash implementaion of that type can be used for mapping to a certain utility function.
 */
abstract class ChoiceSituation<out X : Any> {
    abstract val choice: X

    override fun equals(other: Any?): Boolean {
        if (other !is ChoiceSituation<*>) return false
        return choice == other.choice
    }

    override fun hashCode(): Int {
        return choice.hashCode()
    }

}

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<SIT, PARAMS> {
    fun calculateUtility(alternative: SIT, parameterObject: PARAMS): Double
}


interface AllocatedDistributionFunction<SIT : ChoiceSituation<*>, PARAMS> : DistributionFunction<SIT, PARAMS> {
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {
        return calculateProbabilities(alternatives.map { it to translation(it) }, parameters)
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
 * A distribution function takes in a collection of situations with their associated utility functions, a parameter
 * object and returns a map of calculated probabilities from the given alternatives
 */
fun interface DistributionFunction<SIT, PARAMS> {
    fun calculateProbabilities(
        evaluators: Collection<Pair<SIT, UtilityFunction<SIT, PARAMS>>>,
        parameters: PARAMS
    ): Map<SIT, Double>
}

private class SimpleLogit<SIT : ChoiceSituation<*>, PARAMS> : DistributionFunction<SIT, PARAMS> {
    override fun calculateProbabilities(
        evaluators: Collection<Pair<SIT, UtilityFunction<SIT, PARAMS>>>,
        parameters: PARAMS
    ): Map<SIT, Double> {
        val results = evaluators.associate { it.first to it.second.calculateUtility(it.first, parameters) }
        val sum = results.values.sum()
        return results.mapValues { (_, v) -> v / sum }
    }

}

class PNestBuilder<SIT, PARAMS> {
    val map: MutableMap<SIT, Leaf<out SIT>> = mutableMapOf()
    val childs: MutableList<Node<SIT>> = mutableListOf()

    abstract inner class Node<in SIT> {
        abstract val level: Int
        abstract var probability: Double
        abstract val childs: Collection<Node<SIT>>
        abstract var parent: Node<SIT>?
        abstract var utility: Double?
        abstract fun isAvailable(options: Set<SIT>): Boolean
        abstract fun calculateUtility(options: Set<SIT>, parameters: PARAMS): Node<SIT>?
        abstract fun calculateProbability()
    }

    inner class Leaf<Q : SIT>(
        val x: Q, private val utilityFunction: UtilityFunction<Q, PARAMS>,
    ) : Node<SIT>() {
        override var parent: Node<SIT>? = null
        override val childs: Collection<Node<SIT>> = emptySet()
        override var utility: Double? = 0.0
        override var probability: Double = 1.0
        override val level = 0
        override fun calculateUtility(options: Set<SIT>, parameters: PARAMS): Node<SIT>? {
            // If the option is unavailable it should set the utility to null and return no parent
            if (!isAvailable(options)) {
                utility = null
                return null
            }
            utility = utilityFunction.calculateUtility(x, parameters)
            return parent?.also { it.calculateUtility(options, parameters) }

        }

        override fun isAvailable(options: Set<SIT>): Boolean {
            return x in options
        }

        override fun calculateProbability() {
            return
        }
    }

    inner class Intermediate(
        override val childs: Collection<Node<SIT>>, val lambdaParameter: Double,

        ) : Node<SIT>() {
        override var parent: Node<SIT>? = null
        override var probability: Double = 1.0
        override val level = childs.maxOf { it.level } + 1
        override var utility: Double? = 0.0
        var sum: Double = 0.0
        var maxUtility = 0.0
        override fun calculateUtility(options: Set<SIT>, parameters: PARAMS): Node<SIT>? {
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

        override fun isAvailable(options: Set<SIT>): Boolean {
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

    fun build(): List<Node<SIT>> {
        return childs
    }

    fun nest(lambda: Double, functor: PNestBuilder<SIT, PARAMS>.() -> Unit): Node<SIT> {
        val builder = PNestBuilder<SIT, PARAMS>()
        builder.functor()
        val childNodes = builder.build()
        map.putAll(builder.map)
        val nest = Intermediate(lambdaParameter = lambda, childs = childNodes)
        childNodes.forEach { it.parent = nest }
        childs.add(nest)
        return nest
    }

    fun <X : SIT> optionsafe(alternative: X, generator: PARAMS.(X) -> Double) =
        optionsafe(alternative, { this }, generator)

    fun optionOthea(determinator: SIT.() -> Boolean, generator: PARAMS.(SIT) -> Double) =
        optionOther(determinator, generator)

    fun <Q : SIT> optionOther(determinator: Q.() -> Boolean, generator: PARAMS.(Q) -> Double) {
//        optionsafe(alternative, { this }, generator)
    }

    fun <T, Q : SIT> optionsafe(alternative: Q, parameters: PARAMS.() -> T, generator: T.(Q) -> Double) {

        val utilityFunction = UtilityFunction<Q, PARAMS> { x, q ->

            q.parameters().generator(x)

        }
        val leaf = Leaf(alternative, utilityFunction)
        addLeaf(alternative, leaf)


    }

    fun option(element: SIT, function: UtilityFunction<SIT, PARAMS>) {
        val leaf = Leaf(element, function)
        addLeaf(element, leaf)
    }


    operator fun Pair<SIT, UtilityFunction<SIT, PARAMS>>.unaryPlus() {
        val element = Leaf(this.first, this.second)
        addLeaf(this.first, element)

    }

    private fun <Q : SIT> addLeaf(element: Q, leaf: Leaf<Q>) {
        childs.add(leaf)
        if (map.contains(element)) {
            // TODO add error behaviour determining either print or crash
            println("Nest structure already contains leaf $element , in a nested logit this will cause inconsistency")
        }
        map[element] = leaf
    }
}


private class PNestDistributionFunction<SIT : ChoiceSituation<*>, PARAMS> : AllocatedDistributionFunction<SIT, PARAMS> {
    val map: MutableMap<SIT, UtilityFunction<SIT, PARAMS>> = mutableMapOf()
    val thirdMap: Map<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>> = emptyMap()
    val otherMap: Map<SIT, PNestBuilder<SIT, PARAMS>.Leaf<SIT>> = emptyMap()
    override fun calculateProbabilities(
        evaluators: Collection<Pair<SIT, UtilityFunction<SIT, PARAMS>>>,
        parameters: PARAMS
    ): Map<SIT, Double> {
        return TODO()
    }

    fun translate(target: SIT): UtilityFunction<SIT, PARAMS> {
        return map.getOrPut(target) {
            val match = thirdMap.entries.first { (k) -> k(target) }
            map[target] = match.value
            match.value
        }
    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return map[target] ?: throw NoSuchElementException("Nope")
    }

}

private class NumericStableNestLogit<SIT : ChoiceSituation<*>, PARAMS> : AllocatedDistributionFunction<SIT, PARAMS> {
    val map: Map<SIT, UtilityFunction<SIT, PARAMS>> = emptyMap()
    override fun calculateProbabilities(
        evaluators: Collection<Pair<SIT, UtilityFunction<SIT, PARAMS>>>,
        parameters: PARAMS
    ): Map<SIT, Double> {
        val exponents = evaluators.associate { it.first to exp(it.second.calculateUtility(it.first, parameters)) }
        val sum = exponents.values.sum()

        return exponents.mapValues { it.value / sum }

    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return map[target] ?: throw NoSuchElementException("No utility function found in map")
    }
}


private class MLogit<X : Any, S : ChoiceSituation<X>, Q>
    (private val utilityFunctions: Map<X, UtilityFunction<S, Q>>) :
    ParameterizedDistributionFunction<X, S, Q> {
    override fun translation(target: S): UtilityFunction<S, Q> {
        return utilityFunctions[target.choice]
            ?: throw NoSuchElementException("No Utility Function is added for option ${target.choice}")
    }

    override fun calculateProbabilities(alternatives: Set<S>, parameters: Q): Map<X, Double> {
        return alternatives.associate { it.choice to translation(it).calculateUtility(it, parameters) }
    }

    companion object {
        class MyBuilder<X : Any, S : ChoiceSituation<X>, Q> {
            private val options: MutableMap<X, UtilityFunction<S, Q>> = mutableMapOf()

            fun option(alternative: X, generator: Q.(S) -> Double) =
                option(alternative, { this }, generator)

            fun <T> option(alternative: X, parameters: Q.() -> T, generator: T.(S) -> Double) {
                options[alternative] = UtilityFunction { x, q ->

                    q.parameters().generator(x)

                }


            }

            fun build(): MLogit<X, S, Q> {
                return MLogit(options)
            }
        }

        fun <X : Any, S : ChoiceSituation<X>, Q> build(lambda: MyBuilder<X, S, Q>.() -> Unit): MLogit<X, S, Q> {
            val builder = MyBuilder<X, S, Q>()
            builder.apply(lambda)
            return builder.build()
        }
    }

}

private class ZoneLogit<X : Any, S : ChoiceSituation<X>, Q>(
    val utilitFunction: UtilityFunction<S, Q>,
    val calculation: DistributionFunction<S, Q> = SimpleLogit()
) :
    ParameterizedDistributionFunction<X, S, Q>, DistributionFunction<S, Q>  by calculation {
    override fun translation(target: S): UtilityFunction<S, Q> {
        return utilitFunction
    }

    override fun calculateProbabilities(alternatives: Set<S>, parameters: Q): Map<X, Double> {
        val calculation = calculateProbabilities(alternatives.map{it to translation((it))}, parameters)
        return calculation.mapKeys { (k, _) -> k.choice }
    }
}

class PNestedLogit<X, P>(nestStructure: PNestBuilder<X, P>) :
    SufficientDistributionFunction<X, P> {

    val map = nestStructure.map
    override val alternatives = map.values.map { it.x }.toSet()
    override fun calculateProbabilities(
        alternatives: Set<X>,
        parameters: P,
    ): Map<X, Double> {
        val leafs = map.values.filter { it.x in alternatives }
        val queue = PriorityQueue<PNestBuilder<X, P>.Node<X>> { a, b -> a.level - b.level }
        lateinit var lastElement: PNestBuilder<X, P>.Node<X>
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
        fun <X, P: LambdaParameterObject> root(lambda: PNestBuilder<X, P>.() -> Unit): PNestedLogit<X, P> {
            val builder = PNestBuilder<X, P>()
            builder.nest(1.0) {
                lambda()
            }

            return PNestedLogit(builder)
        }
    }

}

interface LambdaParameterObject {
    val lambdaRoot: Double
}

interface TwoLevelParameterObject: LambdaParameterObject {
    val lambdaCar: Double
}

open class Legg(override val choice: LegacyMode) : ChoiceSituation<LegacyMode>() {
    val number = 1.0

    val taxi get() = choice == LegacyMode.TAXI
}

class BetterLegg(choice: LegacyMode) : Legg(choice) {
    val explodotron = 9001
}


private class Parameters(
    val asc_car: Double,
    val asc_ped: Double, override val lambdaRoot: Double, override val lambdaCar: Double,
): TwoLevelParameterObject {
    fun toPedestrianParameters(): Double {
        return 1.0
    }
}

private open class TestMode : Mode {
    override val requiresVehicleTakeAlong: Boolean = false

    override fun encode(): Int {
        return -1
    }


}

private object Concretization : TestMode() {
    val shift = 1.0
}

private class ModeChoice(override val choice: LegacyMode) : ChoiceSituation<LegacyMode>() {
    val travelTime =
        0.minutes // Example parameter that could be attached to the ChoiceSituation Object and thus referenced later

}

private class GenerousModeChoice(override val choice: TestMode) : ChoiceSituation<TestMode>() {
    val travelTime =
        0.minutes // Example parameter that could be attached to the ChoiceSituation Object and thus referenced later

}

private val TAXI: Legg.() -> Boolean = { taxi }
private val BETTER: BetterLegg.() -> Boolean = { taxi }
fun main() {
    val choice = TestMode()
    val otherchoice = TestMode()
    val result = MLogit.build<TestMode, GenerousModeChoice, Parameters> {
        option(Concretization) {
            it.travelTime.toDouble(DurationUnit.DAYS) * 1.0 + 1.09
        }
        option(choice) {
            1.0 - 99.99
        }
    }

    val re3sult = PNestedLogit.root<TestMode, Parameters> {
        nest(lambda = 1.0) {
//            option(GenerousModeChoice(Concretization)) {
//                1.0 + this.asc_car + it.travelTime.toDouble(DurationUnit.DAYS) * (
//                        1 +
//                                0 +
//                                3 +
//                                4)
//            }
            optionsafe(Concretization) {
                1.0 + it.shift
            }
            optionsafe(choice) {
                1.0
            }
            optionsafe(otherchoice) {
                1.0
            }
        }
    }

    val re = PNestedLogit.root<Legg, Parameters> {
//        optionsafe()
//        optionsafe(LegacyMode.TAXI) {
//            0.0 + 1.9
//        }
        optionOther(TAXI) {
            1.0 + asc_car
        }
        optionOther(BETTER) {
            it.explodotron + 1.0
        }
        optionOther(BETTER) {
            1.0
        }


    }


    val situation = GenerousModeChoice(Concretization)
    val otherSituation = GenerousModeChoice(choice)
    val parameters = Parameters(1.0, 12.0, 1.0, 1.0)
    println(re.calculateProbabilities(setOf(Legg(LegacyMode.TAXI)), parameters))
    val type = result.calculateProbabilities(setOf(situation, otherSituation), parameters)

    println(re3sult.calculateProbabilities(setOf(choice, otherchoice, Concretization), parameters))


}