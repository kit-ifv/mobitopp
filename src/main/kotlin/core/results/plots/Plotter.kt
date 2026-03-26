package core.results.plots

import core.results.plots.data.PlotData
import core.results.plots.render.overlayLogoTopRight
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

/**
 * Renders a Plot from prepared plot data using a particular layout/style.
 *
 * @param G type of the group/category dimension
 * @param X type of the x-axis values
 * @param V type of the y-axis values used by the renderer
 */
interface PlotRenderer<G, X, V> {
    /** Layout and labeling configuration for this renderer. */
    val style: PlotLayout

    /**
     * Produce a Plot from the provided data and optional comparison data.
     * @param data primary plot data
     * @param comparisonData optional reference/expected data to overlay or style differently
     */
    fun plot(
        data: PlotData<G, X, V>,
        comparisonData: PlotData<G, X, V>?
    ): Plot
}

/**
 * Base interface for plot layout metadata and common labels.
 */
interface PlotLayout {
    /** Human-readable plot title and also used for file name generation. */
    val name: String

    /** How to label comparison series in legends or labels. */
    val comparisonLabel: (Any) -> String
        get() = { "*$it" }
}

/**
 * Plotter is a high-level wrapper that ties data with a concrete renderer and can write the plot to disk.
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
     * Render and save the plot as a PNG into resultDir. Also overlays the mobiTopp logo.
     * @param resultDir destination directory; defaults to "results" relative to the project.
     */
    fun plot(resultDir: Path = Path("results")) {
        val plot = renderer.plot(data, comparison)

        val filename = "${renderer.style.name.replace(" ", "_")}.png"
        plot.save(filename, path = resultDir.pathString)

        val path = resultDir.resolve(filename)
        System.err.println("Writing $path to ${path.toAbsolutePath()}")
        System.err.println("exists after save: ${path.toFile().exists()}")
        System.err.println("size after save: ${path.toFile().length()}")
        overlayLogoTopRight(
            basePng = path,
            outPng = path,
        )

        // TODO overlay time, seed, sim name
    }
}
