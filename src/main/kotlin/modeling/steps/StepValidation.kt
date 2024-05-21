@file:Suppress("TooManyFunctions")

package modeling.steps

import utils.Builder
import utils.ErrorHandling
import utils.collections.muteProgressBars
import utils.collections.unmuteProgressBars
import utils.csv.CsvReader
import utils.csv.DefaultCsvReader
import utils.csv.Row

/** Create an empty copy of the given resource but holding the same metadata. */
fun <E> dummyCopyOf(resource: Resource<E>): Resource<E> {
    val name = resource.name
    val source = resource.source

    return SequenceResource(name, source, emptySequence())
}

/** Create an empty dummy resource referencing the given  [ModelStep] in its metadata. */
fun <E> dummyResource(step: ModelStep): Resource<E> {
    val name = "${step::class.simpleName}-Dummy"
    val source = "${step::class.simpleName}.validate()"

    return SequenceResource(name, source, emptySequence())
}

/** Check whether the given repository has the expected state. */
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

/**
 * Validate the given lambda function, catch exceptions (return false if caught) and print warnings.
 * Otherwise, return result of lambda function.
 */
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

/** Mock the finished state in the given [RepositoryBuilder] to validate subsequent states.*/
fun repairPreparingState(
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

/** Mock the preparing state in the given [RepositoryBuilder] to validate subsequent states.*/
fun <B> repairPreparingState(
    repository: RepositoryBuilder<B, *, *>,
    resource: Resource<B>
) where B : Builder<*> = when (repository.state) {
    RepositoryState.UNINITIALIZED -> {
        repository.addBuilders(dummyCopyOf(resource))
    }

    RepositoryState.PREPARING -> { /* State is already PREPARING */ }

    RepositoryState.FINISHED -> {
        repository.reset()
        repository.addBuilders(dummyCopyOf(resource))
    }
}

/**
 * This validation has some is fuzzy logic: if a csv value cannot be mocked for the parser,
 * validation is suspended for this parser. We don't know if the csv parser step is valid:
 * the columns after the unmockable one might be invalid. We also don't know if the step is invalid,
 * all columns might be valid but no test string is given. Hence, a warning is printed to the console.
 * In case of 'don't know' we return true, otherwise validation would always fail,
 * if validity of any csv column cannot be decided.
 *
 * Validate if csv file exists and can be read, then validate existence of columns required by the csv parser.
 */
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
            muteProgressBars()
            csv.parser.parse(this).toList()
            unmuteProgressBars()
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
