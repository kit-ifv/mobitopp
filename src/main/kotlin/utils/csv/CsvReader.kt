package utils.csv

import utils.ErrorHandling
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

    fun convert(column: String, converter: (String) -> String): String? {
        return converter(this[column])
    }

}

/**
 * Default implementation of the [Row] interface
 *
 * @constructor create a row with the given values
 * @property source string description of the source containing this row
 * @property rowNumber the number of this row (unique with respect to
 *     source)
 * @property columnIndex mapping of column names to value list index //TODO
 *     simplify: use string, value mapping instead of string -> index +
 *     value list
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

    override fun get(column: String): String {
        require(column in columnIndex) {
            "The given column '$column' is missing in $source. " +
                    "Available columns: ${columnIndex.keys}"
        }

        val index = columnIndex[column]!!

        require(0 <= index && index < values.size) {
            "The given column's index is out of range in row $rowNumber of $source. " +
                    "Column: $column, index: $index, row length: ${values.size}, values: $values."
        }

        return values[index]
    }

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
    protected val errorHandling: ErrorHandling = ErrorHandling.ERROR_DROP
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
        return parseLine(header).mapIndexed { index, s -> s to index }.toMap()
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


//        val step = (rowCount / 10.0).toInt()
//        print("$name: ")
        return sequence/*.mapIndexed{ index, row ->
            row.also {
                if (index % step == 0) { print("*") }
                if (index == rowCount-1) { println() }
            }
        }*/.filterNotNull()

    }

    private fun parseSafely(index: Int, line: String): Row? =
        errorHandling.handleReadRow(line) { l -> parseRow(index, l) }

    private fun parseRow(index: Int, line: String) = DefaultRow(name, index, columns, parseLine(line))

    private fun parseLine(line: String): List<String> =
        errorHandling.handleReadRow(line) { l ->
            when {
                QUOTE !in l -> l.split(separator)
                l.startsWith(QUOTE) -> consumeQuoted(l.trim())
                else -> consumeUnquoted(l.trim())
            }
        } ?: emptyList()

    private fun consumeQuoted(line: String): List<String> {
        require(line.startsWith(QUOTE))
        val parts = line.split(QUOTE, limit = 3)

        return mutableListOf(parts[1]).also {
            it.addAll(
                consumeTail(parts[2])
            )
        }
    }

    private fun consumeUnquoted(line: String): List<String> {
        require(!line.startsWith(QUOTE))
        val parts = line.split(separator, limit = 2)

        val res = mutableListOf(parts[0])

        if (parts.size > 1) {
            res.addAll(parseLine(parts[1]))
        }

        return res
    }

    private fun consumeTail(line: String): List<String> =
        if (line.isEmpty()) {
            emptyList()
        } else {
            require(line.startsWith(separator)) { "line '$line' should start with '$separator'" }
            parseLine(line.drop(separator.length))
        }

}

/**
 * Handle reading csv line.
 *
 * @param line the line to be parsed
 * @param reader the reader
 * @param T the generic result type
 * @return result of the reader opr null
 * @receiver ErrorHandling
 */
fun <T> ErrorHandling.handleReadRow(line: String, reader: (String) -> T?): T? {
    return this.handle(runnable = { reader(line) }){ "Error reading csv line $line" }
}