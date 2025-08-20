package core.results.plots

import core.modelsteps.Resource


data class ComparisonPlotterBuilder<in T, E, G, X, A, Y, C>(
    val style: PlotStyling<G, X, C>,
    val values: PlotDataSpecification<E, G, X, A, Y, C>,
    val comparisonData: ComparisonDataSpecification<T, G, X, Y>
)

fun <B, T, E, G, X, A, Y, C> B.compareToResource(resource: Resource<T>) where B : PlotterBuilder<E, G, X, A, Y, C> =
    PlotterBuilderWithComparison(style, values, resource)


data class PlotterBuilderWithComparison<T, E, G, X, A, Y, C>(
    val style: PlotStyling<G, X, C>,
    val values: PlotDataSpecification<E, G, X, A, Y, C>,
    val resource: Resource<T>
) {

    fun groupBy(comparisonGrouping: (T) -> G) =
        PlotterBuilderWithComparisonGrouping(style, values, resource, comparisonGrouping)

    fun <G2> groupBy(groupBy: (T) -> G2, mapping: (G2) -> G) =
        PlotterBuilderWithComparisonGrouping(style, values, resource) { mapping(groupBy(it)) }

    fun withSameAttributes(mapping: (T) -> E, yAttribute: (T) -> Y) = ComparisonPlotterBuilder(
        style = style,
        values = values,
        comparisonData =
            ComparisonDataSpecification(
                resource = resource,
                groupBy = {it: T -> values.groupBy(mapping(it)) },
                xAxis = {it: T -> values.xAttribute(mapping(it))},
                yAxis =   yAttribute,
                labelPrefix = "comp_"
            )
    )
}

data class PlotterBuilderWithComparisonGrouping<T, E, G, X, A, Y, C>(
    val style: PlotStyling<G, X, C>,
    val values: PlotDataSpecification<E, G, X, A, Y, C>,
    val resource: Resource<T>,
    val comparisonGrouping: (T) -> G) {

    fun plot(comparisonAttribute: (T) -> Y) =
        PlotterBuilderWithComparisonY(style, values, resource, comparisonGrouping, comparisonAttribute)

}

data class PlotterBuilderWithComparisonY<T, E, G, X, A, Y, C>(
    val style: PlotStyling<G, X, C>,
    val values: PlotDataSpecification<E, G, X, A, Y, C>,
    val resource: Resource<T>,
    val comparisonGrouping: (T) -> G,
    val comparisonAttribute: (T) -> Y){

    fun <X2> over(xAttribute: (T) -> X2, mapping: (X2) -> X, labelPrefix: String = "expected:") =
        ComparisonPlotterBuilder(
            style = style,
            values = values,
            comparisonData =
                ComparisonDataSpecification(
                    resource = resource,
                    groupBy = comparisonGrouping,
                    xAxis = {x: T -> mapping(xAttribute(x))},
                    yAxis = comparisonAttribute,
                    labelPrefix = labelPrefix
                )
        )
}

fun <B, T : Any, E, G : Any, X : Any, C> B.asHistogram(
    normalize: Boolean = true,
    relative: Boolean = true,
) where B : ComparisonPlotterBuilder<T, E, G, X, Unit, Int, C> = HistogramPlotter(
    style,
    values,
    comparisonData = comparisonData,
    normalize = normalize,
    relative = relative,
)

fun <B, T, E, G , X, A, Y, C> B.asLineChart() where B : ComparisonPlotterBuilder<T, E, G, X, A, Y, C> = LineChartPlotter(
    style,
    values,
    comparisonData = comparisonData
)

fun <B, T, E, G, X: Comparable<X>, A, C> B.asScalableSortableLineChart(
    relative: Boolean = true,
    normalize: Boolean = true,
) where B: ComparisonPlotterBuilder<T, E, G, X, A, Double, C> =
    ScalableSortableLineChartPlotter(
        style,
        values,
        comparisonData = comparisonData,
        relative = relative,
        normalize = normalize
    )

fun <B,T, E, G, X, A: Number, Y: Summary<A>, C> B.asBoxPlot() where B : ComparisonPlotterBuilder<T, E, G, X, A, Y, C> =
    BoxPlotter(style, values, comparisonData = comparisonData)

fun <B, T, E, G, X: Number, A, Y: Number, C> B.asScatterPlot() where B : ComparisonPlotterBuilder<T, E, G, X, A, Y, C> =
    ScatterPlotter(style, values, comparisonData = comparisonData)
