package core.results.plots.render

import core.results.plots.KIT_GREEN
import core.results.plots.PlotLayout
import core.results.plots.PlotRenderer
import core.results.plots.RGB
import core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol

/** Layout contract for a scatter plot. */
interface ScatterLayout<G> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val yAxisLabel: String
    val colorAxisLabel: String
    val groupLabel: (G) -> String
    val coloring: (G) -> RGB
}

/** Mutable builder for Scatter plot layout options. */
data class ScatterLayoutBuilder<G>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var yAxisLabel: String = "y",
    override var colorAxisLabel: String = "color",
    override var groupLabel: (G) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
    override val comparisonLabel: (Any) -> String = { "error" }
) : ScatterLayout<G>

/** Renderer producing a scatter plot using the Lets-Plot backend. */
class ScatterPlotRenderer<G, X : Number, Y : Number>(
    override val style: ScatterLayout<G>,
) : PlotRenderer<G, X, Y> {

    override fun plot(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): Plot {
        val builder = DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsDouble().yAsDouble()

        val colorMap = builder.colorByGroup(style.coloring)
        val df = builder.build()

        return df.plot {
            layout {
                title = style.name
                size = DEFAULT_PLOT_SIZE
            }

            points {
                x(X_COL) {
                    axis.name = style.xAxisLabel
                }

                y(Y_COL) {
                    axis.name = style.yAxisLabel
                }

                fillColorFromMap(GROUP_COL, colorMap, style.colorAxisLabel)

                if (comparisonData != null) {
                    symbol(IS_COMP_COL) {
                        scale = booleanScale(Symbol.CIRCLE_FILLED, Symbol.CROSS)
                        legend.name = "is expected data"
                    }

                    color(GROUP_COL) {
                        legend.name = style.colorAxisLabel
                        scale = categorical(
                            domain = colorMap.domain,
                            range = colorMap.range
                        )
                    }
                }
            }
        }
    }
}
