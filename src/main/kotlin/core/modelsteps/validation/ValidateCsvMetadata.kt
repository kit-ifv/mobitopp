@file:Suppress("TooManyFunctions")

package core.modelsteps.validation

import core.modelsteps.Context
import core.modelsteps.resources.CsvResource
import utils.ErrorHandling
import utils.collections.muteProgressBars
import utils.collections.unmuteProgressBars
import utils.csv.CsvReader
import utils.csv.DefaultCsvReader
import utils.csv.Row


/**
 * Validate csv metadata using [CsvHeaderChecker].
 * Retuns false if any exceptions occur or the checker returns false.
 *
 * @param C the generic type of the context
 * @param E the generic type of entities provided by the [CsvResource]
 * @param modelStep name of the model step to be validated
 * @param csv the csv resource to be validated
 * @return false if the [CsvHeaderChecker.validate] evaluates to false or an exception occurs.
 *  Regarding fuzzy logic plese read KDoc of [CsvHeaderChecker].
 */
fun <C: Context, E> C.validateCsvMetadata(
    modelStep: String,
    csv: CsvResource<E>
): Boolean = validateNoException(
    { "Validate csv metadata of ${csv.path} produced exception:" },
    exceptionsAreErrors = true
) {
    CsvHeaderChecker<E>(modelStep, this, csv).validate()
} ?: false


/**
 * This validation has some fuzzy logic: if a csv value cannot be mocked for the parser,
 * validation is suspended for this parser. We don't know if the csv parser step is valid:
 * the columns accessed after the unmockable one might be invalid.
 * In that case, we also don't know if the step is invalid, all columns might be valid,
 * but no mockable test string is given. Hence, a warning is reported.
 * In case of 'don't know' we return true; otherwise validation would always fail if the
 * validity of any csv column cannot be decided.
 *
 * Validate if csv file exists and can be read, then validate the existence of columns required by the csv parser.
 */
class CsvHeaderChecker<E>(
    private val step: String,
    private val context: Context,
    private val csv: CsvResource<E>,
) : Row, CsvReader {

    private lateinit var reader: CsvReader

    // Row Attributes
    override val index = 0
    override val size: Int get() = reader.columns.size
    override val name: String get() = reader.name
    override fun headerForIndex(i: Int): String = reader.columns.toList()[i]

    // CsvReader Attributes
    override val columns get() = reader.columns
    override val rowCount = 1
    override val source get() = "ValidationRow for " + reader.source
    override fun toString() = "$source[1] = $columns"


    private var valid = true

    fun validate(): Boolean {
        if (!context.validateFileReadAccess(csv.path)) {
            return false
        }

        muteProgressBars()
        reader = DefaultCsvReader(csv.path, errorHandling = ErrorHandling.SILENT)

        context.validateNoException({ "Validate parsing in '$step'" }, false) {
            csv.parser.parse(this).toList()
        }

        unmuteProgressBars()

        return true
    }

    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        validateColumnExists(column)
        return mockResult(converter, column)
    }

    override fun <T> valueAt(columnIndex: Int, converter: (String) -> T): T {
        validateColumnIndex(columnIndex)
        return mockResult(converter, "index $columnIndex")
    }

    override fun hasColumn(column: String): Boolean = //true // check when requested via invoke/value at
        validateColumnExists(column)

    override fun rows(): Sequence<Row> = listOf(this).asSequence()


    private fun validateColumnIndex(columnIndex: Int) =
        context.validateCondition({
            "Invalid column index '$columnIndex' accessed in step '${step}' " +
                    "is higher than number of columns (${reader.columns.size}) in source csv file: ${reader.source}!"
        }) {
            reader.columns.size >= columnIndex
        }.also {
            valid = valid && it
        }

    private fun validateColumnExists(column: String): Boolean =
        context.validateCondition({
            "Invalid column '$column' accessed in step '${step}' " +
                    "does not exist in the source csv file: ${reader.source}!"
        }) {
            reader.columns.contains(column)
        }.also {
            valid = valid && it
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun <T> mockResult(converter: (String) -> T, column: String): T {
        for (test in testStrings) {
            try {
                return converter(test)
            } catch (e: Exception) { /**/ }
        }

        context.report.addWarningLog(
            "Mocking '$column' value in '${step}'",
            "Value of column '$column' of $csv could not be mocked for parsing! " +
                    "Validation of columns in step '${step}' may be incomplete!",
        )


        error(
            "Could not mock column $column. " +
                    "None of the following tests Strings matches the expected format for parsing: $testStrings"
        )
    }

    companion object {
        private val testStrings = listOf("1", "1u", "1.0", "1.0f", "true", "", "(48.5, 8.6: 0, 0)")
    }

}

//
///**
// * This validation has some fuzzy logic: if a csv value cannot be mocked for the parser,
// * validation is suspended for this parser. We don't know if the csv parser step is valid:
// * the columns after the unmockable one might be invalid. We also don't know if the step is invalid,
// * all columns might be valid, but no test string is given. Hence, a warning is printed to the console.
// * In case of 'don't know' we return true; otherwise validation would always fail if the
// * validity of any csv column cannot be decided.
// *
// * Validate if csv file exists and can be read, then validate the existence of columns required by the csv parser.
// */
//class ValidateCsvMetadata<E>(
//    private val step: ModelStep,
//    private val csv: CsvResource<E>
//) : Row, CsvReader {
//    companion object {
//        private val testStrings = listOf("1", "1u", "1.0", "1.0f", "true", "", "(48.5, 8.6: 0, 0)")
//    }
//
//    private lateinit var reader: CsvReader
//
//    // Row Attributes
//    override val index = 0
//    override val size: Int get() = reader.columns.size
//
//    override fun headerForIndex(i: Int): String = reader.columns.toList()[i]
//
//    override val name: String
//        get() = reader.name
//
//    // CsvReader Attributes
//    override val columns
//        get() = reader.columns
//
//    override val rowCount = 1
//    override val source
//        get() = "ValidationRow for " + reader.source
//
//    override fun toString() = "$source[1] = $columns"
//
//    // Assume all columns exist external module try to access them to trigger error report in case of missing column
//    override fun hasColumn(column: String) = true
//
//    private var parentWarning: Warning? = null
//
//    @Suppress("EmptyCatchBlock", "TooGenericExceptionCaught", "SwallowedException")
//    fun validate() = validateScope(
//        "Validate csv metadata of ${csv.path} produced warnings:",
//        exceptionsAreErrors = true,
//    ) {
//        parentWarning = this
//
//        validateFileReadAccess(csv.path)?.also {
//            this.addChild(it)
//            return@validateScope
//        }
//
//        muteProgressBars()
//        reader = DefaultCsvReader(csv.path, errorHandling = ErrorHandling.SILENT)
//        try {
//            csv.parser.parse(this@ValidateCsvMetadata).toList()
//        } catch (_: Exception) {
//        }
//
//        unmuteProgressBars()
//
//        parentWarning = null
//    }
//
//    override fun rows(): Sequence<Row> {
//        return listOf(this).asSequence()
//    }
//
//    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
//        validateColumnExists(column)
//        return mockResult(converter, column)
//    }
//
//    override fun <T> valueAt(columnIndex: Int, converter: (String) -> T): T {
//        validateColumnIndex(columnIndex)
//        return mockResult(converter, "index $columnIndex")
//    }
//
//    @Suppress("TooGenericExceptionCaught", "SwallowedException")
//    private fun <T> mockResult(converter: (String) -> T, column: String): T {
//        for (test in testStrings) {
//            try {
//                return converter(test)
//            } catch (e: Exception) {
//                /**/
//            }
//        }
//
//        parentWarning?.addChild(
//            "WARNING: Value of column '$column' of $csv could not be mocked " +
//                "for parsing! Validation of columns in step '${step.name}' may be incomplete!",
//            isError = false
//        )
//
//        error(
//            "Could not mock column $column. " +
//                "None of the following tests Strings matches the expected format for parsing: $testStrings"
//        )
//    }
//
//    private fun validateColumnIndex(columnIndex: Int) =
//        parentWarning?.validateNoException {
//            require(reader.columns.size >= columnIndex) {
//                "ERROR: Invalid column index '$columnIndex' accessed in step '${step.name}' " +
//                    "is higher than number of columns (${reader.columns.size}) in source csv file: ${reader.source}!"
//            }
//        } ?: error(VALIDATION_MODE_ERROR)
//
//    private fun validateColumnExists(column: String) =
//        parentWarning?.validateNoException {
//            require(reader.columns.contains(column)) {
//                "ERROR: Invalid column '$column' accessed in step '${step.name}' " +
//                    "does not exist in the source csv file: ${reader.source}!"
//            }
//        } ?: error(VALIDATION_MODE_ERROR)
//}
