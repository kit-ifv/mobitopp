package edu.kit.ifv.core.results.plots.data
import kotlin.math.abs

/**
 * A functional interface for transforming a list of traces.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the input y-coordinate values.
 * @param T The type of the transformed y-coordinate values.
 */
fun interface TraceTransformer<G, X, V, T> {

    /**
     * Transforms the given [traces] into a new list of traces.
     *
     * @param traces The list of traces to transform.
     * @return The transformed list of traces.
     */
    fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, T>>
}

/**
 * A [TraceTransformer] that fills missing x-values in each trace with a [default] y-value.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the y-coordinate values.
 * @property default The y-value to use for missing x-coordinates.
 */
class FillMissingXValues<G, X, V>(val default: V) : TraceTransformer<G, X, V, V> {
    /**
     * Fills missing x-values in [traces] so that all traces have the same set of x-coordinates.
     *
     * @param traces The list of traces.
     * @return Traces with missing x-values filled.
     */
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, V>> {
        val xValues = traces.flatMap { it.points }.map { it.x }.distinct()

        return traces.map { trace ->
            val observedXValues = trace.points.map { it.x }.distinct()
            val missingXValues = xValues.filter { it !in observedXValues }
            val fillerPoints = missingXValues.map { Point(it, default) }
            Trace(trace.group, trace.points + fillerPoints)
        }
    }
}

/**
 * A [TraceTransformer] that normalizes y-values within each group such that they sum up to 1.0.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the input y-coordinate values (must be a [Number]).
 */
class NormalizeByGroup<G, X, V : Number> : TraceTransformer<G, X, V, Double> {
    /**
     * Normalizes each trace in [traces] independently.
     *
     * @param traces The list of traces to normalize.
     * @return Normalized traces.
     */
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, Double>> = traces.map { transformSingle(it) }

    /**
     * Normalizes a single trace.
     *
     * @param trace The trace to normalize.
     * @return The normalized trace.
     */
    private fun transformSingle(trace: Trace<G, X, V>): Trace<G, X, Double> {
        val sum = trace.points.sumOf { it.y.toDouble() }
        val relativePoints = trace.points.map {
            Point(it.x, it.y.toDouble() / sum)
        }
        return trace.run { Trace(group, relativePoints) }
    }
}

/**
 * A [TraceTransformer] that normalizes y-values for each x-coordinate across all groups.
 * The sum of y-values for a given x-coordinate across all traces will be 1.0.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the input y-coordinate values (must be a [Number]).
 */
class NormalizeByX<G, X, V : Number> : TraceTransformer<G, X, V, Double> {

    /**
     * Normalizes [traces] by x-coordinates.
     *
     * @param traces The list of traces.
     * @return Normalized traces.
     */
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, Double>> {
        val sumByX = traces.flatMap {
            it.points
        }.groupBy({ it.x }, { it.y }).map { (x, y) ->
            x to y.sumOf { abs(it.toDouble()) }
        }.toMap()

        return traces.map { transformSingle(it, sumByX) }
    }

    /**
     * Normalizes a single trace using precalculated sums per x-coordinate.
     *
     * @param trace The trace to normalize.
     * @param sumByX A map of sums for each x-coordinate.
     * @return The normalized trace.
     */
    private fun transformSingle(trace: Trace<G, X, V>, sumByX: Map<X, Double>): Trace<G, X, Double> {
        val normalizedPoints = trace.points.map {
            val sum = sumByX[it.x] ?: 0.0
            Point(it.x, it.y.toDouble() / sum)
        }
        return trace.run { Trace(group, normalizedPoints) }
    }
}

/**
 * A [TraceTransformer] that sorts groups based on an [ordering] strategy.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the y-coordinate values.
 * @property ordering The strategy used to order the groups.
 */
class SortGroups<G, X, V>(val ordering: Ordering<G>) : TraceTransformer<G, X, V, V> {

    /**
     * Sorts the [traces] list by group.
     *
     * @param traces The list of traces.
     * @return Sorted traces.
     */
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, V>> = ordering.arrangeBy(traces) { it.group }
}

/**
 * A [TraceTransformer] that sorts points within each trace based on an [ordering] strategy for x-values.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the y-coordinate values.
 * @property ordering The strategy used to order the points by x-coordinate.
 */
class SortPoints<G, X, V>(val ordering: Ordering<X>) : TraceTransformer<G, X, V, V> {

    /**
     * Sorts points within each trace in [traces].
     *
     * @param traces The list of traces.
     * @return Traces with sorted points.
     */
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, V>> = traces.map { trace ->
        val orderedPoints = ordering.arrangeBy(trace.points) { it.x }
        trace.copy(points = orderedPoints)
    }
}
