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
import units.UnitIntervalValue

fun <E> forData(entities: () -> List<E>) = PlotDataBuilderWithEntities(entities)
fun <E> forData(resource: Resource<E>) = PlotDataBuilderWithEntities { resource.elements.toList() }
fun <E> forData(iterable: Iterable<E>) = PlotDataBuilderWithEntities { iterable.toList() }

fun <E> noGrouping(): (E) -> Unit = { _ -> }

data class PlotDataBuilderWithEntities<E>(
    override val entities: () -> List<E>
) : PlotDataGroupingProvider<E, Unit> {

    override val groupBy: (E) -> Unit
        get() = noGrouping()

    fun <G> groupBy(groupBy: (E) -> G) = PlotDataBuilderWithGrouping(entities, groupBy)
}

data class PlotDataBuilderWithGrouping<E, G>(
    override val entities: () -> List<E>,
    override val groupBy: (E) -> G,
) : PlotDataGroupingProvider<E, G>

@Suppress("TooManyFunctions")
interface PlotDataGroupingProvider<E, G> {
    val entities: () -> List<E>
    val groupBy: (E) -> G

    fun <Y> plot(yAttribute: (E) -> Y) = PlotDataBuilderAllValuesWithY(
        entities,
        groupBy,
        yAttribute,
    )

    fun <Y : Comparable<Y>> plotMinOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MinBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotMaxOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        MaxBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotMedianOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        LowerMedianBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotQuantileOf(yAttribute: (E) -> Y, quantile: UnitIntervalValue) =
        PlotDataBuilderAggregateWithY(
            entities,
            groupBy,
            yAttribute,
            LowerQuantileBy(quantile = quantile, sortBy = { it })
        )

    fun <Y : Number> plotSumOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Sum
    )

    fun <Y : Number> plotMeanOf(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        Mean
    )

    fun <Y : Comparable<Y>> summarize(yAttribute: (E) -> Y) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        SummarizeBy(sortBy = { it })
    )

    private fun <E> noYAtt(): (E) -> Unit = { _ -> }

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

    fun <A, Y> aggregateBy(yAttribute: (E) -> A, aggregation: Aggregation<A, Y>) = PlotDataBuilderAggregateWithY(
        entities,
        groupBy,
        yAttribute,
        aggregation
    )
}

data class PlotDataBuilderAllValuesWithY<E, G, Y>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
) {
    fun <X> over(xAttribute: (E) -> X) = PlotDataTransformationBuilder(
        PlotDataSource(
            elements = entities,
            factory = AllValuesPlotData(
                specs = PlotDataSpecs(xAttribute, yAttribute, groupBy),
            )
        )
    )
}

data class PlotDataBuilderAggregateWithY<E, G, Y, V>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> Y,
    val aggregation: Aggregation<Y, V>
) {

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

interface ReadyForRender<G, X, Y> {
    fun asPlotterBuilder(): PlotterBuilder<G, X, Y>
}

data class PlotDataTransformationBuilder<G, X, Y>(
    val plotData: PlotData<G, X, Y>
) : ReadyForRender<G, X, Y> {

    fun fillMissingXValues(defaultScope: () -> Y) = fillMissingXValues(defaultScope())

    fun fillMissingXValues(defaultValue: Y) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = FillMissingXValues(defaultValue)
        )
    )

    fun sortGroups(sortScope: () -> Ordering<G>) = sortGroups(sortScope())

    fun sortGroups(sortBy: Ordering<G>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortGroups(sortBy)
        )
    )

    fun sortX(sortScope: () -> Ordering<X>) = sortX(sortScope())

    fun sortX(sortBy: Ordering<X>) = PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            transformer = SortPoints(sortBy)
        )
    )

    override fun asPlotterBuilder() = PlotterBuilder(plotData)

    fun compareTo(compareScope: () -> PlotDataTransformationBuilder<G, X, Y>): PlotterBuilder<G, X, Y> {
        val comparison = compareScope()
        return PlotterBuilder(plotData, comparison.plotData)
    }
}

fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByGroup():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByGroup()
        )
    )

fun <G, X, Y : Number> PlotDataTransformationBuilder<G, X, Y>.normalizeByX():
    PlotDataTransformationBuilder<G, X, Double> =
    PlotDataTransformationBuilder(
        TransformedPlotData(
            original = plotData,
            NormalizeByX()
        )
    )

// TODO add transformation context collecting data about relative/normalized

data class PlotterBuilder<G, X, Y>(
    val data: PlotData<G, X, Y>,
    val comparison: PlotData<G, X, Y>? = null,
) : ReadyForRender<G, X, Y> {

    override fun asPlotterBuilder() = this
}

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
