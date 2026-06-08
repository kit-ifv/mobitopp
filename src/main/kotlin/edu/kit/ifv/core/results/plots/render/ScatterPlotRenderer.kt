package edu.kit.ifv.core.results.plots.render
import edu.kit.ifv.core.results.plots.KIT_GREEN
import edu.kit.ifv.core.results.plots.PlotLayout
import edu.kit.ifv.core.results.plots.PlotRenderer
import edu.kit.ifv.core.results.plots.RGB
import edu.kit.ifv.core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.settings.Symbol

/**
 * Layout contract for a scatter plot.
 *
 * It provides the following layout parameters:
 *  - name: The name/title of the plot.
 *  - xAxisLabel: Label for the x-axis.
 *  - yAxisLabel: Label for the y-axis.
 *  - colorAxisLabel: Label for the color axis (legend).
 *  - groupLabel: Function to map a group identifier to its display label.
 *  - coloring: Function to map a group identifier to a color.
 *
 * @param G The type of the group identifier.
 */
interface ScatterLayout<G> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val yAxisLabel: String
    val colorAxisLabel: String
    val groupLabel: (G) -> String
    val coloring: (G) -> RGB
}

/**
 * Mutable builder for Scatter plot layout options.
 *
 * @param G The type of the group identifier.
 * @property name The name/title of the plot.
 * @property xAxisLabel Label for the x-axis.
 * @property yAxisLabel Label for the y-axis.
 * @property colorAxisLabel Label for the color axis (legend).
 * @property groupLabel Function to map a group identifier to its display label.
 * @property coloring Function to map a group identifier to a color.
 * @property comparisonLabel Function to wrap a value in a comparison label.
 */
data class ScatterLayoutBuilder<G>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var yAxisLabel: String = "y",
    override var colorAxisLabel: String = "color",
    override var groupLabel: (G) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
    override val comparisonLabel: (Any) -> String = { "error" },
) : ScatterLayout<G>

/**
 * Renderer producing a scatter plot using the Lets-Plot backend.
 *
 * @param G The type of the group identifier.
 * @param X The type of the numeric x-coordinate.
 * @param Y The type of the numeric y-coordinate values.
 * @property style The layout configuration for the scatter plot.
 */
class ScatterPlotRenderer<G, X : Number, Y : Number>(override val style: ScatterLayout<G>) : PlotRenderer<G, X, Y> {

    /**
     * Creates a scatter plot from the given [data] and optional [comparisonData].
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A [Plot] object representing the scatter plot.
     */
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
                            range = colorMap.range,
                        )
                    }
                }
            }
        }
    }
}
