package edu.kit.ifv.core.results.plots.render
import edu.kit.ifv.core.results.plots.KIT_GREEN
import edu.kit.ifv.core.results.plots.PlotLayout
import edu.kit.ifv.core.results.plots.PlotRenderer
import edu.kit.ifv.core.results.plots.RGB
import edu.kit.ifv.core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.Position
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.feature.position
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars

/**
 * Layout contract for a stacked histogram.
 *
 * It provides the following layout parameters:
 *  - name: The name/title of the plot.
 *  - stackAxisLabel: Label for the stack (color) axis.
 *  - xAxisLabel: Label for the x-axis.
 *  - groupLabel: Function to map a group identifier to its display label.
 *  - xLabel: Function to map an x-coordinate to its display label.
 *  - coloring: Function to map a group identifier to a color.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 */
interface HistogramLayout<G, X> : PlotLayout {
    override val name: String
    val stackAxisLabel: String
    val xAxisLabel: String
    val groupLabel: (G) -> String
    val xLabel: (X) -> String
    val coloring: (G) -> RGB
}

/**
 * Mutable builder used to configure a [HistogramLayout].
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @property name The name/title of the plot.
 * @property stackAxisLabel Label for the stack (color) axis.
 * @property xAxisLabel Label for the x-axis.
 * @property groupLabel Function to map a group identifier to its display label.
 * @property xLabel Function to map an x-coordinate to its display label.
 * @property coloring Function to map a group identifier to a color.
 */
data class HistogramLayoutBuilder<G, X>(
    override var name: String = "plot",
    override var stackAxisLabel: String = "stack",
    override var xAxisLabel: String = "x",
    override var groupLabel: (G) -> String = { it.toString() },
    override var xLabel: (X) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : HistogramLayout<G, X>

/**
 * Renderer producing a stacked histogram using the Lets-Plot backend.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate values (must be a [Number]).
 * @property style The layout configuration for the histogram.
 */
class HistogramRenderer<G, X, Y : Number>(override val style: HistogramLayout<G, X>) : PlotRenderer<G, X, Y> {

    /**
     * Creates a stacked histogram from the given [data] and optional [comparisonData].
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A [Plot] object representing the histogram.
     */
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
            style.comparisonLabel,
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
