package core.results.plots

import core.results.plots.render.LinePlotLayoutBuilder
import core.results.plots.render.NumericLinePlotRenderer

/**
 * Converts a [ReadyForRender] configuration into a [Plotter] for a numeric line plot.
 *
 * @receiver The [ReadyForRender] configuration containing data and comparison data.
 * @param G The type of the group identifier.
 * @param X The type of the numeric x-coordinate.
 * @param Y The type of the numeric y-coordinate values.
 * @param styleScope A lambda to configure the [LinePlotLayoutBuilder].
 * @return A [Plotter] configured with a [NumericLinePlotRenderer].
 */
fun <G, X : Number, Y : Number> ReadyForRender<G, X, Y>.asNumericLinePlot(
    styleScope: LinePlotLayoutBuilder<G>.() -> Unit,
) = asPlotterBuilder().run {
    val style = LinePlotLayoutBuilder<G>()
    style.styleScope()

    Plotter(
        data = data,
        comparison = comparison,
        renderer = NumericLinePlotRenderer(style),
    )
}
