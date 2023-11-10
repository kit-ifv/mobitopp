package utils.random

import java.util.*
import kotlin.random.Random

/**
 * Stochastic actor describes actors taking part in stochastic processes.
 * Therefore, they must provide a random generator.
 */
interface StochasticActor {
    val random: Random
}

/**
 * A distribution models a domain of values with different weights.
 * It allows to draw a random value from the modeled domain.
 *
 * @param T the generic type of the values in the distribution
 * @constructor Create empty Random variable
 */
interface Distribution<T> {
    val name: String

    /**
     * Draws a value from this distribution using the given [StochasticActor]'s random generator.
     *
     * @param actor the stochastic actor selecting from the distribution
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(actor: StochasticActor): T = this.drawValue(actor.random.nextDouble())

    /**
     * Draws a value from this distribution using the given [Random] generator.
     *
     * @param random the random generator used to draw a value
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(random: Random): T = this.drawValue(random.nextDouble())

    /**
     * Draws a value from this distribution using the given random number.
     *
     * @param randomNum a random double number in [0, 1)
     * @return a (weighted) random value from this distribution
     */
    fun drawValue(randomNum: Double): T

}

/**
 * A histogram is a distribution of discrete values.
 *
 * @param T the generic type of the values in the histogram
 * @constructor Create a Histogram with the given name.
 *              Values can be given as value distribution, cumulative distribution or set (for a uniform distribution).
 *
 * @property name name of the [Distribution]
 * @property cumulativeDistribution sorted map of ascending cumulative probabilities per value of the distribution
 */
class Histogram<T>(
    override val name: String,
    private val cumulativeDistribution: SortedMap<Double, T>
): Distribution<T> {

    constructor(name: String, distribution: Map<T, Number>) : this(
        name,
        cumulativeDistribution(name, distribution)
    )

    constructor(name: String, values: Set<T>) : this(
        name,
        cumulativeUniformDistribution(name, values)
    )


    override fun drawValue(randomNum: Double): T {
        return cumulativeDistribution.higherEntry(randomNum)?.value
            ?: cumulativeDistribution.let { it[it.lastKey()]!! }
    }

}

/**
 * Verify the given collection is not empty.
 */
private fun <T> verifySize(name: String, values: Collection<T>) {
    require(values.isNotEmpty()) { "At least one element is required to create Histogram $name" }
}

/**
 * Compute the cumulative distribution for the given values + weights.
 *
 * @param name name of the Histogram
 * @param distribution a set of values and their weights
 * @return the cumulative probability distribution of the given values
 */
private fun <T> cumulativeDistribution(name: String, distribution: Map<T, Number>): SortedMap<Double, T> {
    verifySize(name, distribution.keys)

    val sum = distribution.values.sumOf { it.toDouble() }

    //if any weight is infinite, remove finite weights, remaining have equal probability
    if (sum.isInfinite()) {
        val infiniteValues = distribution.filter { it.value.toDouble().isInfinite() }.keys
        return cumulativeUniformDistribution("$name (infinite weight sum)", infiniteValues)
    }

    var current = 0.0
    return sequence{
        distribution.map {
            val increment = it.value.toDouble() / sum

            if (increment > 0) {
                current += increment
                yield(current to it.key)
            }
        }
    }.toMap().toSortedMap()

}

/**
 * Create a uniform cumulative distribution for the given values.
 *
 * @param name name of the Histogram
 * @param values a set of values
 * @return the uniform cumulative probability distribution of the given values
 */
private fun <T> cumulativeUniformDistribution(name: String, values: Set<T>): SortedMap<Double, T> {
    verifySize(name, values)
    val share = 1.0 / values.size
    return values.mapIndexed { index, value -> (1+index)*share to value }.toMap().toSortedMap()
}


/**
 * Find the first entry in this sorted map, that has a key higher than the given key.
 * Returns null if no such entry exists.
 *
 * @param key key for which the next highest entry is to be found
 * @param S the generic type of the sorted key-value map
 * @param K the generic key type
 * @param V the generic value type
 * @return first entry a higher key or null
 */
fun <S, K, V> S.higherEntry(key: K): Map.Entry<K, V>? where S: SortedMap<K, V>, K: Comparable<K> =
    this.entries.find { it.key > key }
