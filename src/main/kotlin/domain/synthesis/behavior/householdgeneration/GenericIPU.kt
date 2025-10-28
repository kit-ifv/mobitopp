package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import utils.collections.invertMap

fun interface GenericIPU {

    fun run(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)

    /**
     * THe most generic version of IPU does not care about unifying vectors. If you put in multiple identical vecotrs
     * well then thats on you
     */
    fun <I> calculate(
        vectors: Collection<ScalableVector>,
        rules: Collection<Rule<I>>,
    ) {
        val observers = rules.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, vectors)
        }
        run(vectors, observers)

        return
    }

    fun <I> calculateUnfiltered(elements: Collection<I>, rules: Collection<Rule<I>>): List<IPUOutput<I>> {
        val vectorMapping = elements.associateWith { rules.toScalableVector(it) }
        calculate(vectorMapping.values, rules)
        return vectorMapping.map { (k, v) ->
            IPUOutput(k, v.scalar)
        }
    }

    fun <I> calculate(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
    ): List<IPUOutput<List<I>>> {
        return internalGroupedCalculation(elements, rules) {
            it.map { (k, v) ->
                IPUOutput(v, k.scalar)
            }
        }
    }
    fun <I> calculateSignature(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
    ): List<IPUOutput<Signature>> {
        return internalGroupedCalculation(elements, rules) {
            it.keys.map { IPUOutput(it.signature, it.scalar) }
        }
    }

    private fun <X, I> internalGroupedCalculation(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        resultConverter: (Map<ScalableVector, List<I>>) -> X
    ): X {
        val vectorMapping = elements.associateWith { rules.toScalableVector(it) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.keys

        calculate(uniqueVectors, rules)
        return resultConverter(inverseMap)
    }
    companion object {
        /**
         * The original algorithm of hierarchical IPU using no external interrupt criterion.
         */
        @Suppress("MagicNumber")
        val legacy = GenericIPU { vectors, observers ->

            repeat(1000) {
                observers.forEach {
                    it.optimize()
                }
            }
        }

        @Suppress("MagicNumber")
        val newAlgorithm = GenericIPU { vectors, observers ->
            var counter = 0

            while (observers.maxOf { it.quotientDifference } >= 1.001 && counter < 1000) {
                val sorted = observers.sortedByDescending { it.quotientDifference }
                sorted.forEach {
                    it.optimize()
                }
                counter++
            }
        }
    }
}

data class IPUOutput<I>(
    val element: I,
    val amount: Double,
) {
    fun discretize(target: Int) = IntegerIPUOutput(element, target)
}

data class IntegerIPUOutput<I>(
    val element: I,
    val amount: Int,
)
