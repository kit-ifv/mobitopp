package application.steps.results

import core.modelsteps.Context
import core.modelsteps.ModelStep
import core.modelsteps.Warning
import core.modelsteps.validateScope
import core.results.plots.Plotter
import java.nio.file.Path

fun <C : Context> C.addPlot(scope: () -> Plotter<*, *, *>) = runStep {
    AddPlotStep(scope(), resultDir)
}

data class AddPlotStep(
    private val plotter: Plotter<*, *, *>,
    val resultDir: Path
) : ModelStep {

    override val name = "Add Plot ${plotter.name}"

    override fun execute() {
        plotter.plot()
    }

    override fun verifyInput(): Warning? = validateScope { }

    override fun mockBehavior(): Warning? = validateScope { }
}
