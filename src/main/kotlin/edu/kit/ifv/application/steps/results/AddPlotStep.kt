package edu.kit.ifv.application.steps.results
import edu.kit.ifv.application.steps.ResultsConfig
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.core.results.plots.Plotter

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
