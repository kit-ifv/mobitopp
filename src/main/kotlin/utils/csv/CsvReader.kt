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
    /** The source containing this [Row]. */
    val source: String

    /** The index of this [Row]. */
    val index: Int

    /**
     * Parse this [Row]'s value in the given column using the given parser.
     *
     * @param column the column of which the value should be parsed
     * @param converter converts the csv string value to the desired type
     * @param T the desired result type
     * @return the parsed value of this [Row] in the given column
     */
    operator fun <T> invoke(column: String, converter: (String) -> T): T

    /**
     * Get this [Row]'s value in the given column.
     *
     * @param column the column of which the value should be parsed
     * @return the raw value of this [Row] in the given column
     * @receiver [Row]
     */
    operator fun invoke(column: String): String = invoke(column) { s -> s }

    fun hasColumn(column: String): Boolean
}

/**
 * Default implementation of the [Row] interface
 *
 * @constructor create a row with the given values
 * @property source string description of the source containing this row
 * @property index the index of this row (unique with respect to source)
 * @property columnIndex mapping of column names to value list index
 * @property values list of values of this row
 */
open class DefaultRow(
    override val source: String,
    override val index: Int,
    protected val columnIndex: Map<String, Int>,
    protected val values: List<String>
) : Row {

    @Suppress("TooGenericExceptionCaught")
    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        val columnIndex = requireNotNull(columnIndex[column]) {
            "The given column '$column' is missing in $source. " +
                "Available columns: ${columnIndex.keys}"
        }

        val string = try {
            values[columnIndex]
        } catch (i: IndexOutOfBoundsException) { // Why is IndexOutOfBoundsException too generic?
            val message = "The given column's index is out of range in row ${this.index} of $source. " +
                "Column: $column, index: $columnIndex, values: $values."
            throw IllegalArgumentException(message, i)
        }

        return converter(string)
    }

    override fun hasColumn(column: String) = columnIndex.containsKey(column)

    override fun toString() = "$source[$index]=$values"
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

    /** The column names of the csv file. */
    val columns: Set<String>

    /** The number of rows in the csv file. */
    val rowCount: Int

    /** The source (file path) of the csv file. */
    val source: String

    /**
     * Returns a sequence of rows of the csv file.
     *
     * @return a [Sequence] of [Row]s
     */
    fun rows(): Sequence<Row>
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
    protected val separator: String = SEMICOLON,
    protected val errorHandling: ErrorHandling = ErrorHandling.ERROR
) : CsvReader {

    private val columnsIndex: Map<String, Int>
    private val numberOfRows: Int
    protected val name: String = file.name // TODO maybe use path instead?

    override val source: String = file.path
    override val rowCount: Int
        get() = numberOfRows
    override val columns: Set<String>
        get() = columnsIndex.keys

    init {
        val reader = BufferedReader(FileReader(file))
        val header = reader.readLine()
        numberOfRows = reader.lineSequence().count() // TODO profile performance cost of counting
        reader.close()

        columnsIndex = parseHeader(header)
    }

    private fun parseHeader(header: String): Map<String, Int> {
        return lineValues(header).mapIndexed { index, s -> s to index }.toMap()
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

    private fun parseRow(index: Int, line: String) =
        DefaultRow(name, index, columnsIndex, lineValues(line).toLazyList(columnsIndex.size))

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
    "Could not parse row ${row.index} in '${row.source}': $row"
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
    require(row.hasColumn(column)) { // Error message if column does not exist
        "Could not find column '$column' in row: $row."
    }

    row(column, parser)
}) { // Error message for parsing errors
    "Could not parse column '$column' of row ${row.index} in '${row.source}': $row"
}
