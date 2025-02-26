package modeling.eval

import modeling.steps.Context
import modeling.steps.ModelStep
import modeling.validation.Warning
import modeling.validation.validateScope
import units.UnitIntervalValue

data class PlotterBuilder<E, G, X, A, Y, C>(
    val style: PlotStyling<G, X, C>,
    val values: PlotDataSpecification<E, G, X, A, Y, C>
)

fun Context.addPlot(setup: () -> Plotter<*, *, *, *, *, *>) = runStep {
    object : ModelStep {

        private val plotter = setup()
        override val name = "Add Plot ${plotter.name}"

        override fun execute() {
            plotter.plot()
        }

        override fun verifyInput(): Warning? = validateScope { }

        override fun mockBehavior(): Warning? = validateScope { }
    }
}

fun <E> forData(entities: () -> List<E>) = PlotterBuilderWithEntities(entities)

@Suppress("TooManyFunctions")
data class PlotterBuilderWithEntities<E>(
    val entities: () -> List<E>,
) {

    private fun <E> noGrouping(): (E) -> Unit = { _ -> }

    fun <G> groupBy(groupBy: (E) -> G) = PlotterBuilderWithGrouping(entities, groupBy)

    fun <Y> plot(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plot(yAttribute)

    fun <Y : Comparable<Y>> plotMinOf(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plotMinOf(yAttribute)

    fun <Y : Comparable<Y>> plotMaxOf(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plotMaxOf(yAttribute)

    fun <Y : Comparable<Y>> plotMedianOf(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plotMedianOf(yAttribute)

    fun <Y : Comparable<Y>> plotQuantileOf(yAttribute: (E) -> Y, quantile: UnitIntervalValue) =
        this.groupBy(noGrouping()).plotQuantileOf(yAttribute, quantile)

    fun <Y : Number> plotSumOf(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plotSumOf(yAttribute)

    fun <Y : Number> plotMeanOf(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).plotSumOf(yAttribute)

    fun <Y : Comparable<Y>> summarize(yAttribute: (E) -> Y) = this.groupBy(noGrouping()).summarize(yAttribute)

    fun <X> count(xAttribute: (E) -> X) = this.groupBy(noGrouping()).count(xAttribute)

    fun <A, Y> aggregateBy(yAttribute: (E) -> A, aggregation: Aggregation<A, Y>) =
        this.groupBy(noGrouping()).aggregateBy(yAttribute, aggregation)
}

data class PlotterBuilderWithGrouping<E, G>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
) {

    fun <Y> plot(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.AllValues()
    )

    fun <Y : Comparable<Y>> plotMinOf(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.MinBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotMaxOf(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.MaxBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotMedianOf(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.LowerMedianBy(sortBy = { it })
    )

    fun <Y : Comparable<Y>> plotQuantileOf(yAttribute: (E) -> Y, quantile: UnitIntervalValue) =
        PlotterBuilderWithYAxis(
            entities,
            groupBy,
            yAttribute,
            Aggregation.LowerQuantileBy(quantile = quantile, sortBy = { it })
        )

    fun <Y : Number> plotSumOf(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.Sum
    )

    fun <Y : Number> plotMeanOf(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.Mean
    )

    fun <Y : Comparable<Y>> summarize(yAttribute: (E) -> Y) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        Aggregation.SummarizeBy(sortBy = { it })
    )

    private fun <E> noYAtt(): (E) -> Unit = { _ -> }

    fun <X> count(xAttribute: (E) -> X) = PlotterBuilderWithXAxis(
        entities,
        groupBy,
        noYAtt(),
        Aggregation.Count,
        xAttribute
    )

    fun <A, Y> aggregateBy(yAttribute: (E) -> A, aggregation: Aggregation<A, Y>) = PlotterBuilderWithYAxis(
        entities,
        groupBy,
        yAttribute,
        aggregation
    )
}

data class PlotterBuilderWithYAxis<E, G, A, Y>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> A,
    val aggregation: Aggregation<A, Y>,
) {

    fun <X> over(xAttribute: (E) -> X) = PlotterBuilderWithXAxis(
        entities,
        groupBy,
        yAttribute,
        aggregation,
        xAttribute
    )
}

data class PlotterBuilderWithXAxis<E, G, X, A, Y>(
    val entities: () -> List<E>,
    val groupBy: (E) -> G,
    val yAttribute: (E) -> A,
    val aggregation: Aggregation<A, Y>,
    val xAttribute: (E) -> X,
) {

    fun <C> colorBy(colorAttribute: (E?, G, X, Y) -> C) = PlotterBuilderWithColor(
        PlotDataSpecification(
            entities,
            xAttribute,
            yAttribute,
            aggregation,
            groupBy,
            colorAttribute
        )
    )

    fun colorByEntity() = colorBy { e, _, _, _ -> e }

    fun colorByGroup() = colorBy { _, g, _, _ -> g }

    fun colorByX() = colorBy { _, _, x, _ -> x }

    fun colorByY() = colorBy { _, _, _, y -> y }
}

private fun <E, G, X, Y> noColoring(): (E?, G, X, Y) -> Unit = { _, _, _, _ -> }

data class PlotterBuilderWithColor<E, G, X, A, Y, C>(
    val values: PlotDataSpecification<E, G, X, A, Y, C>
) {

    fun withStyle(lambda: PlotStyleBuilder<G, X, C>.() -> Unit): PlotterBuilder<E, G, X, A, Y, C> {
        val style = PlotStyleBuilder<G, X, C>()

        style.lambda()

        return PlotterBuilder(style, values)
    }
}

fun <B, E, G, X> B.withStyle(
    lambda: PlotStyleBuilder<G, X, Unit>.() -> Unit
) where B : PlotterBuilderWithXAxis<E, G, X, Unit, Int> = colorBy(noColoring()).withStyle(lambda)

data class PlotStyleBuilder<G, X, C>(
    override var name: String = "plot",
    override var xLabel: String = "x",
    override var yLabel: String = "y",
    override var groupLabel: String = "group",
    override var colorLabel: String = "color",
    override var xOrder: Ordering<X> = Ordering.Arbitrary(),
    override var groupOrder: Ordering<G> = Ordering.Arbitrary(),
    override var colorMap: (C) -> RGB = { _ -> randomColor() }
) : PlotStyling<G, X, C>

fun <B, E, G, X, C> B.asHistogram(
    normalize: Boolean = true,
    relative: Boolean = true,
) where B : PlotterBuilder<E, G, X, Unit, Int, C> = HistogramPlotter(
    style,
    values,
    normalize = normalize,
    relative = relative,
)

fun <B, E, G, X, C> B.asLineChart(
    normalize: Boolean = true,
    relative: Boolean = true,
    compareTo: ComparisonDataSpecification<Nothing, G, X, Int>? = null,
) where B : PlotterBuilder<E, G, X, Unit, Int, C> = TimeChartPlotter(
    style,
    values,
    comparisonData = compareTo,
    normalize = normalize,
    relative = relative,
)
