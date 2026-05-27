package core.results.plots.data

import kotlin.math.abs

fun interface TraceTransformer<G, X, V, T> {

    fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, T>>
}

class FillMissingXValues<G, X, V>(val default: V) : TraceTransformer<G, X, V, V> {
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

class NormalizeByGroup<G, X, V : Number> : TraceTransformer<G, X, V, Double> {
    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, Double>> = traces.map { transformSingle(it) }

    private fun transformSingle(trace: Trace<G, X, V>): Trace<G, X, Double> {
        val sum = trace.points.sumOf { it.y.toDouble() }
        val relativePoints = trace.points.map {
            Point(it.x, it.y.toDouble() / sum)
        }
        return trace.run { Trace(group, relativePoints) }
    }
}

class NormalizeByX<G, X, V : Number> : TraceTransformer<G, X, V, Double> {

    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, Double>> {
        val sumByX = traces.flatMap {
            it.points
        }.groupBy({ it.x }, { it.y }).map { (x, y) ->
            x to y.sumOf { abs(it.toDouble()) }
        }.toMap()

        return traces.map { transformSingle(it, sumByX) }
    }

    private fun transformSingle(trace: Trace<G, X, V>, sumByX: Map<X, Double>): Trace<G, X, Double> {
        val normalizedPoints = trace.points.map {
            val sum = sumByX[it.x] ?: 0.0
            Point(it.x, it.y.toDouble() / sum)
        }
        return trace.run { Trace(group, normalizedPoints) }
    }
}

class SortGroups<G, X, V>(val ordering: Ordering<G>) : TraceTransformer<G, X, V, V> {

    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, V>> = ordering.arrangeBy(traces) { it.group }
}

class SortPoints<G, X, V>(val ordering: Ordering<X>) : TraceTransformer<G, X, V, V> {

    override fun transform(traces: List<Trace<G, X, V>>): List<Trace<G, X, V>> = traces.map { trace ->
        val orderedPoints = ordering.arrangeBy(trace.points) { it.x }
        trace.copy(points = orderedPoints)
    }
}
