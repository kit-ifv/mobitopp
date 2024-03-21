package utils.csv

import utils.ErrorHandling
import utils.collections.toLazyList
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.streams.asSequence

const val SEMICOLON = ";"
const val COMMA = ","
private const val QUOTE = "\""

/** The interface row provides methods to obtain properties of csv rows. */
interface Row {
    /**
     * Describes the source containing this [Row].
     *
     * @return string description of the source containing this [Row]
     */
    fun source(): String

    /**
     * Returns the index of this [Row].
     *
     * @return the [Row]'s index
     */
    fun index(): Int

    /**
     * Gets the [Row]'s value of the given column.
     *
     * @param column the column for which the [Row]'s value should be returned
     * @return this [Row]'s value at the given column
     */
    operator fun get(column: String): String

    /**
     * Parse this [Row]'s value in the given column using the given parser.
     *
     * @param column the column of which the value should be parsed
     * @param converter converts the csv string value to the desired type
     * @param T the desired result type
     * @return the parsed value of this [Row] in the given column
     */
    operator fun <T> invoke(column: String, converter: (String) -> T): T {
        return converter(this[column])!!
    }

}

/**
 * Default implementation of the [Row] interface
 *
 * @constructor create a row with the given values
 * @property source string description of the source containing this row
 * @property rowNumber the number of this row (unique with respect to
 *     source)
 * @property columnIndex mapping of column names to value list index
 * @property values list of values of this row
 */
open class DefaultRow(
    protected val source: String,
    protected val rowNumber: Int,
    protected val columnIndex: Map<String, Int>,
    protected val values: List<String>
) : Row {

    override fun source() = source
    override fun index() = rowNumber

    @Suppress("TooGenericExceptionCaught")
    override fun get(column: String): String {
        require(column in columnIndex) {
            "The given column '$column' is missing in $source. " +
                    "Available columns: ${columnIndex.keys}"
        }

        val index = columnIndex[column]!!

        return try {
            values[index]

        } catch (i: IndexOutOfBoundsException) { //Why is IndexOutOfBoundsException too generic?
            val message = "The given column's index is out of range in row $rowNumber of $source. " +
                    "Column: $column, index: $index, values: $values."
            throw IllegalArgumentException(message, i)
        }

    }

    override fun toString() = "$source[$rowNumber]=$values"

}

/**
 * [CsvReader] interface provides methods for reading csv rows and header
 * from files.
 */
interface CsvReader {
    companion object {
        /**
         * Create a [CsvReader] for the given [File]
         *
         * @param file the csv [File] to be read
         * @return a [CsvReader] for the given file
         */
        @Suppress("FunctionMinLength")
        fun of(file: File, separator: String = SEMICOLON) = DefaultCsvReader(file, separator)
    }

    /**
     * Return the column names of the csv file.
     *
     * @return a set of column names
     */
    fun columns(): Set<String>

    /**
     * Returns a sequence of rows of the csv file.
     *
     * @return a [Sequence] of [Row]s
     */
    fun rows(): Sequence<Row>

    fun rowCount(): Int
}

/**
 * Default implementation of the [CsvReader] interface. Reads the file
 * header upon creation, all other rows are read lazily.
 *
 * @constructor create a [DefaultCsvReader] for the given [File] using the
 *     given separator
 * @property file the file to be read
 * @property separator the separator to be used; defaults to ';'
 */
open class DefaultCsvReader(
    protected val file: File,
    protected val separator: String = ";",
    protected val errorHandling: ErrorHandling = ErrorHandling.ERROR
) : CsvReader {
    protected val columns: Map<String, Int>
    protected val rowCount: Int
    protected val name: String = file.name //TODO maybe use path instead?

    init {
        val reader = BufferedReader(FileReader(file))
        val header = reader.readLine()
        rowCount = 0 //reader.lineSequence().count() //TODO profile performance cost of counting
        reader.close()

        columns = parseHeader(header)
    }

    private fun parseHeader(header: String): Map<String, Int> {
        return lineValues(header).mapIndexed { index, s -> s to index }.toMap()
    }

    override fun rowCount() = rowCount

    override fun columns(): Set<String> {
        return columns.keys
    }

    override fun rows(): Sequence<Row> {
        val reader = BufferedReader(FileReader(file))
        var idCnt = 0

        val sequence = reader.lines()
            .asSequence()
            .drop(1)
            .map { line -> parseSafely(idCnt++, line) }

        return sequence.filterNotNull()

    }

    private fun parseSafely(index: Int, line: String): Row? =
        errorHandling.handleReadRow(line) { l -> parseRow(index, l) }

    private fun parseRow(index: Int, line: String) = DefaultRow(name, index, columns, lineValues(line).toLazyList())

    private fun lineValues(line: String): Sequence<String> =
        when {
            line.isEmpty() -> emptySequence()
            QUOTE !in line -> line.splitToSequence(separator)
            else -> generateSequence(nextValue(line)) {
                it.second?.let { rest -> nextValue(rest) } ?: (null to null)
            }.map { it.first }.takeWhile { it != null }.map { it!! }
        }

    private fun nextValue(line: String): Pair<String?, String?> {
        val isQuoted = line.startsWith(QUOTE)
        val delim = if (isQuoted) QUOTE else separator
        val text = if (isQuoted) line.drop(QUOTE.length) else line

        val parts = text.split(delim, limit = 2)

        if (parts.size == 1) {
            return parts[0] to null
        }

        val rest = if (isQuoted) parts[1].drop(separator.length) else parts[1]
        return parts[0] to rest.ifBlank { null }
    }

}


/**
 * Handle reading csv line.
 *
 * @param line the line to be read
 * @param reader the reader
 * @param T the generic result type
 * @return result of the reader or null
 * @receiver ErrorHandling
 */
fun <T> ErrorHandling.handleReadRow(line: String, reader: (String) -> T?): T? {
    return this.handle(runnable = { reader(line) }) { "Error reading csv line $line" }
}


/**
 * Handle exceptions while operating on certain [Row]: In case of parsing
 * errors: apply the specific error handling strategy and add row
 * information to the error message.
 *
 * @param row the row being operated on
 * @param runnable the operation to be executed
 * @param E the generic result type
 * @return result of the operation
 * @receiver ErrorHandling
 */
fun <E> ErrorHandling.handleParseRow(
    row: Row,
    runnable: () -> E?,
): E? = this.handle(runnable) {
    "Could not parse row ${row.index()} in ${row.source()}: $row"
}

/**
 * Execute parsing and handle exceptions: obtain the given column value
 * of the row then parse and parse it to an entity. In case of parsing
 * errors: apply the specific error handling strategy and add row/column
 * information to the error message.
 *
 * @param row the row being parsed
 * @param E the generic type of the entity being parsed
 * @return the (transformed) entity or null
 * @receiver ErrorHandling
 */
fun <E> ErrorHandling.handleParseValue(
    row: Row,
    column: String,
    parser: (String) -> E?,
): E? = this.handle(runnable = {

    val cell = this.handle(runnable = {
        row[column]
    }) { //Error message if column does not exist
        "Could not find column $column in row $row."
    }

    cell?.let {
        parser(cell)
    }

}) { //Error message for parsing errors
    "Could not parse column $column of row ${row.index()} in ${row.source()}: $row"
}
