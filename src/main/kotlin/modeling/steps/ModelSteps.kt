package modeling.steps

import utils.Builder
import utils.Identifiable
import java.io.File

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
     * @return true, if the validation was successful
     */
    fun validate(): Boolean
}

/**
 * Add a [Resource] of [Builder]s to the given [RepositoryBuilder].
 *
 * @param B the generic type of builders
 * @param E the generic type of entities to be built
 * @param I the generic entity id type
 * @property name the name of the mode step //TODO could be derived
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

    override fun validate() = validatePrepareResourceStep(repository, resource, this)
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

    override fun validate() = validatePrepareCsvStep(repository, csv, this)
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

    override fun validate() = validateFilterStep(repository, this)
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

    override fun validate() = validateUpdateStep(repository, this)
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

    override fun validate() = validateUpdateStep(repository, this)
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
open class BuildStep<B, E, I> (
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.build()
        println("Built ${repository.name} repo: ${repository.size} elements")
    }

    override fun validate() = validateBuildStep(repository, this)
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

    override fun execute() = steps.forEach { it.execute() }

    override fun validate() = steps.all { step ->
        step.validate().also {
            println("Step ${step.name} is " + (if (it) "valid" else "invalid") + "!")
        }
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

        val validation = ModelExecution(context = contextFactory())
        validation.lambda()
        val isValid = validation.validate()

        if (isValid) {
            println("Execute")
            val simulation = ModelExecution(context = contextFactory())
            simulation.lambda()
            simulation.execute()
            return simulation.context
        } else {
            error("validation failed")
        }
    }
}

/**
 * A Context holds all data required when executing mobiTopp.
 * This is the minimum interface that all project contexts must implement.
 * Think carefully about what you put in here!
 */
interface Context {
    val scenarioName: String
    val demandFolder: File
}
