package core.results.plots

import core.modelsteps.Resource
import core.results.plots.Plotter.DataPoint
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.Position
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.feature.position
import org.jetbrains.kotlinx.kandy.letsplot.layers.barsH
import org.jetbrains.kotlinx.kandy.letsplot.layers.boxes
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol
import org.jetbrains.kotlinx.kandy.letsplot.y
import org.jetbrains.kotlinx.kandy.util.color.Color
import units.UnitIntervalValue
import units.share
import kotlin.collections.map
import kotlin.math.abs
import kotlin.math.floor

/**
 * A Plotter creates a visual/statistical representation of a list of entities.
 * Entities can be grouped into traces.
 * For each trace, the x and y values are derived from the entity.
 * The y value can be aggregated using an [Aggregation] formula.
 * If no aggregation is desired (pure x and y values), [Aggregation.AllValues] can be applied.
 * Finally, an attribute of the entity can be applied to determine the color scale.
 * These properties defining the plot data are bundled in a [PlotDataSpecification].
 *
 * Additionally, to the data specification a [PlotStyling] specification must be provided.
 * It specifies properties like the axis labels or in which order traces should be arranged.
 *
 * Subclasses must implement the transformation of these data traces into a plot.
 *
 * @param ENTITY the generic type of entities being transformed into a plot
 * @param GROUPING the generic type by which entities are grouped into traces
 * @param X_AXIS the generic type of x-axis values
 * @param Y_ATT the generic type of y-attributes
 * @param Y_AXIS the generic type of y-axis attributes after aggregation. This might be different to Y_ATT,
 *  since aggregating (e.g. mean) produces a [Double] result while the input y-value can be any [Number].
 *  Or when [Aggregation.Count]ing the occurrence of x-values, Y_ATT is not required ([Unit]) while the y-axis (Y_AXIS)
 *  are of type [Int].
 * @param COLOR_ATT the generic type by which the color of data points should be represented
 *
 * @property style the [PlotStyling] describing labels and layout properties
 * @property values the [PlotDataSpecification] describing how to extract the required data from an entity
 *  and how to aggregate the y-axis
 */
@Suppress("LongParameterList")
abstract class Plotter<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS, COLOR_ATT>(
    protected val style: PlotStyling<GROUPING, X_AXIS, COLOR_ATT>,
    protected val values: PlotDataSpecification<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS, COLOR_ATT>,
    protected val comparisonData: ComparisonDataSpecification<Nothing, GROUPING, X_AXIS, Y_AXIS>? = null,
) {
    val name: String = style.name

    /**
     * A trace is a grouped list of data points.
     * The group can be identified by the traces key.
     *
     * @param G the generic type of the trace/group key
     * @param X the generic type of the data points' x-axis values
     * @param Y the generic type of the data points' y-axis values
     * @param C the generic type of the data points' color values
     * @property key the value by which the data points were grouped into this trace
     * @property dataPoints the x/y/color data points extracted from the entities and grouped into this trace
     */
    protected data class Trace<G, X, Y, C>(val key: G, val dataPoints: List<DataPoint<X, Y, C>>)

    /**
     * A DataPoint hods the x, y and color value of one entry in a trace.
     *
     * @param X the generic type of the x-axis value
     * @param Y the generic type of the x-axis value
     * @param C the generic type of the color-axis value
     * @property x the x-axis value
     * @property y the y-axis value
     * @property c the color-axis value
     */
    protected data class DataPoint<X, Y, C>(val x: X, val y: Y, val c: C, val arranged: Boolean = false)

    /**
     * Plot the data given in the [PlotDataSpecification].
     * First the entities are grouped, the x and y values are extracted.
     * Y-values are aggregated according to the [Aggregation] strategy.
     * This strategy also extracts the color-axis value for the datapoint
     * (either from the entity itself when using [Aggregation.AllValues], or from the x,y or group value).
     *
     * The created data traces are transformed into a plot in the subclasses' [Plotter.plotTraces].
     */
    fun plot() {
        plotTraces(dataTraces(), comparisonTraces())
    }

    private fun dataTraces(): List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>> {
        val traces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>
        values.apply {
            traces = entities().groupBy(groupBy).entries.map { (group, elements) ->

                val xValues = elements.map { xAttribute(it) to it }

                val trace = Trace(
                    key = group,
                    dataPoints = aggregation.aggregate(
                        yAttribute = yAttribute,
                        elementValues = xValues,
                        colorBy = coloringForGroup(group)
                    ).map { (x, y, c) ->
                        DataPoint(x, y, c)
                    }
                )
                trace
            }
        }
        return traces
    }

    private fun coloringForGroup(group: GROUPING): (ENTITY?, X_AXIS, Y_AXIS) -> COLOR_ATT = { e, x, y ->
        val color = values.colorBy(e, group, x, y)
        color
    }

    private fun comparisonTraces() = comparisonData?.let { comparison ->
        comparison.rawData().groupBy { it.first /*GROUPING*/ }.entries.map { (group, rawDataPoints) ->
            Trace(
                key = group,
                dataPoints = rawDataPoints.map {
                    DataPoint(x = it.second, y = it.third, c = values.colorBy(null, group, it.second, it.third))
                }
            )
        }
    }

    /**
     * Plot the given traces.
     *
     * @param traces the traces to be plotted.
     */
    protected abstract fun plotTraces(
        traces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>,
        comparisonTraces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>? = null,
    )

    protected fun<LIST> mapTraces(
        traces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>,
        comparisonTraces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>?,
        mapping: (DataPoint<X_AXIS, Y_AXIS, COLOR_ATT>) -> LIST
    ): List<LIST> {
        return traces.flatMap {
            it.dataPoints.map(mapping)
        } + (comparisonTraces?.flatMap { it.dataPoints.map(mapping)} ?: emptyList())
    }

}

/**
 * A plot data specification defines how the plot data is to be extracted from the entities.
 *
 * @param ENTITY the generic type of entities to be plotted
 * @param GROUPING the generic type by which entities are grouped
 * @param X_AXIS the generic type of x-axis values
 * @param Y_ATT the generic type of y-attributes
 * @param Y_AXIS the generic type of y-axis attributes after aggregation
 * @param COLOR_ATT the generic type by which the color of data points should be represented
 *
 * @property entities a function returning a list of entities to be plotted
 * @property xAttribute a function extracting the x-axis value from an entity
 * @property yAttribute a function extracting the y-attribute value from an entity
 * @property aggregation the aggregation strategy to be applied to y-values
 * @property groupBy a function extracting the value by which entities will be grouped
 * @property colorBy a function determining the color-axis value of data points
 */
data class PlotDataSpecification<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS, COLOR_ATT>(
    val entities: () -> List<ENTITY>,
    val xAttribute: (ENTITY) -> X_AXIS,
    val yAttribute: (ENTITY) -> Y_ATT,
    val aggregation: Aggregation<Y_ATT, Y_AXIS>,
    val groupBy: (ENTITY) -> GROUPING,
    val colorBy: (ENTITY?, GROUPING, X_AXIS, Y_AXIS) -> COLOR_ATT,
)

data class ComparisonDataSpecification<in T, GROUPING, X_AXIS, Y_AXIS>(
    private val resource: Resource<T>,
    val groupBy: (T) -> GROUPING,
    val xAxis: (T) -> X_AXIS,
    val yAxis: (T) -> Y_AXIS,
    val labelPrefix: String = "Expected:"
) {
    val name: String = resource.name
    fun rawData(): List<Triple<GROUPING, X_AXIS, Y_AXIS>> = resource.elements.map {
        Triple(groupBy(it), xAxis(it), yAxis(it))
    }.toList()
}

/**
 * A PlotStyling defines the labels of the plot
 * and [Ordering]s by which the groups and x-axis
 * should be arranged.
 *
 * @param G the generic type for grouping
 * @param X the generic type for x-axis values
 */
interface PlotStyling<G, X, C> {
    val name: String
    val xLabel: String
    val yLabel: String
    val groupLabel: String
    val colorLabel: String
    val xOrder: Ordering<X>
    val groupOrder: Ordering<G>
    val colorMap: (C) -> RGB
}

/**
 * Summary of statistical values.
 *
 * @param T the generic type of the summarized values
 * @property min the minimum value
 * @property lowerQuart the lower quartile value
 * @property median the median value
 * @property upperQuart the upper quartile value
 * @property max the maximum value
 * @constructor Create empty Summary
 */
data class Summary<T>(
    val min: T,
    val lowerQuart: T,
    val median: T,
    val upperQuart: T,
    val max: T,
)

/**
 * Ordering strategy for arranging elements.
 *
 * @param T the generic type of the elements to be arranged
 */
sealed class Ordering<T> {

    /**
     * Arbitrary ordering without any specific criteria.
     *
     * @param X the generic type of the elements
     */
    class Arbitrary<X> : Ordering<X>() {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> X): List<S> = elements.toList()
    }

    /**
     * Ascending ordering for comparable elements.
     *
     * @param T the generic type of the elements
     */
    class Ascending<T> : Ordering<T>() where T : Comparable<T> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedBy(by)
    }

    /**
     * Descending ordering for comparable elements.
     *
     * @param T the generic type of the elements
     */
    class Descending<T> : Ordering<T>() where T : Comparable<T> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedByDescending(by)
    }

    /**
     * Ascending ordering by a specified key.
     *
     * @param T the generic type of the elements to be ordered
     * @param R the generic type of the key used for ordering
     * @property key a function that extracts the key from an element of type T
     */
    class AscendingBy<T, R>(
        private val key: (T) -> R
    ) : Ordering<T>() where R : Comparable<R> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> =
            elements.sortedBy { s -> key(by(s)) }
    }

    /**
     * Descending ordering by a specified key.
     *
     * @param T the generic type of the elements to be ordered
     * @param R the generic type of the key used for ordering
     * @property key a function that extracts the key from an element of type T
     */
    class DescendingBy<T, R>(
        private val key: (T) -> R
    ) : Ordering<T>() where R : Comparable<R> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> =
            elements.sortedByDescending { s -> key(by(s)) }
    }

    /**
     * Arrange other elements based on this ordering strategy
     * based on the transformation of other elements to this strategies type.
     *
     * @param S the generic type of the other elements
     * @param elements the other elements to be arranged
     * @param by the function to extract the value by which the elements are arranged
     * @return the arranged list of other elements
     */
    abstract fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S>

    /**
     * Arrange elements based on this ordering strategy.
     *
     * @param elements the elements to be arranged
     * @return the arranged list of elements
     */
    fun arrange(elements: Collection<T>): List<T> = arrangeBy(elements) { it }
}

/**
 * Abstract aggregation strategy for y-values.
 *
 * @param I the generic input type of the y-attribute values
 * @param O the generic output type of the y-axis values after aggregation
 */
sealed class Aggregation<I, O> {

//    class ToDouble<A, Y>(private val delegate: Aggregation<A, Y>): Aggregation<A, Double>() where Y: Number {
//
//        override fun <E, X, C> aggregate(
//            yAttribute: (E) -> A,
//            elementValues: List<Pair<X, E>>,
//            colorBy: (E?, X, Double) -> C
//        ): List<Triple<X, Double, C>> {
//            val colorWrap: (E?, X, Y) -> C = { e, x, n -> colorBy(e, x, n.toDouble())}
//            return delegate.aggregate(yAttribute, elementValues, colorWrap).map {
//                Triple(it.first, it.second.toDouble(), it.third)
//            }
//        }
//
//        override fun <X> aggregateCluster(x: X, values: List<A>): Double =
//            delegate.aggregateCluster(x, values).toDouble()
//
//    }

    /**
     * AllValues aggregation returns all y-values as is.
     *
     * @param Y the generic input and output type of the y-attribute values
     */
    class AllValues<Y> : Aggregation<Y, Y>() {

        override fun <E, X, C> aggregate(
            yAttribute: (E) -> Y,
            elementValues: List<Pair<X, E>>,
            colorBy: (E?, X, Y) -> C
        ): List<Triple<X, Y, C>> {
            return elementValues.map {
                val y = yAttribute(it.second)
                Triple(it.first, y, colorBy(it.second, it.first, y))
            }
        }

        override fun <X> aggregateCluster(x: X, values: List<Y>): Y =
            throw UnsupportedOperationException("aggregateCluster should not be called on AllValues!")
    }

    /**
     * Count aggregation the occurrences of each x-value.
     * The y-attribute is omitted.
     */
    data object Count : Aggregation<Unit, Int>() {

        override fun <E, X, C> aggregate(
            yAttribute: (E) -> Unit,
            elementValues: List<Pair<X, E>>,
            colorBy: (E?, X, Int) -> C,
        ): List<Triple<X, Int, C>> {
            return elementValues.groupingBy { it.first }.eachCount().toList().map {
                Triple(it.first, it.second, colorBy(null, it.first, it.second))
            }
        }

        override fun <X> aggregateCluster(x: X, values: List<Unit>): Int =
            throw UnsupportedOperationException("aggregateCluster should not be called on Count!")
    }

    /**
     * Sum aggregation sums up the y-values.
     */
    data object Sum : Aggregation<Number, Double>() {
        override fun <X> aggregateCluster(x: X, values: List<Number>): Double =
            values.sumOf { it.toDouble() }
    }

    /**
     * MinBy aggregation finds the minimum y-value based on a specified key.
     *
     * @param T the generic type of the y-attribute values
     * @param R the generic type of the key used for comparison
     * @property sortBy a function to extract the key from a y-value
     */
    open class MinBy<T, R>(
        private val sortBy: (T) -> R
    ) : Aggregation<T, T>() where R : Comparable<R> {
        override fun <X> aggregateCluster(x: X, values: List<T>): T =
            values.minBy { sortBy(it) }
    }

    /**
     * Min aggregation finds the minimum y-value.
     *
     * @param T the generic type of the y-attribute values
     */
    class Min<T : Comparable<T>> : MinBy<T, T>({ it })

    /**
     * MaxBy aggregation finds the maximum y-value based on a specified key.
     *
     * @param T the generic type of the y-attribute values
     * @param R the generic type of the key used for comparison
     * @property sortBy a function to extract the key from a y-value
     */
    open class MaxBy<T, R>(
        private val sortBy: (T) -> R
    ) : Aggregation<T, T>() where R : Comparable<R> {
        override fun <X> aggregateCluster(x: X, values: List<T>): T =
            values.maxBy(sortBy)
    }

    /**
     * Max aggregation finds the maximum y-value.
     *
     * @param T the generic type of the y-attribute values
     */
    class Max<T : Comparable<T>> : MaxBy<T, T>({ it })

    /**
     * Mean aggregation calculates the mean of the y-values.
     */
    data object Mean : Aggregation<Number, Double>() {
        override fun <X> aggregateCluster(x: X, values: List<Number>): Double =
            values.sumOf { it.toDouble() } / values.size
    }

    /**
     * LowerMedianBy aggregation finds the lower median y-value based on a specified key.
     *
     * @param T the generic type of the y-attribute values
     * @param R the generic type of the key used for comparison
     * @param sortBy a function to extract the key from a y-value
     */
    class LowerMedianBy<T, R>(
        sortBy: (T) -> R,
    ) : LowerQuantileBy<T, R>(0.5.share(), sortBy) where R : Comparable<R>

    /**
     * LowerMedian aggregation finds the lower median y-value.
     *
     * @param T the generic type of the y-attribute values
     */
    class LowerMedian<T : Comparable<T>> : LowerQuantileBy<T, T>(0.5.share(), { it })

    /**
     * LowerQuantile aggregation finds the lower quantile y-value.
     *
     * @param T the generic type of the y-attribute values
     * @param quantile the quantile value to be used for aggregation
     */
    class LowerQuantile<T : Comparable<T>>(quantile: UnitIntervalValue) : LowerQuantileBy<T, T>(quantile, { it })

    /**
     * LowerQuantileBy aggregation finds the lower quantile y-value based on a specified key.
     *
     * @param T the generic type of the y-attribute values
     * @param R the generic type of the key used for comparison
     * @property quantile the quantile value to be used for aggregation
     * @property sortBy a function to extract the key from a y-value
     */
    open class LowerQuantileBy<T, R>(
        private val quantile: UnitIntervalValue,
        private val sortBy: (T) -> R,
    ) : Aggregation<T, T>() where R : Comparable<R> {

        override fun <X> aggregateCluster(x: X, values: List<T>): T {
            val lowerIndex = floor(quantile.toDouble() * (values.size - 1)).toInt()
            return values.sortedBy(sortBy)[lowerIndex]
        }
    }

    /**
     * SummarizeBy aggregation provides summary statistics:
     * (min, lower quartile, median, upper quartile, max)
     * based on a specified key.
     *
     * @param T the generic type of the y-attribute values
     * @param R the generic type of the key used for comparison
     * @property sortBy a function to extract the key from a y-value
     */
    open class SummarizeBy<T, R>(
        private val sortBy: (T) -> R
    ) : Aggregation<T, Summary<T>>() where R : Comparable<R> {

        override fun <X> aggregateCluster(x: X, values: List<T>): Summary<T> =
            Summary(
                min = MinBy(sortBy).aggregateCluster(x, values),
                lowerQuart = LowerQuantileBy(0.25.share(), sortBy).aggregateCluster(x, values),
                median = LowerMedianBy(sortBy).aggregateCluster(x, values),
                upperQuart = LowerQuantileBy(0.75.share(), sortBy).aggregateCluster(x, values),
                max = MaxBy(sortBy).aggregateCluster(x, values)
            )
    }

    /**
     * Summarize aggregation provides summary statistics
     * (min, lower quartile, median, upper quartile, max).
     *
     * @param T the generic type of the y-attribute values
     */
    class Summarize<T : Comparable<T>> : SummarizeBy<T, T>({ it })

    /**
     * Aggregates the provided element values based on the y-attribute.
     *
     * @param E the type of the elements in the collection
     * @param X the type of the x-axis values
     * @param C the type of the color-axis values
     * @param yAttribute a function to extract the y-attribute from an element
     * @param elementValues a list of pairs containing x and element values
     * @param colorBy a function to determine the color for each aggregated value
     * @return a list of triples containing x, aggregated y, and color-axis values
     */
    open fun <E, X, C> aggregate(
        yAttribute: (E) -> I,
        elementValues: List<Pair<X, E>>,
        colorBy: (E?, X, O) -> C
    ): List<Triple<X, O, C>> {
        return elementValues.groupBy({ it.first }, { yAttribute(it.second) }).map {
            val value = aggregateCluster(it.key, it.value)
            Triple(it.key, value, colorBy(null, it.key, value))
        }
    }

    /**
     * Aggregates a cluster of values for a single x-value.
     *
     * @param X the type of the x-axis values
     * @param x the x-value for which the aggregation is performed
     * @param values a list of y-values to be aggregated
     * @return the aggregated y-value
     */
    abstract fun <X> aggregateCluster(x: X, values: List<I>): O
}

abstract class ScalablePlotter<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS: Number, COLOR_ATT>(
    style: PlotStyling<GROUPING, X_AXIS, COLOR_ATT>,
    values: PlotDataSpecification<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS, COLOR_ATT>,
    comparisonData: ComparisonDataSpecification<Nothing, GROUPING, X_AXIS, Y_AXIS>? = null,
    protected val relative: Boolean = true,
    protected val normalize: Boolean = true
) : Plotter<ENTITY, GROUPING, X_AXIS, Y_ATT, Y_AXIS, COLOR_ATT>(style, values, comparisonData) {

    protected fun valueSpaceTransformation(traces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>): (Pair<X_AXIS, Y_AXIS>) -> Double {
        val normalized: (Pair<X_AXIS, Number>) -> Double = if (normalize) {
            val xCounts = traces.flatMap {
                it.dataPoints
            }.groupBy({ it.x }, { it.y }).map { (x, y) ->
                x to y.sumOf { abs(it.toDouble()) }
            }.toMap()

            val func  = ({ (x, y):  Pair<X_AXIS, Number> -> y.toDouble() / (xCounts[x] ?: 1.0) })
            func
        }  else {
            { (_, y) -> y.toDouble() }
        }

        val transformY = if (relative) {
            val totalYSum = traces.sumOf { it.dataPoints.sumOf { p -> abs(p.y.toDouble()) } }
            val relative = ({ (_, y): Pair<X_AXIS, Number> -> y.toDouble() / totalYSum })
            {(x, y): Pair<X_AXIS, Number> -> relative(Pair(x,normalized(Pair(x, y)))) }
            //relative
        } else {
            normalized
        }
        return transformY
    }

    protected fun arrangeTraces(
        xValues: List<X_AXIS>,
        traces: List<Trace<GROUPING, X_AXIS, Y_AXIS, COLOR_ATT>>,
        defaultValue: Y_AXIS
    ) = style.groupOrder.arrangeBy(traces) { it.key }.map { trace ->
            val valuesByX = trace.dataPoints.associateBy { it.x }
            val transformY: (Pair<X_AXIS, Y_AXIS>) -> Double = valueSpaceTransformation(traces)

            val transformedTrace = Trace(
                key = trace.key,
                dataPoints = xValues.map { x ->
                    val point = valuesByX[x]
                    val mappedPoint = point?.let {
                        DataPoint(x, transformY(x to it.y), it.c)
                    } ?: DataPoint(x, 0.0, values.colorBy(null, trace.key, x, defaultValue), arranged = true)
                    mappedPoint
                }
            )
            transformedTrace
        }

}


/**
 * CountPlotter is an abstract [Plotter] for creating plots that count occurrences of values.
 *
 * It processes the traces and can turn the counted values into relative values
 * (along the x-axis, for all groups combined).
 *
 * Also, the values can be normalized along the y-axis (for each occurring x value).
 * Normalize overwrites, relative!
 *
 * @param E the type of elements in the data set
 * @param G the type of the group key
 * @param X the type of the x-axis values
 * @param C the type of the color values
 * @property relative indicates if the count should be relative to the total count
 * @property normalize indicates if the counts should be normalized per x-value
 * @param style the styling configuration for the plot
 * @param values the data specification for the plot
 */
@Suppress("LongParameterList")
abstract class CountPlotter<E, G, X, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, Unit, Int, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Int>? = null,
    relative: Boolean = true,
    normalize: Boolean = true,
) : ScalablePlotter<E, G, X, Unit, Int, C>(
    style = style,
    values = values,
    comparisonData = comparisonData,
    relative = relative,
    normalize = normalize
) {

    protected val yLabel: String = when (relative to normalize) {
        false to false -> "absolute count"
        true to false -> "relative share [%]"
        else -> "relative share [% normalized per ${style.xLabel}]"
    }

    override fun plotTraces(
        traces: List<Trace<G, X, Int, C>>,
        comparisonTraces: List<Trace<G, X, Int, C>>?,
    ) {
        val xValues = traces.flatMap { trace -> trace.dataPoints.map { it.x } }.distinct().let {
            style.xOrder.arrange(it)
        }

        val transformedTraces = arrangeTraces(xValues, traces, 0)

        val arrangedComparisonData = comparisonTraces?.let { arrangeTraces(xValues, it, 0) }

        plot(xValues, transformedTraces, arrangedComparisonData).save(
            "${name.replace(" ", "_")}.png",
            path = "results"
        )
    }

    protected fun colorScale(
        transformedTraces: List<Trace<G, X, Double, C>>?,
        comparisonTraces: List<Trace<G, X, Double, C>>?
    ): List<Pair<String, RGB>> {

        val colorValueOf = {trace: Trace<G, X, Double, C> ->
            trace.dataPoints.find {!it.arranged}?.c ?: trace.dataPoints[0].c
        }

        val colors = transformedTraces?.map {
            val key = it.key.toString()
            // as only one color is chosen per trace, the color of the trace is the color of the data point with the highest y-value,
            // corresponding to the highest count is chosen.
            val dataPointColor = colorValueOf(it)
            val colorMap = style.colorMap
            val color = colorMap(dataPointColor)
            val pair: Pair<String, RGB> = key to color
            pair
        } ?: emptyList()

        val comparisonColors = comparisonTraces?.map {
                val key = comparisonData?.labelPrefix + it.key.toString()
                val dataPointColor = colorValueOf(it)
                val colorMap = style.colorMap
                val color = colorMap(dataPointColor)
                val pair = key to color.scaleLightness(0.8)
                pair
            } ?: emptyList()
        val allColors = colors + comparisonColors
        return allColors
    }

    /**
     * Plots the data on a graph.
     *
     * @param sortedXValues the sorted x-axis values
     * @param transformedTraces the transformed traces with processed y-values
     * @return the plot object
     */
    protected abstract fun plot(
        sortedXValues: List<X>,
        transformedTraces: List<Trace<G, X, Double, C>>,
        comparisonTraces: List<Trace<G, X, Double, C>>?,
    ): Plot
}

private const val GROUP_COL = "group"
private const val SHARE_COL = "share"
private const val X_COL = "x"
private const val Y_COL = "y"
private const val COLOR_COL = "color"
private const val ORIGINAL_COL = "original"
private const val COMPARISON_COL = "comparison"
private const val TYPE_COL = "type"

/**
 * HistogramPlotter is a plotter for creating histogram plots.
 *
 * @param T the type of the comparison data
 * @param E the type of elements in the data set
 * @param G the type of the group key
 * @param X the type of the x-axis values
 * @param C the type of the color values
 * @param style the styling configuration for the plot
 * @param values the data specification for the plot
 * @param relative indicates if the count should be relative to the total count
 * @param normalize indicates if the counts should be normalized per x-value
 */
class HistogramPlotter<T, E, G, X, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, Unit, Int, C>,
    comparisonData: ComparisonDataSpecification<T, G, X, Int>? = null,
    relative: Boolean = true,
    normalize: Boolean = true,
) : CountPlotter<E, G, X, C>(
    style = style,
    values = values,
    comparisonData = comparisonData,
    relative = relative,
    normalize = normalize,
) {

    /**
     * Plots the histogram data on the graph.
     *
     * @param sortedXValues the sorted x-axis values
     * @param transformedTraces the transformed traces with processed y-values
     * @return the histogram plot object
     */
    @Suppress("MagicNumber")
    override fun plot(
        sortedXValues: List<X>,
        transformedTraces: List<Trace<G, X, Double, C>>,
        comparisonTraces: List<Trace<G, X, Double, C>>?,
    ): Plot {

        val xValues = addXValueIfCompared(comparisonTraces, sortedXValues)
        val includesComparison = xValues.size > sortedXValues.size
        val gatheredTraceDf = gather(xValues, transformedTraces, includesComparison, isComparison = false)
        val datasetTrace = gatheredTraceDf.add(List(gatheredTraceDf.rowsCount()) { ORIGINAL_COL }.toColumn(TYPE_COL))

        val compCol = comparisonData?.name ?: COMPARISON_COL
        val gatheredCompDf = gather(xValues, comparisonTraces, includesComparison, isComparison = true)
        val datasetComp = gatheredCompDf.add(List(gatheredCompDf.rowsCount()) { compCol }.toColumn(TYPE_COL))

        val combinedDataset = datasetTrace.join(datasetComp, X_COL, GROUP_COL, SHARE_COL, TYPE_COL, type = JoinType.Full)

        return createPlot(combinedDataset, transformedTraces, comparisonTraces)
    }

    private fun createPlot(dataset: AnyFrame,
                           transformedTraces: List<Trace<G, X, Double, C>>,
                           comparisonTraces: List<Trace<G, X, Double, C>>?): Plot {
        val colors = colorScale(transformedTraces, comparisonTraces).map {
            it.first to it.second.toColor()
        }.toTypedArray()
        return dataset.groupBy(TYPE_COL).plot {
            barsH {
                alpha = 0.8

                y(X_COL) {
                    axis.name = style.xLabel
                }

                x(SHARE_COL) {
                    axis.name = yLabel
                }

                fillColor(GROUP_COL) {
                    @Suppress("SpreadOperator")
                    scale = categorical(
                        *colors
                    )
                    legend.name = style.groupLabel
                }
                // When writing this, borderLine.type only made it solid. Could
                // not get it to be dashed for the comparison data.
                if (comparisonData != null) {
                    borderLine.color(TYPE_COL) {
                        scale = categorical(
                            COMPARISON_COL to Color.GREY,
                            ORIGINAL_COL to Color.BLACK
                        )
                        legend.name = TYPE_COL
                    }
                }

                position = Position.stack()
            }
            layout.title = name
            layout.size = 1200 to 600
          }
    }

    private fun gather(xValues: List<Any?>, traces:  List<Trace<G, X, Double, C>>?, includesComparison: Boolean, isComparison: Boolean): AnyFrame {

        val df = dataFrameOf(
            X_COL to xValues
        )

        val dataframe =
            if( includesComparison) {
                traces?.let{ addComparisonTracesAsColumns(it, df, isComparison)}
            }
        else {
            traces?.let{ addNoComparisonTracesAsColumns(it, df)}
        }

        val columns = setOf(
            *traces?.toTypedArray() ?: emptyArray(),
        ).map { it.key.toString() }.toTypedArray()

        val gathered =  dataframe?.gather(*columns)?.into(
            keyColumn = GROUP_COL,
            valueColumn = SHARE_COL
        )?.select(X_COL, SHARE_COL, GROUP_COL)

        val remapped = if (isComparison && gathered != null) {
            val remappedGroup = gathered[GROUP_COL].map { comparisonData?.labelPrefix + it.toString() }
            gathered.remove(GROUP_COL).add(remappedGroup)
        } else {
            gathered
        }


        return remapped ?:
            DataFrame.empty()
            .add(emptyList<Any>().toColumn(X_COL))
            .add(emptyList<Any>().toColumn(SHARE_COL))
            .add(emptyList<Any>().toColumn(GROUP_COL))
    }

    private fun addComparisonTracesAsColumns(
        transformedTraces: List<Trace<G, X, Double, C>>,
        df: AnyFrame,
        isComparison: Boolean
    ): AnyFrame {
        var df1 = df
        val mapping = {dp: DataPoint<X, Double, C> -> if (isComparison) listOf(0, dp.y) else listOf(dp.y, 0)}
        transformedTraces.forEach { trace ->
            val column = trace.dataPoints.flatMap(mapping).toColumn(trace.key.toString())
            df1 = df1.add(column)
        }
        return df1
    }

    private fun addNoComparisonTracesAsColumns(
        transformedTraces: List<Trace<G, X, Double, C>>,
        df: AnyFrame
    ): AnyFrame {
        var df1 = df
        transformedTraces.forEach { trace ->
            val column = trace.dataPoints.map { it.y }.toColumn(trace.key.toString())
            df1 = df1.add(column)
        }
        return df1
    }

    @Suppress("MagicNumber")
    private fun addXValueIfCompared(
        comparisonTraces: List<Trace<G, X, Double, C>>?,
        sortedXValues: List<X>
    ) = comparisonTraces?.let {
        sortedXValues.flatMap {
            if (it is Number) {
                listOf(it.toDouble(), it.toDouble() + 0.001)
            } else {
                listOf(
                    it.toString(),
                    (comparisonData?.labelPrefix) + it.toString()
                )
            }
        }.toSet().toList()
    } ?: sortedXValues
}

/**
 * A plotter for creating timeline plots.
 *
 * @param E the type of elements in the data set
 * @param G the type of the group key
 * @param X the type of the x-axis values
 * @param C the type of the color values
 * @param style the styling configuration for the plot
 * @param values the data specification for the plot
 * @param relative indicates if the count should be relative to the total count
 * @param normalize indicates if the counts should be normalized per x-value
 */
class TimeChartPlotter<E, G, X, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, Unit, Int, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Int>? = null,
    relative: Boolean = true,
    normalize: Boolean = true,
) : CountPlotter<E, G, X, C>(
    style = style,
    values = values,
    comparisonData = comparisonData,
    relative = relative,
    normalize = normalize,
) {

    /**
     * Plots the timeline data on the graph.
     *
     * @param sortedXValues the sorted x-axis values
     * @param transformedTraces the transformed traces with y-values processed
     * @return the timeline plot object
     */
    override fun plot(
        sortedXValues: List<X>,
        transformedTraces: List<Trace<G, X, Double, C>>,
        comparisonTraces: List<Trace<G, X, Double, C>>?,
    ): Plot {
        val dataset = dataFrameOf(
            "x" to transformedTraces.flatMap { sortedXValues } + (
                comparisonTraces?.flatMap { sortedXValues } ?: emptyList()
                ),

            "y" to transformedTraces.flatMap { it.dataPoints.map { p -> p.y } } + (
                comparisonTraces?.flatMap {
                    it.dataPoints.filter { p -> p.x in sortedXValues }.map { p -> p.y }
                } ?: emptyList()
                ),

            GROUP_COL to transformedTraces.flatMap { trace -> List(sortedXValues.size) { trace.key } } + (
                comparisonTraces?.flatMap { trace ->
                    List(sortedXValues.size) { "${comparisonData?.labelPrefix} ${trace.key}" }
                } ?: emptyList()
                )
        )

        return dataset.groupBy(GROUP_COL).plot {
            line {
                x("x") {
                    axis.name = style.xLabel
                }
                y("y") {
                    axis.name = yLabel
                }
                color(GROUP_COL) {
                    legend.name = style.groupLabel

                    @Suppress("SpreadOperator")
                    scale = categorical(
                        *colorScale(transformedTraces, comparisonTraces).map {
                            it.first to it.second.toColor()
                        }.toTypedArray()
                    )
                }
            }
        }
    }
}

open class LineChartPlotter<E, G, X, A, Y, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, A, Y, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Y>? = null,
    val pointSize: Double = 0.0
) : Plotter<E, G, X, A, Y, C>(
    style = style,
    values = values,
    comparisonData = comparisonData
) {
    /**
     * Plot the given traces.
     *
     * @param traces the traces to be plotted.
     */
    override fun plotTraces(traces: List<Trace<G, X, Y, C>>, comparisonTraces: List<Trace<G, X, Y, C>>?) {
        val yAxis = mapTraces(traces, comparisonTraces) { it.y }

        val dataset = dataFrameOf(
            X_COL to mapTraces(traces, comparisonTraces) { it.x},

            Y_COL to yAxis,
            GROUP_COL to traces.flatMap { trace -> List(trace.dataPoints.size) { trace.key } } + (
                comparisonTraces?.flatMap { trace ->
                    List(trace.dataPoints.size) { "${comparisonData?.labelPrefix} ${trace.key}" }
                } ?: emptyList()
                ),
            COLOR_COL to mapTraces(traces, comparisonTraces) { it.c },
            TYPE_COL to traces.flatMap { List(it.dataPoints.size) { ORIGINAL_COL } } + (comparisonTraces?.flatMap { List(it.dataPoints.size) { COMPARISON_COL } } ?: emptyList())
        )

        val traceColors = traces.flatMap { trace -> trace.dataPoints.map { p -> p.c } }
        val compColors = comparisonTraces?.flatMap { it.dataPoints.map { p -> p.c } } ?: emptyList()

        // Using associateBy caused a collapse of groups, if two groups had the same color.
        val colorList = traceColors.associateWith { style.colorMap(it) }.toList() +
                (compColors.associateWith { style.colorMap(it).scaleLightness(0.5) }.toList())
        val typeList = mapOf(
            ORIGINAL_COL to LineType.SOLID,
            COMPARISON_COL to LineType.DASHED)
            .toList().toTypedArray()

        // group the dataset by the group column
        val groupsMap: MutableMap<String, List<DataRow<Any?>>> = mutableMapOf<String, List<DataRow<Any?>>>().withDefault { emptyList() }
        dataset.rows().forEach { row ->
            val group = row[GROUP_COL].toString()
            groupsMap[group] = groupsMap.getValue(group) + row
        }
        plot {

            groupsMap.forEach { (_, rows) ->

                val xValues = rows.map { it[X_COL] }

                val yValues = rows.map { it[Y_COL] }

                val colorValue = rows.map { it[COLOR_COL] }

                val typeValue = rows.map { it[TYPE_COL] }

                line {

                    x(xValues)

                    y(yValues)
                    // Only plot if C is not of type Unit
                    if (colorList.first().first !is Unit) {
                        color(colorValue) {
                            legend.name = GROUP_COL
                            scale = categorical(
                                *colorList.map {
                                    it.first to it.second.toColor()
                                }.toTypedArray()
                            )

                        }
                    }
                    if (comparisonTraces != null){
                        type(typeValue) {
                            legend.name = TYPE_COL
                            scale = categorical(*typeList)
                        }
                    }


                }
                points {
                    x(xValues)
                    y(yValues)
                    size = pointSize
                    if (colorList.first().first !is Unit) {
                        color(colorValue) {
                            scale = categorical(
                                *colorList.map {
                                    it.first to it.second.toColor()
                                }.toTypedArray()
                            )
                        }
                    }
                }
            }
    }.save(
            "${name.replace(" ", "_")}.png",
            path = "results")
    }

}

class ScalableSortableLineChartPlotter<E, G, X: Comparable<X>, A, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, A, Double, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Double>? = null,
    relative: Boolean = true,
    normalize: Boolean = true,
) : ScalablePlotter<E, G, X, A, Double, C>(
    style = style,
    values = values,
    comparisonData = comparisonData,
    relative = relative,
    normalize = normalize
) {

    // TODO Currently, the only way to combine the two plotter classes is to create an inner class.
    // This is due to the fact that the plotTraces method is protected in the Plotter class.
    // It also cannot be changed to inner, as Trace is a protected class, which cannot be made public,
    // as it used DataPoint, which is also protected.
    private inner class InnerLineChartPlotter(
        style: PlotStyling<G, X, C>,
        values: PlotDataSpecification<E, G, X, A, Double, C>,
        comparisonData: ComparisonDataSpecification<Nothing, G, X, Double>? = null
    ): LineChartPlotter<E, G, X, A, Double, C>(style, values, comparisonData) {

        override fun plotTraces(traces: List<Trace<G, X, Double, C>>, comparisonTraces: List<Trace<G, X, Double, C>>?) {
            val xValues = traces.flatMap { trace -> trace.dataPoints.map { it.x } }.distinct().let {
                style.xOrder.arrange(it)
            }

            val transformedTraces = arrangeTraces(xValues, traces, 0.0)

            val arrangedComparisonData = comparisonTraces?.let { arrangeTraces(xValues, it, 0.0) }
            super.plotTraces(transformedTraces, arrangedComparisonData)
        }

        fun plot(traces: List<Trace<G, X, Double, C>>, comparisonTraces: List<Trace<G, X, Double, C>>?) {
            this.plotTraces(traces, comparisonTraces)
        }

    }

    /**
     * Plot the given traces. The traces are plotted as lines.
     * The color of the lines is determined by the color attribute.
     * The lines are scaled with regard to the relative and normalize
     * flag.
     *
     * @param traces the traces to be plotted.
     */
    override fun plotTraces(traces: List<Trace<G, X, Double, C>>, comparisonTraces: List<Trace<G, X, Double, C>>?) {
        InnerLineChartPlotter(style, values, comparisonData).plot(traces, comparisonTraces)
    }

}




class BoxPlotter<E, G, X, A: Number, Y: Summary<A>, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, A, Y, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Y>? = null
): Plotter<E, G, X, A, Y, C>(
    style, values, comparisonData
) {

    /**
     * Plot the given traces. The traces are plotted as box plots.
     * The color of the boxes is determined by the color attribute.
     *
     * @param traces the traces to be plotted.
     * @param comparisonTraces the comparison traces to be plotted.
     */
    override fun plotTraces(traces: List<Trace<G, X, Y, C>>, comparisonTraces: List<Trace<G, X, Y, C>>?) {


        val typeList = traces.flatMap {
            List(it.dataPoints.size) { ORIGINAL_COL }
        } + (comparisonTraces?.flatMap { List(it.dataPoints.size) { COMPARISON_COL } } ?: emptyList())

        val colorList = mapTraces(traces, comparisonTraces) { it.c }
        val colorMap = colorList.associateWith { style.colorMap(it).toColor() }
        val maxList = mapTraces(traces, comparisonTraces) { it.y.max }

        val dataset = dataFrameOf(
            "min" to mapTraces(traces, comparisonTraces) { it.y.min },
            "lower" to mapTraces(traces, comparisonTraces) { it.y.lowerQuart },
            "median" to mapTraces(traces, comparisonTraces) { it.y.median },
            "upperQuart" to mapTraces(traces, comparisonTraces) { it.y.upperQuart },
            "max" to maxList,
            COLOR_COL to colorList,
            TYPE_COL to typeList,
            X_COL to mapTraces(traces, comparisonTraces) { it.x }
        )

        dataset.groupBy(TYPE_COL).plot {
            y {
                scale = continuous(0.0..maxList.maxOf { it.toDouble() })
                axis.name = style.yLabel
            }
            boxes {
                x(X_COL) {
                    axis.name = style.xLabel
                }
                yMin("min")
                lower("lower")
                middle("median")
                upper("upperQuart")
                yMax("max")
                borderLine.color(COLOR_COL) {
                    scale = categorical(
                        *colorList.map { it to colorMap[it]!! }.toTypedArray()
                    )
                }
                fillColor = Color.WHITE
                if (comparisonTraces != null){
                    borderLine.type(TYPE_COL) {
                        scale = categorical(
                            ORIGINAL_COL to LineType.SOLID,
                            COMPARISON_COL to LineType.LONGDASH
                        )
                    }
                }
            }

        }.save(
            "${name.replace(" ", "_")}.png",
            path = "results"
        )
    }
}

class ScatterPlotter<E, G, X: Number, A, Y: Number, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, A, Y, C>,
    comparisonData: ComparisonDataSpecification<Nothing, G, X, Y>? = null
): Plotter<E, G, X, A, Y, C>(
style, values, comparisonData
){
    /**
     * Plot the given traces. The traces are plotted as points.
     * The color of the points is determined by the color attribute.
     * The color attribute is mapped to a color using the colorMap.
     * The colorMap is used to create a color scale for the plot.
     *
     * @param traces the traces to be plotted.
     * @param comparisonTraces the comparison traces to be plotted.
     */
    override fun plotTraces(traces: List<Trace<G, X, Y, C>>, comparisonTraces: List<Trace<G, X, Y, C>>?) {
        val xList = mapTraces(traces, comparisonTraces) { it.x }
        val yList = mapTraces(traces, comparisonTraces) { it.y }

        val typeList = traces.flatMap { List(it.dataPoints.size) { ORIGINAL_COL } } + (comparisonTraces?.flatMap { List(it.dataPoints.size) { COMPARISON_COL } } ?: emptyList())

        val colorList = mapTraces(traces, comparisonTraces) { it.c }
        val colorMap = colorList.associateWith { style.colorMap(it).toColor() }
        val colors = colorList.associateWith { colorMap[it]!! }.toList().toTypedArray()

        val dataset = dataFrameOf(
            X_COL to xList,
            Y_COL to yList,
            COLOR_COL to colorList,
            TYPE_COL to typeList
        )

        dataset.groupBy(TYPE_COL).plot {
            points {
                x(X_COL) {
                    axis.name = style.xLabel
                }
                y(Y_COL) {
                    axis.name = style.yLabel
                }
                color(COLOR_COL) {
                    scale = categorical(
                        *colors
                    )
                }
                symbol(TYPE_COL) {
                    scale = categorical(
                        ORIGINAL_COL to Symbol.CIRCLE,
                        COMPARISON_COL to Symbol.CROSS
                    )
                }
            }
        }.save(
            "${name.replace(" ", "_")}.png",
            path = "results"
        )
    }

}
