package core.modelsteps

import utils.ConsoleCaptor
import utils.Identifiable
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.units.logTime
import java.nio.file.Path

/**
 * A ModelStep represents an operation performed during the model execution.
 * It can e.g. modify builders in [RepositoryBuilder]s etc.
 * Each ModeStep must be named and provides methods to validate and execute the step.
 */
interface ModelStep {
    val name: String

    /**
     * Run the model step in the given [ExecutionMode].
     *
     * In validation mode: run [validate] and add potential warning as child to the [ExecutionMode.warnings].
     * In execution mode: run [execute] and log the execution time.
     *
     * @param execMode the desired execution mode for this [ModelStep] run.
     */
    fun run(execMode: ExecutionMode) {
        if (execMode.isValidate) {
            val captor = ConsoleCaptor()

            validate()?.also { warning ->
                if (warning.containsError()) {
                    warning.addChild("${this::class.simpleName} '$name' is invalid!", true)
                }
                execMode.warnings?.addChild(warning)
            }

            // No console output during validate
            captor.getText()
        } else {
            println("\nRun $name")
            logTime("    $name") {
                execute()
            }
        }
    }

    /** Execute this [ModelStep]. */
    fun execute()

    /**
     * Validate this [ModelStep].
     *
     * @return a warning, if the validation discovered warnings or errors
     */
    fun validate(validationPrefix: Warning.() -> Unit = { }): Warning? = validateScope(
        message = "Validate step $name produced warnings:"
    ) {
        validationPrefix()

        subValidation {
            verifyInput()
        }

        subValidation {
            mockBehavior()
        }
    }

    fun verifyInput(): Warning?
    fun mockBehavior(): Warning?

    /**
     * Check if this [ModelStep] is valid.
     * If [validate] returns no warning or if the returned warning contains no errors, the step is considered valid.
     */
    val isValid: Boolean
        get() = validate()?.containsError()?.let { !it } ?: true
}

class ExecutionMode { // Do not make class open!
    private var validateMode: Warning? = null

    val warnings: Warning?
        get() = validateMode

    val isValidate: Boolean
        get() = validateMode != null

    val isExecute: Boolean
        get() = validateMode == null

    fun setValidate(simulationName: String) {
        validateMode = Warning("Validate multiple ModelSteps ($simulationName):", false)
    }

    fun setExecute() {
        validateMode = null
    }
}

interface RepositoryDependentStep : ModelStep {

    val repository: Repository<*, *>?
    val dependentRepositories: Set<Repository<*, *>>

    override fun validate(validationPrefix: Warning.() -> Unit): Warning? = super.validate {
        validationPrefix()
        checkAllDependent()
    }

    private fun Warning.checkAllDependent() {
        dependentRepositories.forEach {
            if (it != repository) {
                warnIfDependentNotSealed(it)
            }
        }
    }

    fun Warning.warnIfDependentNotSealed(it: Repository<*, *>) {
        validateCondition(
            message = "Step ${this@RepositoryDependentStep.name} depends on unsealed repository: ${it.name}. " +
                "Make sure this is desired behavior!",
            isError = false
        ) {
            it.sealed
        }
    }
}

interface MutatingStep<E, I> : RepositoryDependentStep where E : Identifiable<I> {

    override val repository: MutableRepository<E, I>

    override fun validate(validationPrefix: Warning.() -> Unit): Warning? = super.validate {
        validationPrefix()
        subValidation {
            validateNotSealed(repository, this@MutatingStep)
        }
    }
}

interface SameValidationBehavior : ModelStep {
    override fun mockBehavior(): Warning? = validateScope("Try ${this::class.simpleName}: $name") {
        execute()
    }
}

/**
 * Add a [Resource] of elements to the given [MutableRepository].
 *
 * @param E the generic type of entities to be added
 * @param I the generic entity id type
 */
abstract class AddResourceStep<E, I> : MutatingStep<E, I> where E : Identifiable<I> {

    abstract val resource: Resource<E>

    override fun execute() {
        repository.addElements("$name (from ${resource.name} [${resource.source}])", resource.elements)
    }

    override fun mockBehavior(): Warning? = validateScope("Mock elements of ${resource.name}") {
        repository.addElements("$name (mocked resource ${resource.name})", mockElementsForValidation())
    }

    abstract fun mockElementsForValidation(): List<E>
}

/**
 * A [ModelStep] adding elements created from csv data.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 */
abstract class AddCsvStep<E, I> : AddResourceStep<E, I>() where E : Identifiable<I> {

    abstract override val resource: CsvResource<E>

    override fun verifyInput(): Warning? = ValidateCsvMetadata(this@AddCsvStep, resource).validate()
}

@Suppress("LongParameterList")
class LoadCsvStep<E, I>(
    path: Path,
    override val name: String = "load ${path.fileName}",
    parser: CsvParser<E>,
    delimiter: String = SEMICOLON,
    override val repository: MutableRepository<E, I>,
    override val dependentRepositories: Set<Repository<*, *>>,
    private val validationMock: List<E>,
) : AddCsvStep<E, I>() where E : Identifiable<I> {
    override val resource: CsvResource<E> by lazy { CsvResource(path, parser, delimiter) }

    override fun mockElementsForValidation(): List<E> = validationMock
}

/**
 * A FilterStep is a [ModelStep] that filters the elements of a given [MutableRepository]
 * using a given predicate.
 * This removes elements from the repository if applying the predicates evaluates to false.
 *
 * @param E the generic type of entities to be filtered
 * @param I the generic id type of entities
 */
abstract class FilterStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.filterElements(name, this::check)
    }
    abstract fun check(element: E): Boolean

}

/**
 * A FilterIdsStep is a [ModelStep] that filters the elements of a given [MutableRepository] by id
 * using a given predicate.
 * This removes elements from the repository if applying the predicates evaluates to false.
 *
 * @param E the generic type of entities
 * @param I the generic id type of entities to be filtered
 */
abstract class FilterIdsStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.filterIds(name, this::check)
    }
    abstract fun check(id: I): Boolean
}

/**
 * An [UpdateEachStep] is a [ModelStep] used to modify / update the internal state of elements in a given repository
 * by applying an action to each element in the repository. This action may alter state variables of the element.
 *
 *  Unlike [UpdateAllStep] where all current elements in the repository
 *  are passed as a collection to the [UpdateAllStep.updateAll] function,
 *  [UpdateEachStep] applies the [update] function to each element individually.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which each element should be updated
 */
abstract class UpdateEachStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.updateEach(name, this::update)
    }
    abstract fun update(element: E)
}

/**
 * An [UpdateAllStep] is a [ModelStep] used to modify / update the internal state of all elements in a given repository
 * by processing all elements in the repository at once. This action may alter state variables of all elements.
 * This can be used if the stat update of the elements are not isolated but interdependent.
 *
 *  Unlike [UpdateEachStep] where the action is applied to each element individually,
 *  [UpdateAllStep] passes all current elements in the repository as a collection to the [updateAll] function.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which all elements should be updated
 */
abstract class UpdateAllStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.updateAll(name, this::updateAll)
    }
    abstract fun updateAll(element: Collection<E>)
}

/**
 * An [TransformEachStep] is a [ModelStep] used to modify / update elements in a given repository
 * by applying a transformation (mapping) to each element in the repository.
 * The transformation might evaluate to null, which removes the element from the repository.
 *
 * Unlike [TransformAllStep] where the new elements are computed from data of all current elements in the repository,
 * [TransformEachStep] maps each current element to a new element or null.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which each element should be transformed
 */
abstract class TransformEachStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.transformEach(name, this::transform)
    }
    abstract fun transform(element: E): E?
}

/**
 * [TransformAllStep] is a [ModelStep] that replaces all elements of a repository by new / derived elements.
 *
 * Unlike [TransformEachStep] where each current element is mapped to a new element or null,
 * [TransformAllStep] computes the new elements from data of all current elements in the repository.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which all elements should be transformed
 */
abstract class TransformAllStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.transformAll(name, this::transformAll)
    }
    abstract fun transformAll(elements: Collection<E>): Collection<E>
}

/**
 * [ForEachStep] is a [ModelStep] that applies a (non mutating) action to each element of the [repository].
 *
 * Unlike [ForAllStep] where all current elements of the [repository]
 * are passed as a collection to the [ForAllStep.processAll] action,
 * [ForEachStep] applies the [process] action to each current element individually.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which each element should be processed
 */
abstract class ForEachStep<E, I> : SameValidationBehavior, RepositoryDependentStep where E : Identifiable<I> {

    abstract override val repository: Repository<E, I>

    override fun execute() {
        repository.elements.forEach {
            process(it)
        }
    }
    abstract fun process(element: E)
}

/**
 * [ForAllStep] is a [ModelStep] that applies a (non mutating) action to all element of the [repository].
 *
 * Unlike [ForEachStep] where the action is applied to each current element individually,
 * [ForAllStep] passes all current elements of the [repository] as a collection to the [processAll] action.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which all elements should be processed
 */
abstract class ForAllStep<E, I> : SameValidationBehavior, RepositoryDependentStep where E : Identifiable<I> {

    abstract override val repository: Repository<E, I>

    override fun execute() {
        processAll(repository.elements.toList())
    }
    abstract fun processAll(element: Collection<E>)
}

/**
 * A SealStep is a [ModelStep] that seals the given repository denying any future modification.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository to be built
 */
class SealStep<E, I>(
    override val repository: MutableRepository<E, I>,
) : MutatingStep<E, I> where E : Identifiable<I> {

    override val name: String = "seal ${repository.name}"

    override val dependentRepositories: Set<MutableRepository<*, *>> = emptySet()

    override fun execute() {
        repository.seal()
        println("Sealed ${repository.name} repo: ${repository.size} elements. This repo can no longer be updated!")
    }

    override fun verifyInput(): Warning? = null
    override fun mockBehavior(): Warning? = validateScope("Try seal ${repository.name}") {
        repository.seal()
        // no print, compared to execute()
    }
}
