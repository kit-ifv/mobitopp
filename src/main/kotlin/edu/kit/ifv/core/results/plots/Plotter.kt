package edu.kit.ifv.core.results.plots
import edu.kit.ifv.core.results.plots.data.PlotData
import edu.kit.ifv.core.results.plots.render.overlayLogoTopRight
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

/**
 * Renders a Plot from prepared plot data using a particular layout/style.
 *
 * @param G The type of the group/category dimension.
 * @param X The type of the x-axis values.
 * @param V The type of the y-axis values used by the renderer.
 */
interface PlotRenderer<G, X, V> {
    /** Layout and labeling configuration for this renderer. */
    val style: PlotLayout

    /**
     * Produce a Plot from the provided data and optional comparison data.
     *
     * @param data Primary plot data.
     * @param comparisonData Optional reference/expected data to overlay or style differently.
     * @return A [Plot] object.
     */
    fun plot(data: PlotData<G, X, V>, comparisonData: PlotData<G, X, V>?): Plot
}

/**
 * Base interface for plot layout metadata and common labels.
 */
interface PlotLayout {
    /** Human-readable plot title and also used for file name generation. */
    val name: String

    /**
     * How to label comparison series in legends or labels.
     *
     * @return A function that takes a value and returns its comparison label string.
     */
    val comparisonLabel: (Any) -> String
        get() = { "*$it" }
}

/**
 * Plotter is a high-level wrapper that ties data with a concrete renderer and can write the plot to disk.
 *
 * @param G The type of the group/category dimension.
 * @param X The type of the x-axis values.
 * @param Y The type of the y-axis values.
 * @property data Primary plot data.
 * @property comparison Optional reference/expected data.
 * @property renderer The renderer used to produce the plot.
 */
data class Plotter<G, X, Y>(
    val data: PlotData<G, X, Y>,
    val comparison: PlotData<G, X, Y>?,
    val renderer: PlotRenderer<G, X, Y>,
) {

    /** Plot name taken from the renderer's layout. */
    val name: String
        get() = renderer.style.name

    /**
     * Render and save the plot as a PNG into [resultDir]. Also overlays the mobiTopp logo.
     *
     * @param resultDir Destination directory; defaults to "results" relative to the project.
     */
    fun plot(resultDir: Path = Path("results")) {
        val plot = renderer.plot(data, comparison)

        val filename = "${renderer.style.name.replace(" ", "_")}.png"
        plot.save(filename, path = resultDir.pathString)

        val path = resultDir.resolve(filename)
        overlayLogoTopRight(
            basePng = path,
            outPng = path,
        )

        // TODO overlay time, seed, sim name
    }
}
