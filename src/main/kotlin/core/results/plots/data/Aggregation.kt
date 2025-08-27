package core.results.plots.data

import units.UnitIntervalValue
import units.share
import kotlin.math.floor

sealed interface Aggregation<in Y, out V> {
    fun aggregate(values: List<Y>): V
}

data object Count : Aggregation<Any, Int> {
    override fun aggregate(values: List<Any>): Int = values.size
}

data object Sum : Aggregation<Number, Double> {

    override fun aggregate(values: List<Number>): Double =
        values.sumOf { it.toDouble() }
}

open class MinBy<Y, R>(
    private val sortBy: (Y) -> R,
) : Aggregation<Y, Y> where R : Comparable<R> {

    override fun aggregate(values: List<Y>): Y =
        values.minBy { sortBy(it) }
}

class Min<Y> : MinBy<Y, Y>({ it }) where Y : Comparable<Y>

open class MaxBy<Y, R>(
    private val sortBy: (Y) -> R,
) : Aggregation<Y, Y> where R : Comparable<R> {

    override fun aggregate(values: List<Y>): Y =
        values.maxBy { sortBy(it) }
}

class Max<Y> : MaxBy<Y, Y>({ it }) where Y : Comparable<Y>

data object Mean : Aggregation<Number, Double> {
    override fun aggregate(values: List<Number>): Double =
        values.sumOf { it.toDouble() } / values.size
}

open class LowerQuantileBy<Y, R>(
    private val quantile: UnitIntervalValue,
    private val sortBy: (Y) -> R,
) : Aggregation<Y, Y> where R : Comparable<R> {

    override fun aggregate(values: List<Y>): Y {
        val lowerIndex = floor(quantile.toDouble() * (values.size - 1)).toInt()
        return values.sortedBy(sortBy)[lowerIndex]
    }
}

open class LowerQuantile<Y>(
    quantile: UnitIntervalValue,
) : LowerQuantileBy<Y, Y>(quantile, { it }) where Y : Comparable<Y>

class LowerMedianBy<Y, R>(
    sortBy: (Y) -> R,
) : LowerQuantileBy<Y, R>(0.5.share(), sortBy) where R : Comparable<R>

class LowerMedian<Y> : LowerQuantile<Y>(0.5.share()) where Y : Comparable<Y>

open class SummarizeBy<Y, R>(
    private val sortBy: (Y) -> R
) : Aggregation<Y, Summary<Y>> where R : Comparable<R> {

    override fun aggregate(values: List<Y>): Summary<Y> =
        Summary(
            min = MinBy(sortBy).aggregate(values),
            lowerQuart = LowerQuantileBy(0.25.share(), sortBy).aggregate(values),
            median = LowerMedianBy(sortBy).aggregate(values),
            upperQuart = LowerQuantileBy(0.75.share(), sortBy).aggregate(values),
            max = MaxBy(sortBy).aggregate(values)
        )
}

class Summarize<Y> : SummarizeBy<Y, Y>({ it }) where Y : Comparable<Y>
