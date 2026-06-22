package edu.kit.ifv.core.results.plots
import edu.kit.ifv.core.modelsteps.resources.Resource
import edu.kit.ifv.core.results.plots.data.AggregateValuesPlotData
import edu.kit.ifv.core.results.plots.data.Aggregation
import edu.kit.ifv.core.results.plots.data.AllValuesPlotData
import edu.kit.ifv.core.results.plots.data.Count
import edu.kit.ifv.core.results.plots.data.FillMissingXValues
import edu.kit.ifv.core.results.plots.data.LowerMedianBy
import edu.kit.ifv.core.results.plots.data.LowerQuantileBy
import edu.kit.ifv.core.results.plots.data.MaxBy
import edu.kit.ifv.core.results.plots.data.Mean
import edu.kit.ifv.core.results.plots.data.MinBy
import edu.kit.ifv.core.results.plots.data.NormalizeByGroup
import edu.kit.ifv.core.results.plots.data.NormalizeByX
import edu.kit.ifv.core.results.plots.data.Ordering
import edu.kit.ifv.core.results.plots.data.PlotData
import edu.kit.ifv.core.results.plots.data.PlotDataSource
import edu.kit.ifv.core.results.plots.data.PlotDataSpecs
import edu.kit.ifv.core.results.plots.data.SortGroups
import edu.kit.ifv.core.results.plots.data.SortPoints
import edu.kit.ifv.core.results.plots.data.Sum
import edu.kit.ifv.core.results.plots.data.SummarizeBy
import edu.kit.ifv.core.results.plots.data.Summary
import edu.kit.ifv.core.results.plots.data.TransformedPlotData
import edu.kit.ifv.core.results.plots.render.BoxPlotLayoutBuilder
import edu.kit.ifv.core.results.plots.render.BoxPlotRenderer
import edu.kit.ifv.core.results.plots.render.HistogramLayoutBuilder
import edu.kit.ifv.core.results.plots.render.HistogramRenderer
import edu.kit.ifv.core.results.plots.render.LinePlotLayoutBuilder
import edu.kit.ifv.core.results.plots.render.LinePlotRenderer
import edu.kit.ifv.core.results.plots.render.ScatterLayoutBuilder
import edu.kit.ifv.core.results.plots.render.ScatterPlotRenderer
import edu.kit.ifv.units.UnitIntervalValue

/**
 * Entry point to build plot data from a lazy supplier of entities.
 * Write
 * ```kotlin
 * forData {
 *     elements
 * }.groupBy { //optional
 *      myGrouping
 * }...
 * ```
 * to start a new plot definition.
 *
 * @param E The type of entities.
 * @param entities A function providing a list of entities.
 * @return A [PlotDataBuilderWithEntities] to continue building the plot.
 */
fun <E> forData(entities: () -> List<E>) = PlotDataBuilderWithEntities(entities)

/**
 * Entry point to build plot data from a [Resource] wrapper.
 *
 * @param E The type of entities.
 * @param resource The resource containing the entities.
 * @return A [PlotDataBuilderWithEntities] to continue building the plot.
 */
fun <E> forData(resource: Resource<E>) = PlotDataBuilderWithEntities { resource.elements.toList() }

/**
 * Entry point to build plot data from an [Iterable].
 *
 * @param E The type of entities.
 * @param iterable The iterable containing the entities.
 * @return A [PlotDataBuilderWithEntities] to continue building the plot.
 */
fun <E> forData(iterable: Iterable<E>) = PlotDataBuilderWithEntities { iterable.toList() }

/**
 * Convenience grouping function for plots without a grouping attribute, uses [Unit] instead.
 *
 * @param E The type of entities.
 * @return A function that maps any entity to [Unit].
 */
fun <E> noGrouping(): (E) -> Unit = { _ -> }

/**
 * Stage 1 of the builder: entities are known, grouping not yet specified (defaults to no grouping).
 *
 * @param E The type of entities.
 * @property entities A function providing a list of entities.
 */
data class PlotDataBuilderWithEntities<E>(override val entities: () -> List<E>) : PlotDataGroupingProvider<E, Unit> {

    /**
     * The default grouping function (no grouping).
     */
    override val groupBy: (E) -> Unit
        get() = noGrouping()

    /**
     * Specify the grouping function. Each distinct value defines a series ([core.results.plots.data.Trace]).
     *
     * @param G The type of the group identifier.
     * @param groupBy Function to extract the group identifier from an entity.
     * @return A [PlotDataBuilderWithGrouping] to continue building the plot.
     */
    fun <G> groupBy(groupBy: (E) -> G) = PlotDataBuilderWithGrouping(entities, groupBy)
}

/**
 * Stage 2 of the builder: entities plus grouping function are known.
 *
 * @param E The type of entities.
 * @param G The type of the group identifier.
 * @property entities A function providing a list of entities.
 * @property groupBy Function to extract the group identifier from an entity.
 */
data class PlotDataBuilderWithGrouping<E, G>(override val entities: () -> List<E>, override val groupBy: (E) -> G) :
    PlotDataGroupingProvider<E, G>

/**
 * Provides operations to configure which values are plotted and how they are aggregated.
 *
 * @param E The type of entities.
 * @param G The type of the group identifier.
 */
@Suppress("TooManyFunctions")
interface PlotDataGroupingProvider<E, G> {
    /** Function providing a list of entities. */
    val entities: () -> List<E>

    /** Function to extract the group identifier from an entity. */
    val groupBy: (E) -> G

    /**
     * Plot all raw Y values against X for each group.
     *
     * @param Y The type of the y-coordinate.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAllValuesWithY] to continue building the plot.
     */
    fun <Y> plot(yAttribute: (E) -> Y) = PlotDataBuilderAllValuesWithY(
        entities,
        groupBy,
        yAttribute,
    )

    /**
     * Plot the minimum Y per X for each group.
     *
     * @param Y The type of comparable y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Comparable<Y>> plotMinOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MinBy(sortBy = { it }),
    )

    /**
     * Plot the maximum Y per X for each group.
     *
     * @param Y The type of comparable y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Comparable<Y>> plotMaxOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MaxBy(sortBy = { it }),
    )

    /**
     * Plot the lower median (50th percentile) Y per X for each group.
     *
     * @param Y The type of comparable y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Comparable<Y>> plotMedianOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        LowerMedianBy(sortBy = { it }),
    )

    /**
     * Plot an arbitrary lower quantile of Y per X for each group.
     *
     * @param Y The type of comparable y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @param quantile The quantile to calculate.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Comparable<Y>> plotQuantileOf(yAttribute: (E) -> Y, quantile: UnitIntervalValue) =
        PlotDataBuilderAggregateWithY(
            entities,
            groupBy,
            yAttribute,
            LowerQuantileBy(quantile = quantile, sortBy = { it }),
        )

    /**
     * Plot the sum of Y per X for each group.
     *
     * @param Y The type of numeric y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Number> plotSumOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Sum,
    )

    /**
     * Plot the mean of Y per X for each group.
     *
     * @param Y The type of numeric y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Number> plotMeanOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Mean,
    )

    /**
     * Summarize Y per X for each group into box-plot friendly statistics.
     *
     * @param Y The type of comparable y-coordinates.
     * @param yAttribute Function to extract the y-coordinate from an entity.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <Y : Comparable<Y>> summarize(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        SummarizeBy(sortBy = { it }),
    )

    /**
     * Internal helper for no Y attribute.
     */
    private fun <E> noYAtt(): (E) -> Unit = { _ -> }

    /**
     * Count elements by X for each group.
     *
     * @param X The type of the x-coordinate.
     * @param xAttribute Function to extract the x-coordinate from an entity.
     * @return A [PlotDataTransformationBuilder] to continue building the plot.
     */
    fun <X> count(xAttribute: (E) -> X) = PlotDataTransformationBuilder(
        PlotDataSource(
            elements = entities,
            factory = AggregateValuesPlotData(
                specs = PlotDataSpecs(xAttribute, noYAtt(), groupBy),
                aggregation = Count,
            ),
        ),
    )

    /**
     * Custom aggregation of a computed attribute [yAttribute] with the provided [aggregation].
     *
     * @param A The type of intermediate attribute to aggregate.
     * @param Y The type of the resulting aggregated value.
     * @param yAttribute Function to extract the attribute to aggregate from an entity.
     * @param aggregation The aggregation strategy to use.
     * @return A [PlotDataBuilderAggregateWithY] to continue building the plot.
     */
    fun <A, Y> aggregateBy(yAttribute: (E) -> A, aggregation: Aggregation<A, Y>) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        aggregation,
    )
}

/**
 * Stage 3 of the builder: all values of [yAttribute] will be kept for plotting.
 *
 * @param E The type of entities.
 * @param G The type of the group identifier.
 * @param Y The type of the y-coordinate.
 * @property entities Function providing a list of entities.
 * @property groupBy Function to extract the group identifier from an entity.
 * @property yAttribute Function to extract the y-coordinate from an entity.
 */
data class PlotDataBuilderAllValuesWithY<E, G, Y>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
) {
    /**
     * Choose the X attribute used to group points along the x-axis.
     *
     * @param X The type of the x-coordinate.
     * @param xAttribute Function to extract the x-coordinate from an entity.
     * @return A [PlotDataTransformationBuilder] to continue building the plot.
     */
    fun <X> over(xAttribute: (E) -> X) = PlotDataTransformationBuilder(
        PlotDataSource(
            elements = entities,
            factory = AllValuesPlotData(
                specs = PlotDataSpecs(xAttribute, yAttribute, groupBy),
            ),
        ),
    )
}

/**
 * Stage 3 of the builder: [yAttribute] will be aggregated with the given [aggregation].
 *
 * @param E The type of entities.
 * @param G The type of the group identifier.
 * @param Y The type of the intermediate attribute to aggregate.
 * @param V The type of the resulting aggregated value.
 * @property entities Function providing a list of entities.
 * @property groupBy Function to extract the group identifier from an entity.
 * @property yAttribute Function to extract the attribute to aggregate from an entity.
 * @property aggregation The aggregation strategy to use.
 */
data class PlotDataBuilderAggregateWithY<E, G, Y, V>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
    val aggregation: Aggregation<Y, V>,
) {

    /**
     * Choose the X attribute used to group points along the x-axis.
     *
     * @param X The type of the x-coordinate.
     * @param xAttribute Function to extract the x-coordinate from an entity.
     * @return A [PlotDataTransformationBuilder] to continue building the plot.
     */
    fun <X> over(xAttribute: (E) -> X) = PlotDataTransformationBuilder(
        PlotDataSource(
            elements = entities,
            factory = AggregateValuesPlotData(
                specs = PlotDataSpecs(xAttribute, yAttribute, groupBy),
                aggregation = aggregation,
            ),
        ),
    )
}

/** Marker interface for builder stages that can be converted to a [PlotterBuilder]. */
interface ReadyForRender<G, X, Y> {
    /**
     * Finalize the data stage into a [PlotterBuilder].
     *
     * @return A [PlotterBuilder] object.
     */
    fun asPlotterBuilder(): PlotterBuilder<G, X, Y>
}

/**
 * Stage that holds a [PlotData] and allows chaining transformations before rendering.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate values.
 * @property plotData The plot data being built.
 */
data class PlotDataTransformationBuilder<G, X, Y>(val plotData: PlotData<G, X, Y>) : ReadyForRender<G, X, Y> {

    /**
     * Fill missing x-values for all groups with a default provided by a lambda.
     *
     * @param defaultScope A lambda providing the default y-value.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun fillMissingXValues(defaultScope: () -> Y) = fillMissingXValues(defaultScope())

    /**
     * Fill missing x-values for all groups with a constant default value.
     *
     * @param defaultValue The default y-value.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun fillMissingXValues(defaultValue: Y) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = FillMissingXValues(defaultValue),
        ),
    )

    /**
     * Provide ordering for groups/series.
     *
     * @param sortScope A lambda providing the [Ordering] strategy.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun sortGroups(sortScope: () -> Ordering<G>) = sortGroups(sortScope())

    /**
     * Provide ordering for groups/series.
     *
     * @param sortBy The [Ordering] strategy.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun sortGroups(sortBy: Ordering<G>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortGroups(sortBy),
        ),
    )

    /**
     * Provide ordering for x-values within each series.
     *
     * @param sortScope A lambda providing the [Ordering] strategy.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun sortX(sortScope: () -> Ordering<X>) = sortX(sortScope())

    /**
     * Provide ordering for x-values within each series.
     *
     * @param sortBy The [Ordering] strategy.
     * @return A [PlotDataTransformationBuilder] with the transformation applied.
     */
    fun sortX(sortBy: Ordering<X>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortPoints(sortBy),
        ),
    )

    /**
     * Convert to a [PlotterBuilder] without comparison data.
     *
     * @return A [PlotterBuilder] object.
     */
    override fun asPlotterBuilder() = PlotterBuilder(plotData)

    /**
     * Pair this data with another as comparison; returns a [PlotterBuilder] carrying both.
     *
     * @param compareScope A lambda providing another [PlotDataTransformationBuilder] as comparison.
     * @return A [PlotterBuilder] containing both original and comparison data.
     */
    fun compareTo(compareScope: () -> PlotDataTransformationBuilder<G, X, Y>): PlotterBuilder<G, X, Y> {
        val comparison = compareScope()
        return PlotterBuilder(plotData, comparison.plotData)
    }
}

/**
 * Normalize each group's Y values by the group's total (sum to 1).
 *
 * @receiver The [PlotDataTransformationBuilder].
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric y type.
 * @return A [PlotDataTransformationBuilder] with the normalization applied.
 */
fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByGroup():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByGroup(),
        ),
    )

/**
 * Normalize Y values at each x by the total across groups (stack sums to 1).
 *
 * @receiver The [PlotDataTransformationBuilder].
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric y type.
 * @return A [PlotDataTransformationBuilder] with the normalization applied.
 */
fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByX():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByX(),
        ),
    )

// TODO add transformation context collecting data about relative/normalized

/**
 * Holds plot data and optional comparison; final stage before choosing a renderer.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate values.
 * @property data Primary plot data.
 * @property comparison Optional reference/expected data.
 */
data class PlotterBuilder<G, X, Y>(val data: PlotData<G, X, Y>, val comparison: PlotData<G, X, Y>? = null) :
    ReadyForRender<G, X, Y> {

    /**
     * Returns this [PlotterBuilder].
     *
     * @return This object.
     */
    override fun asPlotterBuilder() = this
}

/**
 * Render as a stacked histogram.
 *
 * @receiver A builder stage ready for rendering.
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric y type.
 * @param styleScope A lambda to configure the [HistogramLayoutBuilder].
 * @return A configured [Plotter].
 */
fun <G, X, Y : Number> ReadyForRender<G, X, Y>.asHistogram(styleScope: HistogramLayoutBuilder<G, X>.() -> Unit) =
    asPlotterBuilder().run {
        val style = HistogramLayoutBuilder<G, X>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = HistogramRenderer(style),
        )
    }

/**
 * Render summary statistics as a box plot.
 *
 * @receiver A builder stage ready for rendering with summary data.
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric type within the summary.
 * @param styleScope A lambda to configure the [BoxPlotLayoutBuilder].
 * @return A configured [Plotter].
 */
fun <G, X, Y : Number> ReadyForRender<G, X, Summary<Y>>.asBoxPlot(styleScope: BoxPlotLayoutBuilder<G, X>.() -> Unit) =
    asPlotterBuilder().run {
        val style = BoxPlotLayoutBuilder<G, X>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = BoxPlotRenderer(style),
        )
    }

/**
 * Render numeric pairs as a scatter plot.
 *
 * @receiver A builder stage ready for rendering with numeric data.
 * @param G The group type.
 * @param X The numeric x type.
 * @param Y The numeric y type.
 * @param styleScope A lambda to configure the [ScatterLayoutBuilder].
 * @return A configured [Plotter].
 */
fun <G, X : Number, Y : Number> ReadyForRender<G, X, Y>.asScatterPlot(styleScope: ScatterLayoutBuilder<G>.() -> Unit) =
    asPlotterBuilder().run {
        val style = ScatterLayoutBuilder<G>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = ScatterPlotRenderer(style),
        )
    }

/**
 * Render series as a line plot.
 *
 * @receiver A builder stage ready for rendering with numeric y data.
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric y type.
 * @param styleScope A lambda to configure the [LinePlotLayoutBuilder].
 * @return A configured [Plotter].
 */
fun <G, X, Y : Number> ReadyForRender<G, X, Y>.asLinePlot(styleScope: LinePlotLayoutBuilder<G>.() -> Unit) =
    asPlotterBuilder().run {
        val style = LinePlotLayoutBuilder<G>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = LinePlotRenderer(style),
        )
    }
