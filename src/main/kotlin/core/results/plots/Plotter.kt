package core.results.plots

import core.results.plots.data.PlotData
import core.results.plots.render.overlayLogoTopRight
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

interface PlotRenderer<G, X, V> {
    val style: PlotLayout

    fun plot(
        data: PlotData<G, X, V>,
        comparisonData: PlotData<G, X, V>?
    ): Plot
}

interface PlotLayout {
    val name: String
    val comparisonLabel: (Any) -> String
}

data class Plotter<G, X, Y>(
    val data: PlotData<G, X, Y>,
    val comparison: PlotData<G, X, Y>?,
    val renderer: PlotRenderer<G, X, Y>,
) {

    val name: String
        get() = renderer.style.name

    fun plot(resultDir: Path = Path("results")) {
        val plot = renderer.plot(data, comparison)

        val dir = resultDir.resolve("plots")
        val filename = "${renderer.style.name.replace(" ", "_")}.png"
        plot.save(filename, path = dir.pathString)

        val path = dir.resolve(filename)
        overlayLogoTopRight(
            basePng = path,
            outPng = path,
        )
    }
}
