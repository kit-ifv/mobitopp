package core.results.plots.render

import core.results.plots.KIT_GREEN
import core.results.plots.PlotLayout
import core.results.plots.PlotRenderer
import core.results.plots.RGB
import core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType

/** Layout contract for a line plot. */
interface LinePlotLayout<G> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val yAxisLabel: String
    val groupAxisLabel: String
    val groupLabel: (G) -> String
    val coloring: (G) -> RGB
}

/** Mutable builder for LinePlot layout options. */
data class LinePlotLayoutBuilder<G>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var yAxisLabel: String = "y",
    override var groupAxisLabel: String = "group",
    override var groupLabel: (G) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : LinePlotLayout<G>

/** Renderer producing a line plot using the Lets-Plot backend. */
class LinePlotRenderer<G, X, Y : Number>(
    override val style: LinePlotLayout<G>,
) : PlotRenderer<G, X, Y> {

    override fun plot(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): Plot {
        val builder = DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsString().yAsDouble()

        val colorMap = builder.colorByGroup(style.coloring)
        val df = builder.build()

        return df.plot {
            layout {
                title = style.name
                size = DEFAULT_PLOT_SIZE
            }

            line {
                x(X_COL) {
                    axis.name = style.xAxisLabel
                }

                y(Y_COL) {
                    axis.name = style.yAxisLabel
                }

                color(GROUP_COL) {
                    applyScale(colorMap, style.groupAxisLabel)
                }

                if (comparisonData != null) {
                    type(IS_COMP_COL) {
                        scale = booleanScale(LineType.DASHED, LineType.SOLID)
                        legend.name = "is expected data"
                    }
                }
            }
        }
    }
}
