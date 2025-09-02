package core.results.plots.render

import core.results.plots.KIT_GREEN
import core.results.plots.PlotLayout
import core.results.plots.PlotRenderer
import core.results.plots.RGB
import core.results.plots.data.PlotData
import core.results.plots.data.Summary
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.boxes

/** Layout contract for a box plot of summary statistics per group/x. */
interface BoxPlotLayout<G, X> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val colorAxisLabel: String
    val groupLabel: (G) -> String
    val xLabel: (X) -> String
    val coloring: (G) -> RGB
}

/** Mutable builder for BoxPlot layout options. */
data class BoxPlotLayoutBuilder<G, X>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var colorAxisLabel: String = "color",
    override var groupLabel: (G) -> String = { it.toString() },
    override var xLabel: (X) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : BoxPlotLayout<G, X>

/** Renderer producing a box plot using the Lets-Plot backend. */
class BoxPlotRenderer<G, X, Y : Number>(
    override val style: BoxPlotLayout<G, X>,
) : PlotRenderer<G, X, Summary<Y>> {

    override fun plot(data: PlotData<G, X, Summary<Y>>, comparisonData: PlotData<G, X, Summary<Y>>?): Plot {
        val builder = DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsString {
            style.xLabel(it)
        }.yAsDoubleSummary()

        val colorMap = builder.colorByGroup(style.coloring)

        val keyCol = "x_key"
        val df = builder.combineCompAndXLabel(keyCol, style.comparisonLabel).build()

        return df.plot {
            layout {
                title = style.name
                size = DEFAULT_PLOT_SIZE
            }

            boxes {
                alpha = DEFAULT_ALPHA

                x(keyCol) {
                    axis.name = layout.xAxisLabel
                }

                yMin(MIN_COL)
                lower(LOWER_QUART_COL)
                middle(MEDIAN_COL)
                upper(UPPER_QUART_COL)
                yMax(MAX_COL)

                fillColorFromMap(GROUP_COL, colorMap, style.colorAxisLabel)

                if (comparisonData != null) {
                    borderLine.color(IS_COMP_COL) {
                        scale = booleanColorScale()
                        legend.name = "is expected data"
                    }
                }
            }
        }
    }
}
