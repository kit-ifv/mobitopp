package core.modelsteps

import utils.units.logTime

/**
 * Simulation allows to specify a simulation configuration in readable kotlin dsl.
 * Users can define a mobitopp object and model steps.
 * When executed, all specified [ModelStep]s are validated first.
 *
 * @param C the generic mobitopp type. A type that is a `Context` and a `Cloneable<C>`.
 * @property contextFactory a factory to create new mobitopp objects
 */
class Simulation<C>(contextFactory: () -> C) where C : Context, C : Cloneable<C> {

    /**
     * A context factory that guarantees shallow independent instances of `C` as long as `clone` works correctly.
     */
    private val contextFactory: () -> C = { contextFactory().clone() }

    /**
     * Steps scope defines execution (order) of model steps.
     * @param lambda a function that runs model steps on the context
     */
    fun steps(lambda: C.() -> Unit) {

        validate(lambda)

        val simulationContext = contextFactory()
        simulationContext.execMode.setExecute()

        logTime("    Execution") {
            println("\nExecute")
            simulationContext.lambda()
        }

        simulationContext.report.printToConsole()
    }

    private fun validate(lambda: C.() -> Unit) = logTime("    Validation") {
        println("Validate before run!")

        val context = contextFactory()
        context.execMode.setValidate()
        context.lambda()

        val report = context.report
        if (report.hasErrors()) {
            report.addErrorLog(
                "validation errors",
                "validation of ${context.scenarioName} discovered errors!"
            )

        } else if (report.hasWarnings()) {
            report.addWarningLog(
                "validation warnings",
                "validation of ${context.scenarioName} warnings!"
            )
        }

        report.printToConsole()

        if (report.hasErrors()) {
            error("validation failed")
        }
    }

}
