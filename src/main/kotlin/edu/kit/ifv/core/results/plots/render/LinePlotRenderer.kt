package edu.kit.ifv.core.results.plots.render
import edu.kit.ifv.core.results.plots.KIT_GREEN
import edu.kit.ifv.core.results.plots.PlotLayout
import edu.kit.ifv.core.results.plots.PlotRenderer
import edu.kit.ifv.core.results.plots.RGB
import edu.kit.ifv.core.results.plots.data.PlotData
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType

/**
 * Layout contract for a line plot.
 *
 * It provides the following layout parameters:
 *  - name: The name/title of the plot.
 *  - xAxisLabel: Label for the x-axis.
 *  - yAxisLabel: Label for the y-axis.
 *  - groupAxisLabel: Label for the group (color) axis.
 *  - groupLabel: Function to map a group identifier to its display label.
 *  - coloring: Function to map a group identifier to a color.
 *
 * @param G The type of the group identifier.
 */
interface LinePlotLayout<G> : PlotLayout {
    override val name: String
    val xAxisLabel: String
    val yAxisLabel: String
    val groupAxisLabel: String
    val groupLabel: (G) -> String
    val coloring: (G) -> RGB
}

/**
 * Mutable builder for [LinePlotLayout] options.
 *
 * @param G The type of the group identifier.
 * @property name The name/title of the plot.
 * @property xAxisLabel Label for the x-axis.
 * @property yAxisLabel Label for the y-axis.
 * @property groupAxisLabel Label for the group (color) axis.
 * @property groupLabel Function to map a group identifier to its display label.
 * @property coloring Function to map a group identifier to a color.
 */
data class LinePlotLayoutBuilder<G>(
    override var name: String = "plot",
    override var xAxisLabel: String = "x",
    override var yAxisLabel: String = "y",
    override var groupAxisLabel: String = "group",
    override var groupLabel: (G) -> String = { it.toString() },
    override var coloring: (G) -> RGB = { KIT_GREEN },
) : LinePlotLayout<G>

/**
 * Renderer producing a line plot with string x-coordinates using the Lets-Plot backend.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate values (must be a [Number]).
 * @param style The layout configuration for the line plot.
 */
class LinePlotRenderer<G, X, Y : Number>(style: LinePlotLayout<G>) : AbstractLinePlotRenderer<G, X, Y>(style) {
    /**
     * Provides a [DataFrameBuilder] configured for string x-coordinates.
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A configured [DataFrameBuilder].
     */
    override fun getDFBuilder(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): DataFrameBuilder<G, X, Y> =
        DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsString().yAsDouble()
}

/**
 * Base class for line plot renderers.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate value.
 * @property style The layout configuration for the line plot.
 */
abstract class AbstractLinePlotRenderer<G, X, Y>(override val style: LinePlotLayout<G>) : PlotRenderer<G, X, Y> {
    /**
     * Abstract method to provide a [DataFrameBuilder] for the specific x-coordinate type.
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A configured [DataFrameBuilder].
     */
    abstract fun getDFBuilder(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): DataFrameBuilder<G, X, Y>

    /**
     * Creates a line plot from the given [data] and optional [comparisonData].
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A [Plot] object representing the line plot.
     */
    override fun plot(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): Plot {
        val builder = getDFBuilder(data, comparisonData)
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

/**
 * Renderer producing a line plot with numeric x-coordinates using the Lets-Plot backend.
 *
 * @param G The type of the group identifier.
 * @param X The type of the numeric x-coordinate.
 * @param Y The type of the y-coordinate values (must be a [Number]).
 * @param style The layout configuration for the line plot.
 */
class NumericLinePlotRenderer<G, X : Number, Y : Number>(style: LinePlotLayout<G>) :
    AbstractLinePlotRenderer<G, X, Y>(style) {
    /**
     * Provides a [DataFrameBuilder] configured for numeric x-coordinates.
     *
     * @param data The main plot data.
     * @param comparisonData Optional comparison plot data.
     * @return A configured [DataFrameBuilder].
     */
    override fun getDFBuilder(data: PlotData<G, X, Y>, comparisonData: PlotData<G, X, Y>?): DataFrameBuilder<G, X, Y> =
        DataFrameBuilder(style.name, data, comparisonData).groupAsString {
            style.groupLabel(it)
        }.xAsDouble().yAsDouble()
}
