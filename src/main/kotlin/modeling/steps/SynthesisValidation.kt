@file:Suppress("TooManyFunctions")
package modeling.steps

import Builder
import Identifiable
import utils.ErrorHandling
import utils.csv.CsvReader
import utils.csv.DefaultCsvReader
import utils.csv.Row

fun <E> dummyCopyOf(resource: Resource<E>, step: ModelStep): Resource<E> {
        val dummy = dummyResource<E>(step)

        val name = ErrorHandling.WARNING.handle(runnable = { resource.name })
        { "Could not obtain name of resource in step '${step.name}'!" }
            ?: dummy.name

        val source = ErrorHandling.WARNING.handle(runnable = { resource.source })
        { "Could not obtain source of resource in step '${step.name}'!" }
            ?: dummy.source

        return SequenceResource(name, source, emptySequence())
    }

fun <E> dummyResource(step: ModelStep): Resource<E> {
    val name = "${step.javaClass.simpleName}-Dummy"
    val source = "${step.javaClass.simpleName}.validate()"

    return SequenceResource(name, source, emptySequence())
}

fun validateState(repository: LateInitRepository<*>, expectedState: RepositoryState, step: ModelStep): Boolean {
        if (repository.state != expectedState) {
            println("Error: expected state after execution of step '${step.name}' " +
                    "is expected to be $expectedState but is ${repository.state}")
            return false
        }
        return true
    }

@Suppress("TooGenericExceptionCaught")
fun validateScope(step: ModelStep, validation: () -> Boolean): Boolean =
    try {
        ErrorHandling.WARN_COLLECT.handle(runnable = {
            val isValid = validation()
            require(isValid)
            true
        }) {"${step::class.simpleName} '${step.name}' is invalid!"} ?: false

    } catch (e: Exception) {
        println("   ${e.message}")
        false
    }


fun <E> repairInitState(
    repository: LateInitRepository<E>,
    resource: Resource<E>,
    step: ModelStep
) where E: Identifiable<E> = when(repository.state) {
    RepositoryState.UNINITIALIZED ->  {
        val dummy = dummyCopyOf(resource, step)
        repository.initialize(dummy)
    }
    RepositoryState.PREPARING -> {
        (repository as BuilderRepository<*,*>).build()
    }
    RepositoryState.FINISHED -> {
         /* State is already FINISHED. */
    }
}

fun <E, B> repairPreparingState(
    repository: BuilderRepository<B, E>,
    resource: Resource<B>,
    step: ModelStep
): Unit where B: Builder<E>, E: Identifiable<E> = when(repository.state) {
    RepositoryState.UNINITIALIZED -> {
        repository.prepare(dummyCopyOf(resource, step))
    }
    RepositoryState.PREPARING -> { /* State is already BUILDING */ }
    RepositoryState.FINISHED -> {
        repository.reset()
        repairPreparingState(repository, resource, step)
    }
}




fun <B, E> validatePrepareResourceStep(
    builderRepository: BuilderRepository<B, E>,
    resource: Resource<B>,
    step: ModelStep
) where B: Builder<E>, E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(builderRepository, RepositoryState.UNINITIALIZED, step)
    repairPreparingState(builderRepository, resource, step)
    check(validateState(builderRepository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validatePrepareCsvStep(
    repository: BuilderRepository<B, E>,
    csv: CsvResource<B>,
    step: ModelStep
) where B: Builder<E>, E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.UNINITIALIZED, step) and
                    ValidateCsvMetadata(step, csv).validate()

    repairPreparingState(repository, csv, step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}

fun <E> validateInitializeResourceStep(
    repository: LateInitRepository<E>,
    resource: Resource<E>,
    step: ModelStep
) where E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.UNINITIALIZED, step)
    repairInitState(repository, resource, step)
    check(validateState(repository, RepositoryState.FINISHED, step))
    isValid
}

fun <E> validateInitializeCsvStep(
    repository: LateInitRepository<E>,
    csv: CsvResource<E>,
    step: ModelStep
) where E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.UNINITIALIZED, step) and
            ValidateCsvMetadata(step, csv).validate()

    repairInitState(repository, csv, step)
    check(validateState(repository, RepositoryState.FINISHED, step))
    isValid
}

fun <B, E> validateFilterStep(
    repository: BuilderRepository<B, E>,
    step: ModelStep
) where B: Builder<E>, E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)

    repairPreparingState(repository, dummyResource(step), step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validateUpdateStep(
    repository: BuilderRepository<B, E>,
    step: ModelStep
) where B: Builder<E>, E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)
    repairPreparingState(repository, dummyResource(step), step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}


fun <B, E> validateBuildStep(
    repository: BuilderRepository<B, E>,
    step: ModelStep
) where B: Builder<E>, E: Identifiable<E> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)

    repairInitState(repository, dummyResource(step), step)
    check(validateState(repository, RepositoryState.FINISHED, step))
    isValid
}


class ValidateCsvMetadata<E>(
    private val step: ModelStep,
    private val csv: CsvResource<E>
): Row, CsvReader {
    companion object {
        private val testStrings = listOf("1", "1u", "1.0", "1.0f", "true", "", "(48.5, 8.6: 0, 0)")
    }
    private lateinit var reader: CsvReader
    private var isValid = true


    //Row Attributes
    override val index = 0

    // CsvReader Attributes
    override val columns
        get() = reader.columns
    override val rowCount = 1
    override val source
        get() = "ValidationRow for " + reader.source
    override fun toString() = "${source}[1]:${columns}"
    override fun hasColumn(column: String) = true
    //Assume all columns exist external module try to access them to trigger error report in case of missing column

    fun validate(): Boolean {
        if (!csv.file.exists()) {
            println("Error: Csv resource file '${csv.file}' used in step '${step.name}' does not exist!")
            isValid = false
        } else if (!csv.file.canRead()) {
            println("Error: Cannot read '${csv.file}' used in step '${step.name}': missing read permission!")
            isValid = false
        }

        reader = DefaultCsvReader(csv.file)

        ErrorHandling.ERROR.handle {
            ErrorHandling.mute()
            csv.parser.parse(this).toList()
            ErrorHandling.unmute()
        }


        return isValid
    }


    override fun rows(): Sequence<Row> {
        return sequenceOf(this)
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        validateColumnExists(column)

        for (test in testStrings) {
            try {
                return converter(test)
            } catch (e: Exception) { /**/ }
        }

        isValid = false
        println("WARNING: Value of column '$column' of $csv could not be mocked " +
                "for parsing! Validation of columns in step '${step.name}' may be incomplete!")
        error("Could not mock column '$column'!")

    }

    private fun validateColumnExists(column: String) {
        if (!reader.columns.contains(column)) {
            println("ERROR: Invalid column '$column' accessed in step '${step.name}' " +
                    "does not exist in the source csv file: ${reader.source}!")
            isValid = false
        }
    }


}
