package modeling.steps

import modeling.validation.Warning
import modeling.validation.validateScope
import utils.Builder
import utils.ConsoleCaptor
import utils.Identifiable
import utils.collections.muteProgressBars
import utils.collections.unmuteProgressBars
import utils.units.logTime

/**
 * A ModelStep represents an operation performed during the model execution.
 * It can e.g. modify builders in [RepositoryBuilder]s etc.
 * Each ModeStep must be named and provides methods to validate and execute the step.
 */
interface ModelStep {
    val name: String

    /** Execute this [ModelStep]. */
    fun execute()

    /**
     * Validate this [ModelStep]
     *
     * @return a warning, if the validation discovered warnings or errors
     */
    fun validate(): Warning?

    /**
     * Check if this [ModelStep] is valid.
     * If [validate] returns no warning or if the returned warning contains no errors, the step is considered valid.
     */
    val isValid: Boolean
        get() = validate()?.containsError()?.let { !it } ?: true
}

class CustomStep(
    override val name: String,
    val validation: () -> Warning? = { null },
    val exec: () -> Unit,
) : ModelStep {
    override fun execute() = exec()

    override fun validate(): Warning? = validation()
}

/**
 * Add a [Resource] of [Builder]s to the given [RepositoryBuilder].
 *
 * @param B the generic type of builders
 * @param E the generic type of entities to be built
 * @param I the generic entity id type
 * @property name the name of the mode step
 * @property resource the [Resource] of builders to be added
 * @property repository the [RepositoryBuilder] into which the [Builder]s are added
 */
open class AddResourceStep<B, E, I>(
    override val name: String,
    protected val resource: Resource<B>,
    protected val repository: RepositoryBuilder<B, E, I>,
) : ModelStep where E : Identifiable<I>, B : Builder<E> {

    override fun execute() {
        repository.addBuilders(resource)
    }

    override fun validate() = validateScope(
        "Validate $name: adding resource ${resource.name} to repo ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.UNINITIALIZED, this@AddResourceStep)
        repairPreparingState(repository, resource)
    }
}

/**
 * A [ModelStep] adding [Builder]s created from csv data.
 *
 * @param B the generic [Builder] type
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param name the name of this add csv step
 * @param repository the repository to which the builders should be added to
 * @constructor
 * @property csv
 */
open class AddCsvStep<B, E, I>(
    name: String,
    protected val csv: CsvResource<B>,
    repository: RepositoryBuilder<B, E, I>,
) : AddResourceStep<B, E, I>(
    name = name,
    resource = csv,
    repository = repository
) where E : Identifiable<I>, B : Builder<E> {

    override fun validate() = validateScope(
        "Validate $name: adding csv ${csv.name} to repo ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.UNINITIALIZED, this@AddCsvStep)
        ValidateCsvMetadata(this@AddCsvStep, csv).validate()?.also {
            this.addChild(it)
        }

        repairPreparingState(repository, resource)
    }
}

/**
 * A FilterStep is a [ModelStep] that filters the builders of a given [RepositoryBuilder]
 * using a given predicate.
 * This removes [Builder]s from the repository if applying the predicates evaluates to false.
 *
 * @param B the generic [Builder] type
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property name the name of the filter step
 * @property repository the repository to be filtered
 * @property predicate the predicate used to filter the repository
 */
open class FilterStep<B, E, I>(
    override val name: String,
    val repository: RepositoryBuilder<B, E, I>,
    protected val predicate: (B) -> Boolean,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.filter(name, predicate)
    }

    override fun validate() = validateScope(
        "Validate $name: filtering ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.PREPARING, this@FilterStep)
        repairPreparingState(repository, dummyResource(this@FilterStep))
    }
}

/**
 * An UpdateStep is a [ModelStep] used to modify / update [Builder]s in a given repository
 * by applying a transformation (mapping) to each [Builder] in the repository.
 * The transformation might evaluate to null, which removes the [Builder] from the repository.
 *
 * @param B the generic [Builder] type
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property name the name of the update step
 * @property repository the repository in which [Builder]s are updated
 * @property transformation a mapping to update a single [Builder]
 */
open class UpdateStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
    protected val transformation: (B) -> B?,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.update(name, transformation)
    }

    override fun validate() = validateScope(
        "Validate $name: updating ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.PREPARING, this@UpdateStep)
        repairPreparingState(repository, dummyResource(this@UpdateStep))
    }
}

/**
 * UpdateAllStep is a [ModelStep] that replaces all [Builder]s of a repository by new / derived builders.
 *
 *
 * @param B the generic [Builder] type
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property name the name of the update all step
 * @property repository the repository in which all [Builder]s should be updated
 * @property transformation a mapping to be applied to all [Builder]s of the repository
 */
open class UpdateAllStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
    protected val transformation: (Sequence<B>) -> Sequence<B>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.updateAll(name, transformation)
    }

    override fun validate() = validateScope(
        "Validate $name: updating all elements of ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.PREPARING, this@UpdateAllStep)
        repairPreparingState(repository, dummyResource(this@UpdateAllStep))
    }
}

/**
 * A BuildStep is a [ModelStep] that builds all [Builder]s in a given repository.
 *
 * @param B the generic [Builder] type
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property name th name of the build step
 * @property repository the repository to be built
 */
open class BuildStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.build()
        println("Built ${repository.name} repo: ${repository.size} elements")
    }

    override fun validate() = validateScope(
        "Validate $name: building ${repository.name} produced warnings:"
    ) {
        subValidateState(repository, RepositoryState.PREPARING, this@BuildStep)
        repairFinishedState(repository, this@BuildStep)
    }
}

/**
 * A MultiStep is a [ModelStep] that executes multiple steps sequentially.
 *
 * @param steps the steps to be executed (in order of execution)
 * @property name the name of the multi step
 */
open class MultiStep(
    override val name: String,
    vararg steps: ModelStep,
) : ModelStep {
    private val steps = steps.toMutableList()

    /**
     * Add the given step as new last step of the execution order.
     *
     * @param step the step to added to this [MultiStep]
     */
    fun addStep(step: ModelStep) {
        steps.add(step)
    }

    override fun execute() = steps.forEach {
        println("\nRun ${it.name}")
        logTime("    ${it.name}") {
            it.execute()
        }
    }

    override fun validate() = validateScope(
        "Validate multiple ModelSteps:"
    ) {
        val captor = ConsoleCaptor()

        steps.forEach {
            it.validate()?.also { warning ->
                if (warning.containsError()) {
                    warning.addChild("${it::class.simpleName} '${it.name}' is invalid!", true)
                }
                this.addChild(warning)
            }
        }

        captor.getText()
    }?.also {
        it.printTree()
    }
}

/**
 * ModelExecution is a [MultiStep] holding a context object.
 * This can be used e.g. to define the steps of a simulation.
 *
 * @param C the generic context type
 * @property context the context object for
 */
class ModelExecution<C>(
    val context: C,
) : MultiStep(context.scenarioName) where C : Context

/**
 * Run allows to specify a simulation configuration in readable kotlin dsl.
 * Users can define a context object and model steps.
 * When executed, all specified [ModelStep]s are validated first.
 *
 * @param C the generic context type
 * @property contextFactory a factory to create new context objects
 */
class Run<C>(private val contextFactory: () -> C) where C : Context {

    /**
     * Steps
     *
     * @param lambda a function executed on the model
     *      execution object which defines / adds the steps to the [ModelExecution]
     * @return the context
     */
    fun steps(lambda: ModelExecution<C>.() -> Unit): C {
        println("Validate before run!")

        if (validate(lambda)) {
            println("\nExecute")

            val simulation = ModelExecution(context = contextFactory())
            logTime("    Execution") {
                simulation.lambda()
                simulation.execute()
            }

            return simulation.context
        } else {
            error("validation failed")
        }
    }

    private fun validate(lambda: ModelExecution<C>.() -> Unit) = logTime("    Validation") {
        muteProgressBars()
        val validation = ModelExecution(context = contextFactory())
        validation.lambda()
        validation.validate().also {
            unmuteProgressBars()
        }
    }?.containsError()?.let { !it } ?: true
}
