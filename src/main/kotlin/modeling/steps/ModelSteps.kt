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
 * Add csv step
 *
 * @param B
 * @param E
 * @param I
 * @param name
 * @param repository
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
 * Filter step
 *
 * @param B
 * @param E
 * @param I
 * @constructor Create empty Filter step
 * @property name
 * @property repository
 * @property predicate
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
 * Update step
 *
 * @param B
 * @param E
 * @param I
 * @constructor Create empty Update step
 * @property name
 * @property repository
 * @property transformation
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
 * Update all step
 *
 * @param B
 * @param E
 * @param I
 * @constructor Create empty Update all step
 * @property name
 * @property repository
 * @property transformation
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
 * Build step
 *
 * @param B
 * @param E
 * @param I
 * @constructor Create empty Build step
 * @property name
 * @property repository
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
 * Multi step
 *
 * @param steps
 * @constructor
 * @property name
 */
open class MultiStep(
    override val name: String,
    vararg steps: ModelStep,
) : ModelStep {
    private val steps = steps.toMutableList()

    /**
     * Add step
     *
     * @param step
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
 * Model execution
 *
 * @param C
 * @constructor Create empty Model execution
 * @property context
 */
class ModelExecution<C>(
    val context: C,
) : MultiStep(context.scenarioName) where C : Context

/**
 * Run
 *
 * @param C
 * @constructor Create empty Run
 * @property contextFactory
 */
class Run<C>(private val contextFactory: () -> C) where C : Context {

    /**
     * Steps
     *
     * @param lambda
     * @return
     * @receiver
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
 * Context
 *
 * @constructor Create empty Context
 */
interface Context {
    val scenarioName: String
    val demandFolder: File

    /** Reset */
    fun reset()
}
