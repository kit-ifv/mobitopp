package application.steps.results

import application.steps.ResultsConfig
import core.modelsteps.Context
import core.modelsteps.steps.modelStep
import core.results.plots.Plotter

context(config: ResultsConfig)
fun <C : Context> C.addPlot(vararg subDirs: String = arrayOf("plots"), scope: () -> Plotter<*, *, *>) {
    val plotter = scope()

    modelStep("Add plot ${plotter.name}") {
        var dir = config.resultDir
        for (subDir in subDirs) {
            dir = dir.resolve(subDir)
        }

        plotter.plot(dir)
    }
}
