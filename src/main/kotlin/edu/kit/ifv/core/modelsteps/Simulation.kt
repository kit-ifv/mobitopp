package edu.kit.ifv.core.modelsteps
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.units.logTime

const val NO_ERROR_MESSAGE = "No error message"

/**
 * A configuration for a [Simulation], containing user input parameters.
 */
interface Config {
    /** The seed for random number generators to ensure reproducibility. */
    val seed: Long

    /** The error handling strategy to use during simulation. */
    val errorHandling: ErrorHandling // maybe move to source file config?
}

/**
 * A [Simulation] represents the execution of a sequence of model steps.
 *
 * It takes a [config] containing user input parameters such as file paths, model parameters,
 * and boolean flags.
 *
 * The execution process follows these steps:
 * 1. Create a new context for the **validation** run.
 * 2. Execute all specified model steps in validation mode for quick error detection.
 * 3. Check for errors and print the validation report collected in the context.
 * 4. If no errors are detected, create a new context for the **execution** run.
 * 5. Run all model steps in execution mode.
 * 6. Finally, print the execution report to the console.
 *
 * @param C The generic context type, which must be a [Context] and [Cloneable].
 * @param CFG The generic configuration type, which must implement [Config].
 * @param config The simulation configuration.
 * @param contextFactory A factory function to create initial context instances.
 */
class Simulation<C, CFG>(val config: CFG, contextFactory: () -> C) where C : Context, C : Cloneable<C>, CFG : Config {

    /**
     * A context factory that guarantees shallow independent instances of `C` as long as `clone` works correctly.
     */
    private val contextFactory: () -> C = { contextFactory().clone() }

    /**
     * Defines the execution order of model steps and initiates the simulation process.
     *
     * This method first calls [validate] to perform the validation phase.
     * If validation passes, it proceeds to the execution phase.
     *
     * @receiver The simulation instance.
     * @param lambda A function that defines the sequence of model steps to run.
     */
    fun steps(lambda: context(CFG) C.() -> Unit) {
        validate(lambda)

        val simulationContext = contextFactory()
        simulationContext.execMode.setExecute()

        val logTitle = "Simulation: ${simulationContext.scenarioName}"

        @Suppress("TooGenericExceptionCaught")
        try {
            logTime("    Execution") {
                println("\nExecute")
                context(config) {
                    simulationContext.lambda()
                }
            }
        } catch (e: Exception) {
            simulationContext.report.addErrorLog(
                logTitle,
                "Error during execution of ${simulationContext.scenarioName}: " + (e.message ?: NO_ERROR_MESSAGE),
            )
            simulationContext.report.printToConsole()
            throw e
        }

        simulationContext.report.printToConsole()
    }

    private fun validate(lambda: context(CFG) C.() -> Unit) = logTime("    Validation") {
        println("Validate before run!")

        val context = contextFactory()
        context.execMode.setValidate()

        val report = context.report
        val logTitle = "Simulation: ${context.scenarioName}"

        @Suppress("TooGenericExceptionCaught")
        try {
            context(config) {
                context.lambda()
            }
        } catch (e: Exception) {
            report.addErrorLog(
                logTitle,
                "Error during validation not caught by validation checks: " + (e.message ?: NO_ERROR_MESSAGE),
            )
            report.printToConsole()
            throw e
        }

        if (report.hasErrors()) {
            report.addErrorLog(logTitle, "Discovered errors during validation!")
        } else if (report.hasWarnings()) {
            report.addWarningLog(logTitle, "validation produced warnings!")
        }

        report.printToConsole()

        if (report.hasErrors()) {
            error("validation failed")
        }
    }
}
