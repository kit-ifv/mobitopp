package core.modelsteps.steps

import core.modelsteps.Context
import core.modelsteps.Validation
import core.modelsteps.resources.Repository
import core.modelsteps.validation.validateCondition
import utils.ConsoleCaptor
import utils.report.CardStatus
import utils.units.logTime


fun <C: Context> C.modelStep(name: String, validation: Validation<C> = emptyList(), execution: C.() -> Unit) {
    val mode = if (execMode.isValidate) {
        "validation"
    } else {
        "execution"
    }
    val action = if (execMode.isValidate) "Validate" else "Execute"
    fun title(type: String) = "$type in $name"
    fun message(result: String) = "$mode of model step $name $result."

    val diff = report.detectReportChanges {
        println("\n$action $name")
        try {

            if (execMode.isValidate) {
                val captor = ConsoleCaptor()
                val isValid = validation.takeIf { it.isNotEmpty() }?.all { it() } ?: true
                // No console output during validate
                captor.getText()
                isValid

            } else {
                logTime("    $name") {
                    this.execution()
                }
                true
            }


        } catch (ex: Exception) {
            report.addErrorLog(title("Exception"), "exception during validation of $name: \n ${ex.message}")
            false
        }
    }

    val (prefix, rMessage) = if (!diff.result || diff.newErrors) {
        title("Error") to CardStatus.FAILURE to message("failed")
    } else if (diff.newWarnings) {
        title("Warning") to CardStatus.WARNING to message("produced warnings")
    } else {
        "$name is valid" to CardStatus.SUCCESS to message("was successful")
    }

    val (rTitle, rStatus) = prefix

    report.addOverviewItem(name, rStatus, rMessage)

    when (rStatus) {
        CardStatus.FAILURE -> report.addErrorLog(rTitle, rMessage)
        CardStatus.WARNING -> report.addWarningLog(rTitle, rMessage)
        CardStatus.SUCCESS -> report.addSuccessLog(rTitle, rMessage)
    }
}


/**
 * Add additional check to validation:
 * report a warning for each sealed repo in [dependentRepositories].
 *
 */
fun <C: Context> C.repositoryDependentStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>>,
    validation: Validation<C> = emptyList(),
    execution: C.() -> Unit
) = modelStep(
    name,
    validation + { checkDependentRepositories(name, dependentRepositories) },
    execution
)


private fun <C: Context> C.checkDependentRepositories(
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


