package core.results.plots

import core.results.plots.render.LinePlotLayoutBuilder
import core.results.plots.render.NumericLinePlotRenderer

fun <G, X : Number, Y : Number> ReadyForRender<G, X, Y>.asNumericLinePlot(
    styleScope: LinePlotLayoutBuilder<G>.() -> Unit
) =
    asPlotterBuilder().run {
        val style = LinePlotLayoutBuilder<G>()
        style.styleScope()

        Plotter(
            data = data,
            comparison = comparison,
            renderer = NumericLinePlotRenderer(style)
        )
    }
