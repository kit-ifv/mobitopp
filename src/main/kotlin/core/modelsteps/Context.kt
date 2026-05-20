package core.modelsteps

import utils.report.CardStatus
import utils.report.ReportBuilder

/**
 * A Context holds all data required when executing mobiTopp.
 *
 * This interface defines the minimum state and services that all simulation contexts must provide,
 * including tracking the current model step, execution mode, scenario information, and reporting.
 *
 * Model steps are executed within a context, which collects logs, warnings, and errors
 * during both validation and execution phases.
 */
@Suppress("ComplexInterface")
interface Context {
    /** The name of the currently executing model step. */
    var currentStep: String

    /** The current execution mode (Validation or Execution). */
    val execMode: ExecutionMode

    /** The name of the simulation scenario. */
    val scenarioName: String

    /** The report builder used to collect logs, warnings, and errors. */
    val report: ReportBuilder

    /**
     * Logs an error message for the current step.
     *
     * @param message The error message to log.
     */
    fun logError(message: String) {
        report.addErrorLog(currentStep, message)
    }

    /**
     * Logs a warning message for the current step.
     *
     * @param message The warning message to log.
     */
    fun logWarning(message: String) {
        report.addWarningLog(currentStep, message)
    }

    /**
     * Logs a success message for the current step.
     *
     * @param message The success message to log.
     */
    fun logSuccess(message: String) {
        report.addSuccessLog(currentStep, message)
    }

    /**
     * Logs a normal informational message for the current step.
     *
     * @param message The message to log.
     */
    fun logNormal(message: String) {
        report.addNormalLog(currentStep, message)
    }

    /**
     * Logs an overview item with a specific status and message.
     *
     * @param status The status of the overview item.
     * @param message The message to log.
     */
    fun logOverview(status: CardStatus, message: String) {
        report.addOverviewItem(currentStep, status, message)
    }

}

/**
 * Initializes a [ReportBuilder] based on the current context's execution mode and scenario name.
 *
 * @receiver The context for which the report is initialized.
 * @return A new [ReportBuilder] instance.
 */
fun Context.initReport() = ReportBuilder(
    if (execMode.isValidate) {
        "Validation report for: $scenarioName"
    } else {
        "Execute report for: $scenarioName"
    }
)

typealias Check<C> = C.() -> Boolean
typealias Validation<C> = List<Check<C>>

/**
 * Simple interface with a clone function.
 */
interface Cloneable<T : Cloneable<T>> {
    /**
     * @return a shallow copy of this.
     */
    fun clone(): T
}

/**
 * Represents the current execution phase of the simulation.
 *
 * Simulations run in two phases:
 * 1. **Validation**: Quick error detection to catch "obvious errors" before time-consuming logic.
 * 2. **Execution**: The actual simulation run if no errors were found during validation.
 */
class ExecutionMode { // Do not make class open!
    private var validateMode: Boolean = false

    /** Returns true if the simulation is currently in validation mode. */
    val isValidate: Boolean
        get() = validateMode

    /** Returns true if the simulation is currently in execution mode. */
    val isExecute: Boolean
        get() = !validateMode

    /** Sets the mode to Validation. */
    fun setValidate() {
        validateMode = true
    }

    /** Sets the mode to Execution. */
    fun setExecute() {
        validateMode = false
    }
}
