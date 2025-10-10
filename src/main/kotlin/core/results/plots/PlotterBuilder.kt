package core.results.plots

import core.modelsteps.Resource
import core.results.plots.data.AggregateValuesPlotData
import core.results.plots.data.Aggregation
import core.results.plots.data.AllValuesPlotData
import core.results.plots.data.Count
import core.results.plots.data.FillMissingXValues
import core.results.plots.data.LowerMedianBy
import core.results.plots.data.LowerQuantileBy
import core.results.plots.data.MaxBy
import core.results.plots.data.Mean
import core.results.plots.data.MinBy
import core.results.plots.data.NormalizeByGroup
import core.results.plots.data.NormalizeByX
import core.results.plots.data.Ordering
import core.results.plots.data.PlotData
import core.results.plots.data.PlotDataSource
import core.results.plots.data.PlotDataSpecs
import core.results.plots.data.SortGroups
import core.results.plots.data.SortPoints
import core.results.plots.data.Sum
import core.results.plots.data.SummarizeBy
import core.results.plots.data.Summary
import core.results.plots.data.TransformedPlotData
import core.results.plots.render.BoxPlotLayoutBuilder
import core.results.plots.render.BoxPlotRenderer
import core.results.plots.render.HistogramLayoutBuilder
import core.results.plots.render.HistogramRenderer
import core.results.plots.render.LinePlotLayoutBuilder
import core.results.plots.render.LinePlotRenderer
import core.results.plots.render.ScatterLayoutBuilder
import core.results.plots.render.ScatterPlotRenderer
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
 */
fun <E> forData(entities: () -> List<E>) = PlotDataBuilderWithEntities(entities)

/**
 * Entry point to build plot data from a Resource wrapper.
 */
fun <E> forData(resource: Resource<E>) = PlotDataBuilderWithEntities { resource.elements.toList() }

/**
 * Entry point to build plot data from an Iterable.
 */
fun <E> forData(iterable: Iterable<E>) = PlotDataBuilderWithEntities { iterable.toList() }

/**
 * Convenience grouping function for plots without a grouping attribute, uses Unit instead.
 */
fun <E> noGrouping(): (E) -> Unit = { _ -> }

/**
 * Stage 1 of the builder: entities are known, grouping not yet specified (defaults to no grouping).
 */
data class PlotDataBuilderWithEntities<E>(
    override val entities: () -> List<E>
) : PlotDataGroupingProvider<E, Unit> {

    override val groupBy: (E) -> Unit
        get() = noGrouping()

    /**
     * Specify the grouping function. Each distinct value defines a series ([core.results.plots.data.Trace]).
     */
    fun <G> groupBy(groupBy: (E) -> G) = PlotDataBuilderWithGrouping(entities, groupBy)
}

/**
 * Stage 2 of the builder: entities plus grouping function are known.
 */
data class PlotDataBuilderWithGrouping<E, G>(
    override val entities: () -> List<E>,
    override val groupBy: (E) -> G,
) : PlotDataGroupingProvider<E, G>

/**
 * Provides operations to configure which values are plotted and how they are aggregated.
 */
@Suppress("TooManyFunctions")
interface PlotDataGroupingProvider<E, G> {
    val entities: () -> List<E>
    val groupBy: (E) -> G

    /**
     * Plot all raw Y values against X for each group.
     */
    fun <Y> plot(yAttribute: (E) -> Y) = PlotDataBuilderAllValuesWithY(
        entities,
        groupBy,
        yAttribute,
    )

    /** Plot the minimum Y per X for each group. */
    fun <Y : Comparable<Y>> plotMinOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MinBy(sortBy = { it })
    )

    /** Plot the maximum Y per X for each group. */
    fun <Y : Comparable<Y>> plotMaxOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MaxBy(sortBy = { it })
    )

    /** Plot the lower median (50th percentile) Y per X for each group. */
    fun <Y : Comparable<Y>> plotMedianOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        LowerMedianBy(sortBy = { it })
    )

    /** Plot an arbitrary lower quantile of Y per X for each group. */
    fun <Y : Comparable<Y>> plotQuantileOf(yAttribute: (E) -> Y, quantile: UnitIntervalValue) =
        PlotDataBuilderAggregateWithY(
            entities,
            groupBy,
            yAttribute,
            LowerQuantileBy(quantile = quantile, sortBy = { it })
        )

    /** Plot the sum of Y per X for each group. */
    fun <Y : Number> plotSumOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Sum
    )

    /** Plot the mean of Y per X for each group. */
    fun <Y : Number> plotMeanOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Mean
    )

    /** Summarize Y per X for each group into box-plot friendly statistics. */
    fun <Y : Comparable<Y>> summarize(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        SummarizeBy(sortBy = { it })
    )

    private fun <E> noYAtt(): (E) -> Unit = { _ -> }

    /** Count elements by X for each group. */
    fun <X> count(xAttribute: (E) -> X) =
        PlotDataTransformationBuilder(
            PlotDataSource(
                elements = entities,
                factory = AggregateValuesPlotData(
                    specs = PlotDataSpecs(xAttribute, noYAtt(), groupBy),
                    aggregation = Count,
                )
            )
        )

    /** Custom aggregation of a computed attribute yAttribute with the provided aggregation. */
    fun <A, Y> aggregateBy(yAttribute: (E) -> A, aggregation: Aggregation<A, Y>) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        aggregation
    )
}

/**
 * Stage 3 of the builder: all values of yAttribute will be kept for plotting.
 */
data class PlotDataBuilderAllValuesWithY<E, G, Y>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
) {
    /** Choose the X attribute used to group points along the x-axis. */
    fun <X> over(xAttribute: (E) -> X) = PlotDataTransformationBuilder(
        PlotDataSource(
            elements = entities,
            factory = AllValuesPlotData(
                specs = PlotDataSpecs(xAttribute, yAttribute, groupBy),
            )
        )
    )
}

/**
 * Stage 3 of the builder: yAttribute will be aggregated with the given aggregation.
 */
data class PlotDataBuilderAggregateWithY<E, G, Y, V>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
    val aggregation: Aggregation<Y, V>
) {

    /** Choose the X attribute used to group points along the x-axis. */
    fun <X> over(xAttribute: (E) -> X) =
        PlotDataTransformationBuilder(
            PlotDataSource(
                elements = entities,
                factory = AggregateValuesPlotData(
                    specs = PlotDataSpecs(xAttribute, yAttribute, groupBy),
                    aggregation = aggregation,
                )
            )
        )
}

/** Marker interface for builder stages that can be converted to a PlotterBuilder. */
interface ReadyForRender<G, X, Y> {
    /** Finalize the data stage into a PlotterBuilder. */
    fun asPlotterBuilder(): PlotterBuilder<G, X, Y>
}

/**
 * Stage that holds a PlotData and allows chaining transformations before rendering.
 */
data class PlotDataTransformationBuilder<G, X, Y>(
    val plotData: PlotData<G, X, Y>
) : ReadyForRender<G, X, Y> {

    /** Fill missing x-values for all groups with a default provided by a lambda. */
    fun fillMissingXValues(defaultScope: () -> Y) = fillMissingXValues(defaultScope())

    /** Fill missing x-values for all groups with a constant default value. */
    fun fillMissingXValues(defaultValue: Y) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = FillMissingXValues(defaultValue)
        )
    )

    /** Provide ordering for groups/series. */
    fun sortGroups(sortScope: () -> Ordering<G>) = sortGroups(sortScope())

    /** Provide ordering for groups/series. */
    fun sortGroups(sortBy: Ordering<G>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortGroups(sortBy)
        )
    )

    /** Provide ordering for x-values within each series. */
    fun sortX(sortScope: () -> Ordering<X>) = sortX(sortScope())

    /** Provide ordering for x-values within each series. */
    fun sortX(sortBy: Ordering<X>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortPoints(sortBy)
        )
    )

    /** Convert to a PlotterBuilder without comparison data. */
    override fun asPlotterBuilder() = PlotterBuilder(plotData)

    /** Pair this data with another as comparison; returns a PlotterBuilder carrying both. */
    fun compareTo(compareScope: () -> PlotDataTransformationBuilder<G, X, Y>): PlotterBuilder<G, X, Y> {
        val comparison = compareScope()
        return PlotterBuilder(plotData, comparison.plotData)
    }
}

/** Normalize each group's Y values by the group's total (sum to 1). */
fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByGroup():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByGroup()
        )
    )

/** Normalize Y values at each x by the total across groups (stack sums to 1). */
fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByX():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByX()
        )
    )

// TODO add transformation context collecting data about relative/normalized

/** Holds plot data and optional comparison; final stage before choosing a renderer. */
data class PlotterBuilder<G, X, Y>(
    val data: PlotData<G, X, Y>,
    val comparison: PlotData<G, X, Y>? = null,
) : ReadyForRender<G, X, Y> {

    /** No-op: already a PlotterBuilder. */
    override fun asPlotterBuilder() = this
}

/** Render as a stacked histogram. */
fun <G, X, Y : Number> ReadyForRender<G, X, Y>.asHistogram(styleScope: HistogramLayoutBuilder<G, X>.() -> Unit) =
    asPlotterBuilder().run {
        val style = HistogramLayoutBuilder<G, X>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = HistogramRenderer(style)
        )
    }

/** Render summary statistics as a box plot. */
fun <G, X, Y : Number> ReadyForRender<G, X, Summary<Y>>.asBoxPlot(styleScope: BoxPlotLayoutBuilder<G, X>.() -> Unit) =
    asPlotterBuilder().run {
        val style = BoxPlotLayoutBuilder<G, X>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = BoxPlotRenderer(style)
        )
    }

/** Render numeric pairs as a scatter plot. */
fun <G, X : Number, Y : Number> ReadyForRender<G, X, Y>.asScatterPlot(styleScope: ScatterLayoutBuilder<G>.() -> Unit) =
    asPlotterBuilder().run {
        val style = ScatterLayoutBuilder<G>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = ScatterPlotRenderer(style)
        )
    }

/** Render series as line plot. */
fun <G, X, Y : Number> ReadyForRender<G, X, Y>.asLinePlot(styleScope: LinePlotLayoutBuilder<G>.() -> Unit) =
    asPlotterBuilder().run {
        val style = LinePlotLayoutBuilder<G>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = LinePlotRenderer(style)
        )
    }
