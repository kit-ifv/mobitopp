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

/**
 * Layout contract for a box plot of summary statistics per group/x.
 *
 * It provides the following layout parameters:
 *  - name: The name/title of the plot.
 *  - xAxisLabel: Label for the x-axis.
 *  - colorAxisLabel: Label for the color axis (legend).
 *  - groupLabel: Function to map a group identifier to its display label.
 *  - xLabel: Function to map an x-coordinate to its display label.
 *  - coloring: Function to map a group identifier to a color.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 */
interface BoxPlotLayout<G, X> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val colorAxisLabel: String
    val groupLabel: (G) -> String
    val xLabel: (X) -> String
    val coloring: (G) -> RGB
}

/**
 * Mutable builder for [BoxPlotLayout] options.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @property name The name/title of the plot.
 * @property xAxisLabel Label for the x-axis.
 * @property colorAxisLabel Label for the color axis (legend).
 * @property groupLabel Function to map a group identifier to its display label.
 * @property xLabel Function to map an x-coordinate to its display label.
 * @property coloring Function to map a group identifier to a color.
 */
data class BoxPlotLayoutBuilder<G, X>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var colorAxisLabel: String = "color",
    override var groupLabel: (G) -> String = { it.toString() },
    override var xLabel: (X) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : BoxPlotLayout<G, X>

/**
 * Renderer producing a box plot using the Lets-Plot backend.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate values in the summary (must be a [Number]).
 * @property style The layout configuration for the box plot.
 */
class BoxPlotRenderer<G, X, Y : Number>(override val style: BoxPlotLayout<G, X>) : PlotRenderer<G, X, Summary<Y>> {

    /**
     * Creates a box plot from the given [data] and optional [comparisonData].
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A [Plot] object representing the box plot.
     */
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
