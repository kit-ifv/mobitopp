package modeling.steps

import modeling.validation.Warning
import modeling.validation.subValidation
import modeling.validation.validateCondition
import modeling.validation.validateScope
import utils.ConsoleCaptor
import utils.Identifiable
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.units.logTime
import java.io.File

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

    protected abstract val resource: Resource<E>

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
 * An [UpdateStep] is a [ModelStep] used to modify / update the internal state of elements in a given repository
 * by applying an action to each element in the repository. This action may alter state variables of the element.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 */
abstract class UpdateStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.updateEach(name, this::update)
    }

    abstract fun update(element: E)
}

/**
 * An [TransformStep] is a [ModelStep] used to modify / update elements in a given repository
 * by applying a transformation (mapping) to each element in the repository.
 * The transformation might evaluate to null, which removes the element from the repository.
 *
 * Unlike [TransformAllStep] where the new elements are computed from data of all current elements in the repository,
 * [TransformStep] maps each current element to a new element or null.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 */
abstract class TransformStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.transformEach(name, this::transform)
    }

    abstract fun transform(element: E): E?
}

/**
 * [TransformAllStep] is a [ModelStep] that replaces all elements of a repository by new / derived elements.
 *
 * Unlike [TransformStep] where each current element is mapped to a new element or null,
 * [TransformAllStep] computes the new elements from data of all current elements in the repository.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @property repository the repository in which all elements should be updated
 */
abstract class TransformAllStep<E, I> : MutatingStep<E, I>, SameValidationBehavior where E : Identifiable<I> {

    override fun execute() {
        repository.transformAll(name, this::transformAll)
    }

    abstract fun transformAll(elements: Collection<E>): Collection<E>
}

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

@Suppress("LongParameterList")
class LoadCsvStep<E, I>(
    file: File,
    override val name: String = "load ${file.name}",
    parser: CsvParser<E>,
    delimiter: String = SEMICOLON,
    override val repository: MutableRepository<E, I>,
    override val dependentRepositories: Set<Repository<*, *>>,
    private val validationMock: List<E>,
) : AddCsvStep<E, I>() where E : Identifiable<I> {

    override val resource: CsvResource<E> by lazy { CsvResource(file, parser, delimiter) }

    override fun mockElementsForValidation(): List<E> = validationMock
}
