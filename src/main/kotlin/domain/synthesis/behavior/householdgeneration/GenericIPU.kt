package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import utils.collections.invertMap
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.time.measureTime

fun interface GenericIPU {

    fun run(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)

    /**
     * THe most generic version of IPU does not care about unifying vectors. If you put in multiple identical vecotrs
     * well then thats on you. Returns the observed values and expected values.
     */
    fun <I> calculateDirect(
        vectors: Collection<ScalableVector>,
        rules: Collection<Rule<I>>,
    ): List<Pair<Rule<I>, Double>> {
        val observers = rules.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, vectors)
        }
        run(vectors, observers)

        return rules.zip(observers.map { it.actual })
    }

    fun <I> calculateUnfiltered(elements: Collection<I>, rules: Collection<Rule<I>>): List<IPUOutput<I>> {
        val vectorMapping = elements.associateWith { rules.toScalableVector(it) }
        calculateDirect(vectorMapping.values, rules)
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
        ipuCalculationCallback: (List<Pair<Rule<I>, Double>>) -> Unit = {},
    ): List<IPUOutput<Signature>> {
        return internalGroupedCalculation(elements, rules, ipuCalculationCallback) {
            it.keys.map { IPUOutput(it.signature, it.scalar) }
        }
    }

    private fun <X, I> internalGroupedCalculation(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        ipuCalculationCallback: (List<Pair<Rule<I>, Double>>) -> Unit = {},
        resultConverter: (Map<ScalableVector, List<I>>) -> X
    ): X {
        val vectorMapping = elements.associateWith { rules.toScalableVector(it) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.keys

        val errors = calculateDirect(uniqueVectors, rules)
        ipuCalculationCallback(errors)
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

        val tabooList = GenericIPU { vectors, observers ->

            var counter = 0
            while (counter < 1000) {
                val observerCopy = observers.toMutableList()
                while (observerCopy.isNotEmpty() && counter < 1000) {
                    val best = observers.maxBy { it.absoluteDifference }
                    best.optimize()
                    observerCopy.remove(best)
                    counter++
                }
            }
        }
        val limitOptimization = GenericIPU { vectors, observers ->
            val indexedObservers = observers.withIndex().toMutableList()
            val counters = indexedObservers.map { 100 }.toIntArray()
            while (indexedObservers.size >= 2) {
                val target = indexedObservers.maxBy { (i, observer) -> observer.absoluteDifference }
                val (idx, candidate) = target
                candidate.optimize()
                counters[idx]--

                if (counters[idx] <= 0) {
                    indexedObservers.remove(target)
                }
            }
        }

        val functionalTabooList = GenericIPU { vectors, observers ->

            var counter = 0
            while (counter < 1000) {
                val observerCopy = observers.toMutableList()
                while (observerCopy.isNotEmpty() && counter < 1000) {
                    val best = observerCopy.maxBy { it.absoluteDifference }
                    best.optimize()
                    observerCopy.remove(best)
                    counter++
                }
            }
        }

        val aggressiveStomping = GenericIPU { vectors, observers ->

            var counter = 0
            while (counter < 1000 * observers.size) {

                val best = observers.maxBy { it.absoluteDifference }
                best.optimize()

                counter++
            }
        }
    }
}
fun GenericIPU.withLogging(path: Path) = PerformanceLoggingIPU(this, path)
data class PerformanceLoggingIPU(val original: GenericIPU, val path: Path) : GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val duration = measureTime { original.run(vectors, observers) }
        val text = "$original; ${vectors.size}; ${observers.size}; $duration\n"
        if (path.exists()) {
            path.appendText(text)
        } else {
            path.parent.createDirectories()
            path.writeText(text)
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
