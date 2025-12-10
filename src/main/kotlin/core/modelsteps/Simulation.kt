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
    private var contextFactory: () -> C = { contextFactory().clone() }

    /**
     * Steps scope defines execution (order) of model steps.
     *
     * @param lambda a function executed on the model
     *      execution object which defines / adds the steps to the [ModelExecution]
     */
    fun steps(lambda: C.() -> Unit) {
        println("Validate before run!")

        if (validate(lambda)) {
            println("\nExecute")

            val simulationContext = contextFactory()
            simulationContext.execMode.setExecute()
            logTime("    Execution") {
                simulationContext.lambda()
            }
        } else {
            error("validation failed")
        }
    }

    private fun validate(lambda: C.() -> Unit) = logTime("    Validation") {
        val context = contextFactory()
        context.execMode.setValidate(context.scenarioName)
        context.lambda()

        val warnings = context.execMode.warnings?.takeIf { it.subWarnings.isNotEmpty() }

        warnings?.also {
            it.printTree()
        }

        return@logTime warnings?.containsError()?.let { !it } ?: true
    }
}
