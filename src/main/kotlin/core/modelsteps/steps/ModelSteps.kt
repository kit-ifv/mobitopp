package core.modelsteps.steps

import core.modelsteps.Context
import core.modelsteps.NO_ERROR_MESSAGE
import core.modelsteps.Validation
import core.modelsteps.resources.Repository
import core.modelsteps.validation.validateCondition
import utils.ConsoleCaptor
import utils.report.CardStatus
import utils.units.logTime

/**
 * Executes a single model step.
 *
 * Model steps are the basic building blocks of simulations and are executed sequentially.
 * Each step has a [name] for logging purposes.
 *
 * In validation mode, this function runs the provided [validation] checks to detect "obvious errors"
 * early, before any time-consuming simulation logic is executed.
 * In execution mode, it runs the [execution] block.
 *
 * @receiver The context type in which the step is executed.
 * @param C The context type in which the step is executed.
 * @param name The descriptive name of this step, used for logging and reporting.
 * @param validation A list of validation checks to be performed in validation mode.
 * @param execution The actual logic to be executed in execution mode.
 */
fun <C : Context> C.modelStep(name: String, validation: Validation<C> = emptyList(), execution: C.() -> Unit) {
    currentStep = name

    detectReportDiffAndLogOverview {
        println("\n$action $name")

        if (execMode.isValidate) {
            val captor = ConsoleCaptor()

            @Suppress("TooGenericExceptionCaught")
            val isValid = try {
                validation.takeIf { it.isNotEmpty() }?.all { it() } ?: true
            } catch (e: Exception) {
                logError("Exception: " + (e.message ?: NO_ERROR_MESSAGE))
                false
            }

            // No console output during validate
            captor.getText()
            isValid
        } else {
            logTime("    $name") {
                this.execution()
            }
            true
        }
    }
}

private val Context.mode: String get() = if (execMode.isValidate) "validation" else "execution"
private val Context.action: String get() = if (execMode.isValidate) "Validate" else "Execute"
private fun Context.message(result: String) = "$mode $result."
private fun Context.detectReportDiffAndLogOverview(block: () -> Boolean) {
    val diff = report.detectReportChanges {
        block()
    }

    val (rStatus, rMessage) = if (!diff.result || diff.newErrors) {
        CardStatus.FAILURE to message("failed")
    } else if (diff.newWarnings) {
        CardStatus.WARNING to message("produced warnings")
    } else {
        CardStatus.SUCCESS to message("was successful")
    }

    logOverview(rStatus, rMessage)

    when (rStatus) {
        CardStatus.FAILURE -> logError(rMessage)
        CardStatus.WARNING -> logWarning(rMessage)
        CardStatus.SUCCESS -> logSuccess(rMessage)
    }
}

/**
 * A wrapper for [modelStep] that adds a check for [dependentRepositories].
 *
 * This step ensures that all repositories it depends on are already sealed.
 * If a repository is not sealed, a warning is reported during validation.
 *
 * @receiver The context type in which the step is executed.
 * @param C The context type in which the step is executed.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks to be performed.
 * @param execution The logic to be executed.
 */
fun <C : Context> C.repositoryDependentStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>>,
    validation: Validation<C> = emptyList(),
    execution: C.() -> Unit
) = modelStep(
    name,
    validation + { checkDependentRepositories(name, dependentRepositories) },
    execution
)

private fun <C : Context> C.checkDependentRepositories(
    name: String,
    dependentRepositories: Set<Repository<*, *>>
): Boolean {
    dependentRepositories.forEach {
        validateCondition(
            message = {
                "Step $name depends on unsealed repository: ${it.name}. Make sure this is desired behavior!"
            },
            isError = false
        ) { it.sealed }
    }
    return true
}
