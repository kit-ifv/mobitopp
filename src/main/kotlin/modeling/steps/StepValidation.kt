@file:Suppress("TooManyFunctions")

package modeling.steps

import utils.Builder
import utils.ErrorHandling
import utils.Identifiable
import utils.csv.CsvReader
import utils.csv.DefaultCsvReader
import utils.csv.ErrorHandlingRow
import utils.csv.Row

fun <E> dummyCopyOf(resource: Resource<E>, step: ModelStep): Resource<E> {
    val dummy = dummyResource<E>(step)

    val name = ErrorHandling.WARNING.handle(runnable = {
        resource.name
    }) { "Could not obtain name of resource in step '${step.name}'!" }
        ?: dummy.name

    val source = ErrorHandling.WARNING.handle(runnable = {
        resource.source
    }) { "Could not obtain source of resource in step '${step.name}'!" }
        ?: dummy.source

    return SequenceResource(name, source, emptySequence())
}

fun <E> dummyResource(step: ModelStep): Resource<E> {
    val name = "${step.javaClass.simpleName}-Dummy"
    val source = "${step.javaClass.simpleName}.validate()"

    return SequenceResource(name, source, emptySequence())
}

fun validateState(repository: RepositoryBuilder<*, *, *>, expectedState: RepositoryState, step: ModelStep): Boolean {
    if (repository.state != expectedState) {
        println(
            "Error: expected state after execution of step '${step.name}' " +
                "is expected to be $expectedState but is ${repository.state}"
        )
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
        }) { "${step::class.simpleName} '${step.name}' is invalid!" } ?: false
    } catch (e: Exception) {
        println("   ${e.message}")
        false
    }

fun repairInitState(
    repository: RepositoryBuilder<*, *, *>,
    step: ModelStep
) = when (repository.state) {
    RepositoryState.UNINITIALIZED -> {
        repository.addBuilders(dummyResource(step))
        repository.build()
    }
    RepositoryState.PREPARING -> {
        repository.build()
    }
    RepositoryState.FINISHED -> {
        /* State is already FINISHED. */
    }
}

fun <B> repairPreparingState(
    repository: RepositoryBuilder<B, *, *>,
    resource: Resource<B>,
    step: ModelStep
) where B : Builder<*> = when (repository.state) {
    RepositoryState.UNINITIALIZED -> {
        repository.addBuilders(dummyCopyOf(resource, step))
    }

    RepositoryState.PREPARING -> { /* State is already BUILDING */ }

    RepositoryState.FINISHED -> {
        repository.reset()
        repository.addBuilders(dummyCopyOf(resource, step))
    }
}

fun <B, E, I> validatePrepareResourceStep(
    builderRepository: RepositoryBuilder<B, E, I>,
    resource: Resource<B>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<I> = validateScope(step) {
    val isValid = validateState(builderRepository, RepositoryState.UNINITIALIZED, step)
    repairPreparingState(builderRepository, resource, step)
    check(validateState(builderRepository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validatePrepareCsvStep(
    repository: RepositoryBuilder<B, E, *>,
    csv: CsvResource<B>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<*> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.UNINITIALIZED, step) and
        ValidateCsvMetadata(step, csv).validate()
    // and ValidateCsvSample(step, csv).validate()

    repairPreparingState(repository, csv, step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validateFilterStep(
    repository: RepositoryBuilder<B, E, *>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<*> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)

    repairPreparingState(repository, dummyResource(step), step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validateUpdateStep(
    repository: RepositoryBuilder<B, E, *>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<*> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)
    repairPreparingState(repository, dummyResource(step), step)
    check(validateState(repository, RepositoryState.PREPARING, step))
    isValid
}

fun <B, E> validateBuildStep(
    repository: RepositoryBuilder<B, E, *>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<*> = validateScope(step) {
    val isValid = validateState(repository, RepositoryState.PREPARING, step)

    repairInitState(repository, step)
    check(validateState(repository, RepositoryState.FINISHED, step))
    isValid
}

fun <B, E, I> validateMergeStep(
    builderRepository: RepositoryBuilder<B, E, I>,
    resource: Resource<B>,
    step: ModelStep
) where B : Builder<E>, E : Identifiable<I> = validateScope(step) {
    val isValid = validateState(builderRepository, RepositoryState.UNINITIALIZED, step)
    repairPreparingState(builderRepository, resource, step)
    check(validateState(builderRepository, RepositoryState.PREPARING, step))
    isValid
}

class ValidateCsvMetadata<E>(
    private val step: ModelStep,
    private val csv: CsvResource<E>
) : Row, CsvReader {
    companion object {
        private val testStrings = listOf("1", "1u", "1.0", "1.0f", "true", "", "(48.5, 8.6: 0, 0)")
    }
    private lateinit var reader: CsvReader
    private var isValid = true

    // Row Attributes
    override val index = 0

    override val name: String
        get() = reader.name

    // CsvReader Attributes
    override val columns
        get() = reader.columns
    override val rowCount = 1
    override val source
        get() = "ValidationRow for " + reader.source
    override fun toString() = "$source[1]:$columns"
    override fun hasColumn(column: String) = true
    // Assume all columns exist external module try to access them to trigger error report in case of missing column

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

    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        validateColumnExists(column)
        return mockResult(converter, column)
    }

    override fun <T> valueAt(columnIndex: Int, converter: (String) -> T): T {
        validateColumnIndex(columnIndex)
        return mockResult(converter, "index $columnIndex")
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun <T> mockResult(converter: (String) -> T, column: String): T {
        for (test in testStrings) {
            try {
                return converter(test)
            } catch (e: Exception) {
                /**/
            }
        }

        // isValid = false // TODO think about validity
        println(
            "WARNING: Value of column '$column' of $csv could not be mocked " +
                "for parsing! Validation of columns in step '${step.name}' may be incomplete!"
        )
        error("Could not mock column '$column'!")
    }

    private fun validateColumnIndex(columnIndex: Int) {
        if (reader.columns.size >= columnIndex) {
            println(
                "ERROR: Invalid column index '$columnIndex' accessed in step '${step.name}' " +
                    "is higher than column number in source csv file: ${reader.source}!"
            )
            isValid = false
        }
    }

    private fun validateColumnExists(column: String) {
        if (!reader.columns.contains(column)) {
            println(
                "ERROR: Invalid column '$column' accessed in step '${step.name}' " +
                    "does not exist in the source csv file: ${reader.source}!"
            )
            isValid = false
        }
    }
}

class ValidateCsvSample<E>(
    private val step: ModelStep,
    private val csv: CsvResource<E>,
    private val sampleSize: Int = 2000,
    private val reader: CsvReader = DefaultCsvReader(csv.file)
) : CsvReader by reader {

    private var messages = ""
    private var valid = true

    fun validate(): Boolean {
        csv.parser.parse(this).count()

        if (!valid) {
            println("Parsing first $sampleSize csv rows in step ${step.name} produced errors:")
            println(messages)
        }

        return valid
    }

    override val rowCount: Int = sampleSize
    override fun rows(): Sequence<Row> =
        reader.rows().take(sampleSize).map {
            ErrorHandlingRow(it, ErrorHandling.THROW) { e ->
                messages += (e.message ?: "") + "\n"
                valid = false
            }
        }
}

class ValidationRepositoryBuilder<B, E, I> : RepositoryBuilder<B, E, I>() where B : Builder<E>, E : Identifiable<I> {

    override fun getById(id: I): E? {
        return super.getById(id)
    }
}
