package core.results.plots.render

import core.results.plots.KIT_GREEN
import core.results.plots.PlotLayout
import core.results.plots.PlotRenderer
import core.results.plots.RGB
import core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.Position
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.feature.position
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars

interface HistogramLayout<G, X> : PlotLayout {
    override val name: String
    val stackAxisLabel: String
    val xAxisLabel: String
    val groupLabel: (G) -> String
    val xLabel: (X) -> String
    val coloring: (G) -> RGB
}

data class HistogramLayoutBuilder<G, X>(
    override var name: String = "plot",
    override var stackAxisLabel: String = "stack",
    override var xAxisLabel: String = "x",
    override var groupLabel: (G) -> String = { it.toString() },
    override var xLabel: (X) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : HistogramLayout<G, X>

class HistogramRenderer<G, X, Y : Number>(
    override val style: HistogramLayout<G, X>,
) : PlotRenderer<G, X, Y> {

    override fun plot(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): Plot {
        val builder = DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsString {
            style.xLabel(it)
        }.yAsDouble()

        val colorMap = builder.colorByGroup(style.coloring)

        val keyCol = "x_key"
        val df = builder.combineCompAndXLabel(
            keyCol,
            style.comparisonLabel
        ).build()

        return df.plot {
            layout {
                title = style.name
                size = DEFAULT_PLOT_SIZE
            }

            bars {
                alpha = DEFAULT_ALPHA

                x(keyCol) {
                    axis.name = style.xAxisLabel
                }

                y(Y_COL) {
                    axis.name = "count"
                }

                fillColorFromMap(GROUP_COL, colorMap, style.stackAxisLabel)

                position = Position.Companion.stack()

                if (comparisonData != null) {
                    borderLine.color(IS_COMP_COL) {
                        scale = booleanColorScale() // booleanScale(Color.RED, Color.BLACK)
                        legend.name = "is expected data"
                    }
                }
            }
        }
    }
}
