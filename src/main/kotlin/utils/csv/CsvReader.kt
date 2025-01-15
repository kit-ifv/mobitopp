package utils.csv

import utils.ErrorHandling
import utils.collections.addProgressBar
import utils.collections.toLazyList
import utils.files.decompressedBufferedReader
import java.io.File
import kotlin.io.path.fileSize
import kotlin.math.floor
import kotlin.streams.asSequence

const val SEMICOLON = ";"
const val COMMA = ","
private const val QUOTE = "\""

/** The interface row provides methods to obtain properties of csv rows. */
interface Row {
    /** The source containing this [Row]. */
    val source: String // TODO source should reference CsvReader which holds detailed information on source file

    /** The index of this [Row]. */
    val index: Int

    /** The amount of elements to be expected in this row */
    val size: Int

    /** Return the column header for a target index */
    fun headerForIndex(i: Int): String

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

    /**
     * Parse this [Row]'s value at the given index using the given parser.
     *
     * @param columnIndex the column index at which the value should be parsed
     * @param converter converts the csv string value to the desired type
     * @param T the desired result type
     * @return the parsed value of this [Row] in the given index
     */
    fun <T> valueAt(columnIndex: Int, converter: (String) -> T): T

    /**
     * Get this [Row]'s value at the given index.
     *
     * @param columnIndex the column index to look up the value
     * @return the raw value of this [Row] in the given index
     */
    fun valueAt(columnIndex: Int): String = valueAt(columnIndex) { s -> s }

    /**
     * Check whether the row/source contains the given column.
     *
     * @param column the column to be checked
     * @return true, if the given column exists in the [Row]/source
     */
    fun hasColumn(column: String): Boolean
}

/**
 * Default implementation of the [Row] interface
 *
 * @constructor create a row with the given values
 * @property source string description of the source containing this row
 * @property index the index of this row (unique with respect to source)
 * @property columnIndexMap mapping of column names to value list index
 * @property values list of values of this row
 */
open class DefaultRow(
    override val source: String,
    override val index: Int,
    protected val columnIndexMap: Map<String, Int>,
    protected val values: List<String>
) : Row {
    override val size = values.size
    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        val columnIndex = requireNotNull(columnIndexMap[column]) {
            "The given column '$column' is missing in $source. " +
                "Available columns: ${columnIndexMap.keys}"
        }

        return converter(getIndexValue(columnIndex, column))
    }

    override fun <T> valueAt(columnIndex: Int, converter: (String) -> T): T {
        return converter(getIndexValue(columnIndex))
    }

    override fun headerForIndex(i: Int): String {
        return columnIndexMap.keys.toList()[i]
    }

    @Suppress("TooGenericExceptionCaught")
    private fun getIndexValue(columnIndex: Int, column: String? = null): String = try {
        values[columnIndex]
    } catch (i: IndexOutOfBoundsException) { // Why is IndexOutOfBoundsException too generic?
        val columnName = column ?: columnIndex.toString()
        val message = "The given column's index is out of range in row ${this.index} of $source. " +
            "Column: $columnName, index: $columnIndex, values: $values."
        throw IllegalArgumentException(message, i)
    }

    override fun hasColumn(column: String) = columnIndexMap.containsKey(column)

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

    val name: String
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
    protected val errorHandling: ErrorHandling = ErrorHandling.ERROR,
    protected val showProgressBar: Boolean = true
) : CsvReader {

    private val columnsIndex: Map<String, Int>
    private val numberOfRows: Int
    override val name: String = file.name // TODO maybe use path instead?

    override val source: String = file.path
    override val rowCount: Int
        get() = numberOfRows
    override val columns: Set<String>
        get() = columnsIndex.keys

    init {
        numberOfRows = estimateRowCount(file)

        val reader = file.decompressedBufferedReader()
        val header = reader.readLine()
        reader.close()

        columnsIndex = parseHeader(header)
    }

    private fun parseHeader(header: String): Map<String, Int> {
        return lineValues(header).mapIndexed { index, s -> s to index }.toMap()
    }

    override fun rows(): Sequence<Row> {
        val reader = file.decompressedBufferedReader()
        var idCnt = 0

        val sequence = reader.lines()
            .asSequence()
            .drop(1)
            .map { line -> parseSafely(idCnt++, line) }
            .filterNotNull()

        return sequence.iterator().addProgressBar(
            label = "read $name",
            expectedCount = rowCount.toLong(),
            visible = showProgressBar
        ).asSequence()
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

fun estimateRowCount(file: File, sampleSize: Int = 10000, scale: Double = 0.9): Int {
    var rows = 0

    val reader = file.decompressedBufferedReader()
    val sample = reader.lineSequence()
        .drop(1)
        .take(sampleSize)
        .onEach { rows++ }
        .joinToString().toByteArray().size
    reader.close()

    if (rows < sampleSize) {
        return rows
    }

    val bytePerRow = sample.toDouble() / rows.toDouble()
    val fileSize = file.toPath().fileSize()

    return floor(fileSize.toDouble() * scale / bytePerRow).toInt()
}
