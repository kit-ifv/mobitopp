package core.results.plots.data

import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share
import kotlin.math.floor

/**
 * Interface for aggregating a list of values of type [Y] into a single value of type [V].
 *
 * @param Y The type of input values to be aggregated.
 * @param V The type of the resulting aggregated value.
 */
sealed interface Aggregation<in Y, out V> {
    /**
     * Aggregates the given [values] into a single value.
     *
     * @param values The list of values to aggregate.
     * @return The aggregated value.
     */
    fun aggregate(values: List<Y>): V
}

/**
 * An [Aggregation] that counts the number of elements in the list.
 */
data object Count : Aggregation<Any, Int> {
    /**
     * Counts the number of elements in [values].
     *
     * @param values The list of values to count.
     * @return The number of elements in the list.
     */
    override fun aggregate(values: List<Any>): Int = values.size
}

/**
 * An [Aggregation] that calculates the sum of numerical values.
 */
data object Sum : Aggregation<Number, Double> {

    /**
     * Calculates the sum of [values].
     *
     * @param values The list of numbers to sum.
     * @return The sum as a [Double].
     */
    override fun aggregate(values: List<Number>): Double = values.sumOf { it.toDouble() }
}

/**
 * An [Aggregation] that finds the minimum element based on a given [sortBy] selector.
 *
 * @param Y The type of elements in the list.
 * @param R The type used for comparison.
 * @property sortBy A function that maps an element to a comparable value.
 */
open class MinBy<Y, R>(private val sortBy: (Y) -> R) : Aggregation<Y, Y> where R : Comparable<R> {

    /**
     * Finds the minimum element in [values] according to [sortBy].
     *
     * @param values The list of elements.
     * @return The minimum element.
     */
    override fun aggregate(values: List<Y>): Y = values.minBy { sortBy(it) }
}

/**
 * An [Aggregation] that finds the minimum element in a list of comparable elements.
 *
 * @param Y The type of comparable elements.
 */
class Min<Y> : MinBy<Y, Y>({ it }) where Y : Comparable<Y>

/**
 * An [Aggregation] that finds the maximum element based on a given [sortBy] selector.
 *
 * @param Y The type of elements in the list.
 * @param R The type used for comparison.
 * @property sortBy A function that maps an element to a comparable value.
 */
open class MaxBy<Y, R>(private val sortBy: (Y) -> R) : Aggregation<Y, Y> where R : Comparable<R> {

    /**
     * Finds the maximum element in [values] according to [sortBy].
     *
     * @param values The list of elements.
     * @return The maximum element.
     */
    override fun aggregate(values: List<Y>): Y = values.maxBy { sortBy(it) }
}

/**
 * An [Aggregation] that finds the maximum element in a list of comparable elements.
 *
 * @param Y The type of comparable elements.
 */
class Max<Y> : MaxBy<Y, Y>({ it }) where Y : Comparable<Y>

/**
 * An [Aggregation] that calculates the arithmetic mean of numerical values.
 */
data object Mean : Aggregation<Number, Double> {
    /**
     * Calculates the mean of [values].
     *
     * @param values The list of numbers.
     * @return The arithmetic mean.
     */
    override fun aggregate(values: List<Number>): Double = values.sumOf { it.toDouble() } / values.size
}

/**
 * An [Aggregation] that finds the element at a specific [quantile] after sorting by [sortBy].
 *
 * @param Y The type of elements in the list.
 * @param R The type used for comparison.
 * @property quantile The quantile to find (e.g., 0.5 for median).
 * @property sortBy A function that maps an element to a comparable value.
 */
open class LowerQuantileBy<Y, R>(private val quantile: UnitIntervalValue, private val sortBy: (Y) -> R) :
    Aggregation<Y, Y> where R : Comparable<R> {

    /**
     * Finds the element at the specified quantile in [values].
     *
     * @param values The list of elements.
     * @return The element at the specified quantile.
     */
    override fun aggregate(values: List<Y>): Y {
        val lowerIndex = floor(quantile.toDouble() * (values.size - 1)).toInt()
        return values.sortedBy(sortBy)[lowerIndex]
    }
}

/**
 * An [Aggregation] that finds the element at a specific [quantile] in a list of comparable elements.
 *
 * @param Y The type of comparable elements.
 * @param quantile The quantile to find.
 */
open class LowerQuantile<Y>(quantile: UnitIntervalValue) :
    LowerQuantileBy<Y, Y>(quantile, {
        it
    }) where Y : Comparable<Y>

/**
 * An [Aggregation] that finds the lower median element after sorting by [sortBy].
 *
 * @param Y The type of elements.
 * @param R The type used for comparison.
 * @param sortBy A function that maps an element to a comparable value.
 */
class LowerMedianBy<Y, R>(sortBy: (Y) -> R) : LowerQuantileBy<Y, R>(0.5.share(), sortBy) where R : Comparable<R>

/**
 * An [Aggregation] that finds the lower median element in a list of comparable elements.
 *
 * @param Y The type of comparable elements.
 */
class LowerMedian<Y> : LowerQuantile<Y>(0.5.share()) where Y : Comparable<Y>

/**
 * An [Aggregation] that provides a [Summary] of elements based on a [sortBy] selector.
 *
 * @param Y The type of elements.
 * @param R The type used for comparison.
 * @property sortBy A function that maps an element to a comparable value.
 */
open class SummarizeBy<Y, R>(private val sortBy: (Y) -> R) : Aggregation<Y, Summary<Y>> where R : Comparable<R> {

    /**
     * Creates a [Summary] of [values] based on [sortBy].
     *
     * @param values The list of elements.
     * @return A [Summary] containing min, lower quartile, median, upper quartile, and max.
     */
    override fun aggregate(values: List<Y>): Summary<Y> = Summary(
        min = MinBy(sortBy).aggregate(values),
        lowerQuart = LowerQuantileBy(0.25.share(), sortBy).aggregate(values),
        median = LowerMedianBy(sortBy).aggregate(values),
        upperQuart = LowerQuantileBy(0.75.share(), sortBy).aggregate(values),
        max = MaxBy(sortBy).aggregate(values),
    )
}

/**
 * An [Aggregation] that provides a [Summary] of comparable elements.
 *
 * @param Y The type of comparable elements.
 */
class Summarize<Y> : SummarizeBy<Y, Y>({ it }) where Y : Comparable<Y>
