package core.results.plots.data

/** Single data point at x with value y. */
data class Point<X, Y>(val x: X, val y: Y)
/** A grouped series of points belonging to a single group/category. */
data class Trace<G, X, Y>(val group: G, val points: List<Point<X, Y>>)

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

interface PlotData<G, X, Y> {
    fun asTraces(): List<Trace<G, X, Y>>
}

class PlotDataSource<E, G, X, Y, V>(
    private val elements: () -> Iterable<E>,
    private val factory: PlotDataFactory<E, G, X, Y, V>
) : PlotData<G, X, V> {

    constructor(
        elements: Iterable<E>,
        factory: PlotDataFactory<E, G, X, Y, V>
    ) : this({ elements }, factory)

    override fun asTraces(): List<Trace<G, X, V>> =
        factory.createGroupedTraces(elements())
}

class TransformedPlotData<G, X, V, T>(
    private val original: PlotData<G, X, V>,
    private val transformer: TraceTransformer<G, X, V, T>
) : PlotData<G, X, T> {
    override fun asTraces(): List<Trace<G, X, T>> =
        transformer.transform(original.asTraces())
}

data class PlotDataSpecs<E, G, X, Y>(
    val xAttribute: (E) -> X,
    val yAttribute: (E) -> Y,
    val groupBy: (E) -> G,
)

interface PlotDataFactory<E, G, X, Y, V> {
    val specs: PlotDataSpecs<E, G, X, Y>

    fun createGroupedTraces(elements: Iterable<E>) =
        elements.groupBy(specs.groupBy).entries.map { (group, elements) ->
            createTrace(group, elements)
        }

    fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, V>
}

class AllValuesPlotData<E, G, X, Y>(
    override val specs: PlotDataSpecs<E, G, X, Y>,
) : PlotDataFactory<E, G, X, Y, Y> {

    override fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, Y> =
        elements.map {
            val x = specs.xAttribute(it)
            val y = specs.yAttribute(it)
            Point(x, y)
        }.let { Trace(group, it) }
}

class AggregateValuesPlotData<E, G, X, Y, V>(
    override val specs: PlotDataSpecs<E, G, X, Y>,
    private val aggregation: Aggregation<Y, V>,
) : PlotDataFactory<E, G, X, Y, V> {

    override fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, V> =
        elements.groupBy(
            { specs.xAttribute(it) },
            { specs.yAttribute(it) }
        ).map {
            val x = it.key
            val value = aggregation.aggregate(it.value)
            Point(x, value)
        }.let {
            Trace(group, it)
        }
}
