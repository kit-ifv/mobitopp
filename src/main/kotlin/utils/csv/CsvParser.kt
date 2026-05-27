package utils.csv

import utils.ErrorHandling
import java.nio.file.Path

/**
 * The CsvParser interface defines methods for parsing a csv file and
 * generating a Sequence of entities of generic type E.
 *
 * It provides a default implementation for parsing a file path, File
 * or Path object for convenience.
 *
 * @param E the generic type of the entities to be parsed
 */
interface CsvParser<out E> {

    /**
     * Parse the csv file at the given path string.
     *
     * @param path the file path string of the csv file to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(path: String, separator: String = SEMICOLON): Sequence<E> = parse(Path.of(path), separator)

    /**
     * Parse the given path as csv.
     *
     * @param path the csv path to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(path: Path, separator: String = SEMICOLON): Sequence<E> {
        val csv = CsvReader.of(path, separator)
        return parse(csv)
    }

    /**
     * Parse the csv data provided by the given CsvReader.
     *
     * @param csv the csv reader providing the csv data to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(csv: CsvReader): Sequence<E>

    companion object {
        operator fun <E> invoke(errorHandling: ErrorHandling = ErrorHandling.WARNING, mapping: (Row) -> E?) =
            DefaultCsvParser(exceptionHandling = errorHandling, mapping = mapping)
    }
}

/**
 * A CsvParser parsing each csv Row as a single entity.
 *
 * @param E the generic type of the entities to be parsed
 */
interface RowCsvParser<E> : CsvParser<E> {

    override fun parse(csv: CsvReader): Sequence<E> {
        val rows = csv.rows()
        return rows.map { parse(it) }.filterNotNull()
    }

    /**
     * Parse the given Row as entity of generic type E.
     *
     * May return null, if an error occurs while parsing the row.
     *
     * @param row the row to be parsed as entity
     * @return parsed entity, may be null if parsing produces errors
     */
    fun parse(row: Row): E?
}

/**
 * Creates a row parser with a preceding filter to allow the removal of certain elements without having to fully evaluate
 * the object before filtering.
 */
fun <E> RowCsvParser<E>.withFilter(filter: (Row) -> Boolean): FilterRowCsvParser<E> = FilterRowCsvParser(this, filter)

/**
 * A default implementation of RowCsvParser.
 *
 * @param E the generic type of entities to be produced by the parser
 * @constructor create a row based csv parser with the given mapping
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 * @property mapping the mapping applied to the row to generate an entity
 */
open class DefaultCsvParser<E>(
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARNING,
    protected val mapping: (Row) -> E?,
) : RowCsvParser<E> {

    override fun parse(row: Row): E? = exceptionHandling.handleParseRow(row) {
        mapping(ErrorHandlingRow(row, exceptionHandling))
    }
}

/**
 * A RowCsvParser for parsing values of a single column. This is
 * for convenience, as no entity spawner has to be provided as in
 * [DefaultCsvParser].
 *
 * @param E type of the values to be parsed
 * @constructor create a row based csv parser for the given column, parser
 *     and [ErrorHandling] strategy
 */
open class SingleColumnParser<E>(
    valueColumn: String,
    exceptionHandling: ErrorHandling = ErrorHandling.WARNING,
    parser: (String) -> E,
) : DefaultCsvParser<E>(
    exceptionHandling,
    mapping = { row -> row(valueColumn, parser) },
)

// UTILITY

/**
 * A [Row]-wrapper that applies error handling when parsing/getting values.
 *
 * @property row the row to be wrapped
 * @property errorHandling the [ErrorHandling] strategy to be applied in
 *     case of errors
 */
class ErrorHandlingRow(
    private val row: Row,
    private val errorHandling: ErrorHandling,
    private val onError: (Exception) -> Unit = {},
) : Row by row {

    @Suppress("TooGenericExceptionCaught")
    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        try {
            return errorHandling.handleParseValue(row, column, converter)!!
        } catch (e: Exception) {
            onError(e)
            throw e
        }
    }

    override fun toString() = row.toString()
}

/**
 * A [Row]-wrapper providing a parser for a certain type [T] to be applied
 * when values are obtained from the row.
 *
 * @param T the generic result type of the parser
 * @property row the wrapped row
 * @property parser the parser to be applied when getting values
 */
class TypedRow<T>(val row: Row, val parser: (String) -> T) {

    fun <K> wrap(transformation: (T) -> K): TypedRow<K> = TypedRow(row) {
        transformation(parser(it))
    }

    operator fun invoke(column: String): T = row(column, parser)
}

class FilterRowCsvParser<E>(private val original: RowCsvParser<E>, private val filter: (Row) -> Boolean) :
    RowCsvParser<E> {

    /**
     * Parse the given Row as entity of generic type E.
     *
     * May return null, if an error occurs while parsing the row.
     *
     * @param row the row to be parsed as entity
     * @return parsed entity, may be null if parsing produces errors
     */
    override fun parse(row: Row): E? {
        if (filter(row)) return original.parse(row)
        return null
    }
}
