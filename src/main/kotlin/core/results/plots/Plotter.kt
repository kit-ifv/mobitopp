package core.results.plots

import core.modelsteps.Resource
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.Position
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.feature.position
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
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
    protected data class DataPoint<X, Y, C>(val x: X, val y: Y, val c: C)

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

                Trace(
                    key = group,
                    dataPoints = aggregation.aggregate(
                        yAttribute = yAttribute,
                        elementValues = xValues,
                        colorBy = coloringForGroup(group)
                    ).map { (x, y, c) ->
                        DataPoint(x, y, c)
                    }
                )
            }
        }
        return traces
    }

    private fun coloringForGroup(group: GROUPING): (ENTITY?, X_AXIS, Y_AXIS) -> COLOR_ATT = { e, x, y ->
        values.colorBy(e, group, x, y)
    }

    private fun comparisonTraces() = comparisonData?.let { comparison ->
        comparison.rawData().groupBy { it.first }.entries.map { (group, rawDataPoints) ->
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
    protected val relative: Boolean = true,
    protected val normalize: Boolean = true,
) : Plotter<E, G, X, Unit, Int, C>(
    style = style,
    values = values,
    comparisonData = comparisonData
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

        val transformedTraces = arrangeTraces(xValues, traces)
//            style.groupOrder.arrangeBy(traces) { it.key }.map { trace ->
//            val valuesByX = trace.dataPoints.associateBy { it.x }
//
//            Trace(
//                key = trace.key,
//                dataPoints = xValues.map { x ->
//                    val point = valuesByX[x]
//                    point?.let {
//                        DataPoint(x, transformY(x to it.y), it.c)
//                    } ?: DataPoint(x, 0.0, values.colorBy(null, trace.key, x, 0))
//                }
//            )
//        }

        val arrangedComparisonData = comparisonTraces?.let { arrangeTraces(xValues, it) }

        plot(xValues, transformedTraces, arrangedComparisonData).save(
            "${name.replace(" ", "_")}.png",
            path = "results"
        )
    }

    private fun arrangeTraces(
        xValues: List<X>,
        traces: List<Trace<G, X, Int, C>>
    ) =
        style.groupOrder.arrangeBy(traces) { it.key }.map { trace ->
            val valuesByX = trace.dataPoints.associateBy { it.x }
            val transformY: (Pair<X, Int>) -> Double = valueSpaceTransformation(traces)

            Trace(
                key = trace.key,
                dataPoints = xValues.map { x ->
                    val point = valuesByX[x]
                    point?.let {
                        DataPoint(x, transformY(x to it.y), it.c)
                    } ?: DataPoint(x, 0.0, values.colorBy(null, trace.key, x, 0))
                }
            )
        }

    private fun valueSpaceTransformation(traces: List<Trace<G, X, Int, C>>): (Pair<X, Int>) -> Double {
        val transformY: (Pair<X, Number>) -> Double = if (normalize) {
            val xCounts = traces.flatMap {
                it.dataPoints
            }.groupBy({ it.x }, { it.y }).map { (x, y) ->
                x to y.sumOf(Number::toDouble)
            }.toMap()

            ({ (x, y) -> y.toDouble() / (xCounts[x] ?: 1.0) })
        } else if (relative) {
            val totalYSum = traces.sumOf { it.dataPoints.sumOf { p -> p.y.toDouble() } }
            ({ (_, y) -> y.toDouble() / totalYSum })
        } else {
            { (_, y) -> y.toDouble() }
        }
        return transformY
    }

    protected fun colorScale(
        transformedTraces: List<Trace<G, X, Double, C>>,
        comparisonTraces: List<Trace<G, X, Double, C>>?
    ): List<Pair<String, RGB>> =
        transformedTraces.map {
            it.key.toString() to style.colorMap(it.dataPoints[0].c)
        } + (
            comparisonTraces?.map {
                comparisonData?.labelPrefix + it.key.toString() to
                    style.colorMap(it.dataPoints[0].c) // .scaleLightness(1.5)
            } ?: emptyList()
            )

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

/**
 * HistogramPlotter is a plotter for creating histogram plots.
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
class HistogramPlotter<E, G, X, C>(
    style: PlotStyling<G, X, C>,
    values: PlotDataSpecification<E, G, X, Unit, Int, C>,
    comparisonData: ComparisonDataSpecification<Any, G, X, Int>? = null,
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

        var df = dataFrameOf(
            "x" to xValues
        )

        df = if (comparisonTraces == null) {
            addNoComparisonTracesAsColums(transformedTraces, df)
        } else {
            addDataAndComparisonTracesToDf(transformedTraces, df, comparisonTraces)
        }

        @Suppress("SpreadOperator")
        val groupColumns = setOf(
            *transformedTraces.toTypedArray(),
            *comparisonTraces?.toTypedArray() ?: emptyArray(),
        )

        @Suppress("SpreadOperator")
        val dataset = df.gather(*groupColumns.map { it.key.toString() }.toTypedArray()).into(
            GROUP_COL,
            SHARE_COL
        )

        return dataset.plot {
            bars {
                alpha = 0.8

                x("x") {
                    axis.name = style.xLabel
                }

                y(SHARE_COL) {
                    axis.name = yLabel
                }

                if (transformedTraces.size > 1) {
                    fillColor(GROUP_COL) {
                        @Suppress("SpreadOperator")
                        scale = categorical(
                            *colorScale(transformedTraces, comparisonTraces).map {
                                it.first to it.second.toColor()
                            }.toTypedArray()
                        )

                        legend.name = style.groupLabel
                        position = Position.stack()
                    }
                }
            }

            layout.title = name
        }
    }

    private fun addDataAndComparisonTracesToDf(
        transformedTraces: List<Trace<G, X, Double, C>>,
        df: AnyFrame,
        comparisonTraces: List<Trace<G, X, Double, C>>?
    ): AnyFrame {
        var df1 = df
        transformedTraces.forEach { trace ->
            val column = trace.dataPoints.flatMap {
                listOf(it.y, 0.0)
            }.toColumn(trace.key.toString())

            df1 = df1.add(column)
        }

        comparisonTraces?.forEach { compTrace ->
            val column = compTrace.dataPoints.flatMap {
                listOf(0.0, it.y)
            }.toColumn(compTrace.key.toString())

            df1 = df1.add(column)
        }
        return df1
    }

    private fun addNoComparisonTracesAsColums(
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
        }
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

                if (transformedTraces.size > 1) {
                    color(GROUP_COL) {
                        legend.name = style.groupLabel

                        @Suppress("SpreadOperator")
                        scale = categorical(
                            *colorScale(transformedTraces, comparisonTraces).map {
                                it.first to it.second.toColor()
                            }.toTypedArray()
                        )

                        if (normalize) {
                            position = Position.stack()
                        }
                    }
                }
            }
        }
    }
}
