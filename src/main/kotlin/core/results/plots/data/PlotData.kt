package core.results.plots.data

/**
 * Single data point at x with value y.
 *
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate value.
 * @property x The x-coordinate.
 * @property y The y-coordinate value.
 */
data class Point<X, Y>(val x: X, val y: Y)

/**
 * A grouped series of points belonging to a single group/category.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates in the points.
 * @param Y The type of the y-coordinate values in the points.
 * @property group The group identifier.
 * @property points The list of points in this trace.
 */
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
 */
data class Summary<T>(val min: T, val lowerQuart: T, val median: T, val upperQuart: T, val max: T)

/**
 * Interface for providing plot data as a list of traces.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param Y The type of the y-coordinate values.
 */
interface PlotData<G, X, Y> {
    /**
     * Converts the plot data into a list of [Trace]s.
     *
     * @return A list of [Trace] objects.
     */
    fun asTraces(): List<Trace<G, X, Y>>
}

/**
 * A [PlotData] implementation that sources elements from an [Iterable] and uses a [PlotDataFactory] to create traces.
 *
 * @param E The type of elements in the data source.
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param Y The type of the intermediate y-values.
 * @param V The type of the final y-coordinate values.
 * @property elements A function returning an [Iterable] of elements.
 * @property factory The factory used to create plot data from elements.
 */
class PlotDataSource<E, G, X, Y, V>(
    private val elements: () -> Iterable<E>,
    private val factory: PlotDataFactory<E, G, X, Y, V>,
) : PlotData<G, X, V> {

    /**
     * Constructs a [PlotDataSource] with a fixed [Iterable] of elements.
     *
     * @param elements The elements to use as data source.
     * @param factory The factory used to create plot data.
     */
    constructor(
        elements: Iterable<E>,
        factory: PlotDataFactory<E, G, X, Y, V>,
    ) : this({ elements }, factory)

    /**
     * Returns the traces created by the factory from the elements.
     *
     * @return A list of [Trace]s.
     */
    override fun asTraces(): List<Trace<G, X, V>> = factory.createGroupedTraces(elements())
}

/**
 * A [PlotData] implementation that transforms traces from an [original] [PlotData] using a [transformer].
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param V The type of the original y-coordinate values.
 * @param T The type of the transformed y-coordinate values.
 * @property original The original plot data.
 * @property transformer The transformer to apply to the traces.
 */
class TransformedPlotData<G, X, V, T>(
    private val original: PlotData<G, X, V>,
    private val transformer: TraceTransformer<G, X, V, T>,
) : PlotData<G, X, T> {
    /**
     * Returns the transformed traces.
     *
     * @return A list of transformed [Trace]s.
     */
    override fun asTraces(): List<Trace<G, X, T>> = transformer.transform(original.asTraces())
}

/**
 * Specifications for extracting attributes from elements for plotting.
 *
 * @param E The type of elements.
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate.
 * @property xAttribute Function to extract x-coordinate from an element.
 * @property yAttribute Function to extract y-coordinate from an element.
 * @property groupBy Function to extract group identifier from an element.
 */
data class PlotDataSpecs<E, G, X, Y>(val xAttribute: (E) -> X, val yAttribute: (E) -> Y, val groupBy: (E) -> G)

/**
 * Interface for factories that create [Trace]s from elements.
 *
 * @param E The type of elements.
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param Y The type of the intermediate y-values.
 * @param V The type of the final y-coordinate values.
 */
interface PlotDataFactory<E, G, X, Y, V> {
    /**
     * The specifications for data extraction.
     */
    val specs: PlotDataSpecs<E, G, X, Y>

    /**
     * Groups [elements] and creates a list of traces.
     *
     * @param elements The elements to process.
     * @return A list of grouped [Trace]s.
     */
    fun createGroupedTraces(elements: Iterable<E>) = elements.groupBy(specs.groupBy).entries.map { (group, elements) ->
        createTrace(group, elements)
    }

    /**
     * Creates a single [Trace] for a [group] from the given [elements].
     *
     * @param group The group identifier.
     * @param elements The elements belonging to the group.
     * @return A [Trace] for the group.
     */
    fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, V>
}

/**
 * A [PlotDataFactory] that includes all values in the traces without aggregation.
 *
 * @param E The type of elements.
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param Y The type of the y-coordinate values.
 * @property specs The specifications for data extraction.
 */
class AllValuesPlotData<E, G, X, Y>(override val specs: PlotDataSpecs<E, G, X, Y>) : PlotDataFactory<E, G, X, Y, Y> {

    /**
     * Creates a [Trace] containing all points from [elements].
     *
     * @param group The group identifier.
     * @param elements The elements in the group.
     * @return A [Trace] with all points.
     */
    override fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, Y> = elements.map {
        val x = specs.xAttribute(it)
        val y = specs.yAttribute(it)
        Point(x, y)
    }.let { Trace(group, it) }
}

/**
 * A [PlotDataFactory] that aggregates values for each x-coordinate.
 *
 * @param E The type of elements.
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinates.
 * @param Y The type of the intermediate y-values to be aggregated.
 * @param V The type of the aggregated y-coordinate values.
 * @property specs The specifications for data extraction.
 * @property aggregation The aggregation strategy to apply.
 */
class AggregateValuesPlotData<E, G, X, Y, V>(
    override val specs: PlotDataSpecs<E, G, X, Y>,
    private val aggregation: Aggregation<Y, V>,
) : PlotDataFactory<E, G, X, Y, V> {

    /**
     * Creates a [Trace] where y-values are aggregated for each unique x-coordinate.
     *
     * @param group The group identifier.
     * @param elements The elements in the group.
     * @return A [Trace] with aggregated points.
     */
    override fun createTrace(group: G, elements: Iterable<E>): Trace<G, X, V> = elements.groupBy(
        { specs.xAttribute(it) },
        { specs.yAttribute(it) },
    ).map {
        val x = it.key
        val value = aggregation.aggregate(it.value)
        Point(x, value)
    }.let {
        Trace(group, it)
    }
}
