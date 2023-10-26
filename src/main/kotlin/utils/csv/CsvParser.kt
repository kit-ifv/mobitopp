package utils.csv

import utils.ErrorHandling
import java.io.File

/**
 * The CsvParser interface defines methods for parsing a csv file and
 * generating a Sequence of entities of generic type E.
 *
 * It provides a default implementation for parsing a file path or File
 * object for convenience.
 *
 * @param E the generic type of the entities to be parsed
 */
interface CsvParser<E> {

    /**
     * Parse the csv file at the given path.
     *
     * @param path the file path of the csv file to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(path: String, separator: String = SEMICOLON): Sequence<E> {
        return parse(File(path), separator)
    }

    /**
     * Parse the given file as csv.
     *
     * @param file the csv file to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(file: File, separator: String = SEMICOLON): Sequence<E> {
        val csv = CsvReader.of(file, separator)
        return parse(csv)
    }

    /**
     * Parse the csv data provided by the given CsvReader.
     *
     * @param csv the csv reader providing the csv data to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(csv: CsvReader): Sequence<E>

}

/**
 * A CsvParser parsing each csv Row as a single entity.
 *
 * @param E the generic type of the entities to be parsed
 */
abstract class RowCsvParser<E> : CsvParser<E> { //TODO add validation: check if all required columns are available

    override fun parse(csv: CsvReader): Sequence<E> {
        return csv.rows().map { parse(it) }.filterNotNull()
    }

    /**
     * Parse the given Row as entity of generic type E.
     *
     * May return null, if an error occurs while parsing the row.
     *
     * @param row the row to be parsed as entity
     * @return parsed entity, may be null if parsing produces errors
     */
    abstract fun parse(row: Row): E?

}


/**
 * A default implementation of RowCsvParser.
 *
 * @param E
 * @constructor create a row based csv parser with the given column parsing
 *     functions
 * @property entitySpawner a function to produce empty entity, receives the
 *     row index (starting at 0)
 * @property columParsers parsing functions for columns: each must receive
 *     a string value and the entity to be updated
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 */
open class DefaultRowCsvParser<E>(
    protected val entitySpawner: (Int) -> E,
    protected val columParsers: Map<String, (E, String) -> E?>,
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARN_KEEP
) : RowCsvParser<E>() {
    protected var idCount = 0

    override fun parse(row: Row): E? {
        var entity: E? = entitySpawner(idCount++)

        columParsers.forEach { (col, parser) ->
            entity = exceptionHandling.handleTransforming(
                entity,
                col,
                row,
                parser
            )
        }

        return entity
    }
}

/**
 * A RowCsvParser for parsing values of a single column. This is
 * for convenience, as no entity spawner has to be provided as in
 * [DefaultRowCsvParser].
 *
 * @param E type of the values to be parsed
 * @constructor create a row based csv parser for the given column
 * @property valueColumn name of the column to be parsed
 * @property parser parsing functions for given columns
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 */
open class CsvValueParser<E>(
    protected val valueColumn: String,
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARN_KEEP,
    protected val parser: (String) -> E?,
) : RowCsvParser<E>() {
    override fun parse(row: Row): E? {

        return exceptionHandling.handleParsing(column = valueColumn, row = row, parser = parser)

    }
}

/**
 * A RowCsvParser for parsing key value pairs defined by two columns. The
 * [DefaultRowCsvParser] is unhandy for pairs as they are immutable and
 * cannot be modified. This implementation does not require a´n entity
 * spawner. This parser can be transformed into a [MapCsvParser] or
 * [MapMergeCsvParser].
 *
 * @param K type of the keys to be parsed
 * @param V type of the values to be parsed
 * @constructor create a row based csv parser for key value pairs
 * @property keyColumn name of the key column to be parsed
 * @property valueColumn name of the value column to be parsed
 * @property keyParser parsing functions for the keys
 * @property valueParser parsing functions for the values
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 */
open class CsvPairParser<K, V>(
    protected val keyColumn: String,
    protected val valueColumn: String,
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARN_KEEP,
    protected val keyParser: (String) -> K?,
    protected val valueParser: (String) -> V?,
) : RowCsvParser<Pair<K, V>>() {
    override fun parse(row: Row): Pair<K, V>? {

        val key: K? = exceptionHandling.handleParsing(column = keyColumn, row = row, parser = keyParser)
        val value: V? = exceptionHandling.handleParsing(column = valueColumn, row = row, parser = valueParser)

        return key?.let { k -> value?.let { v -> k to v } }
    }

    fun asMapParser(): MapCsvParser<K, V> = DefaultMapCsvParser(this)
    fun asMergeMapParser(): MapMergeCsvParser<K, V> = MapMergeCsvParser(this)

}

/**
 * [MapCsvParser] are [CsvParser] for key-value [Pair]s. They provide
 * methods to transform the [Sequence] of [Pair]s into a [Map].
 *
 * @param K the generic key type
 * @param V the generic value type
 */
interface MapCsvParser<K, V> : CsvParser<Pair<K, V>> {

    /**
     * Parse the csv file at the given path as map.
     *
     * @param path the path of the csv file to be parsed
     * @return a map containing the parsed values by key
     */
    fun parseMap(path: String, separator: String = SEMICOLON): Map<K, V> {
        return parseMap(File(path), separator)
    }

    /**
     * Parse the given csv [File] as map.
     *
     * @param file the csv file to be parsed
     * @return a map containing the parsed values by key
     */
    fun parseMap(file: File, separator: String = SEMICOLON): Map<K, V> {
        val csv = CsvReader.of(file, separator)
        return parseMap(csv)
    }

    /**
     * Parse the csv data provided by the given [CsvReader] as map.
     *
     * @param csv the [CsvReader] providing the data to be parsed
     * @return a map containing the parsed values by key
     */
    fun parseMap(csv: CsvReader): Map<K, V>
}

/**
 * A default implementation of RowCsvParser. In case of duplicate keys, the
 * last occurrence is present in the resulting [Map]
 *
 * @param K the generic key type
 * @param V the generic value type
 * @constructor creates a [DefaultMapCsvParser] using the given [CsvParser]
 * @property pairParser the [CsvParser] used to parse key-value [Pair]s
 */
open class DefaultMapCsvParser<K, V>(
    protected val pairParser: CsvParser<Pair<K, V>>
) : MapCsvParser<K, V> {
    override fun parseMap(csv: CsvReader): Map<K, V> {
        return parse(csv).toMap()
    }

    override fun parse(csv: CsvReader) = pairParser.parse(csv)

}

/**
 * A [MapCsvParser] which collects values with duplicate keys in a list.
 * The method parse(CsvReader) is not supported.
 *
 * @param K the generic key type
 * @param V the generic value type
 * @constructor creates a [DefaultMapCsvParser] using the given [CsvParser]
 * @property pairParser the [CsvParser] used to parse key-value [Pair]s
 */
open class MapMergeCsvParser<K, V>(
    protected val pairParser: CsvParser<Pair<K, V>>
) : MapCsvParser<K, List<V>> {
    override fun parseMap(csv: CsvReader): Map<K, List<V>> {
        return pairParser.parse(csv).groupBy(keySelector = { it.first }, valueTransform = { it.second })
    }

    override fun parse(csv: CsvReader): Sequence<Pair<K, List<V>>> {
        throw UnsupportedOperationException(
            "MapMergeCsvParser#parse(CsvReader) is not supported and should not be called!"
        )
    }

}




/**
 * Creates parse error message for the given column, row and entity
 *
 * @param entity the parsed entity
 * @param column the parsed column
 * @param row the parsed row
 * @param E the generic type of the entity being parsed
 */
fun <E> parseErrorMessage(entity: E, column: String, row: Row) =
    "Could not parse column $column of row ${row.index()} in ${row.source()} for entity $entity. Value: ${row[column]}"

/**
 * Execute parsing + entity transformation and handle exceptions: obtain
 * the given column value of the row then parse and set it in the given
 * entity. In case of parsing errors: apply the specific error handling
 * strategy.
 *
 * @param entity the entity being created
 * @param column the column to be parsed
 * @param row the row being parsed
 * @param parser transformation function
 * @param E the generic type of the entity being parsed
 * @return the (transformed) entity or null
 * @receiver ErrorHandling
 */
fun <E> ErrorHandling.handleTransforming(
    entity: E? = null,
    column: String,
    row: Row,
    parser: (E, String) -> E?
): E? = handleGetValue(column, row)?.let { value ->
    this.handle(entity, { parseErrorMessage(entity, column, row) }) {
        entity?.let { parser(it, value) }
    }
}

/**
 * Obtain column value from row and handle exceptions: In case of parsing
 * errors: apply the specific error handling strategy.
 *
 * @param column the column to be parsed
 * @param row the row being parsed
 * @return the found error handling
 * @receiver ErrorHandling
 */
private fun ErrorHandling.handleGetValue(column: String, row: Row) =
    this.handle(errorMessage = { "Could not find column $column in row $row!" }) {
        row[column]
    }

/**
 * Execute parsing and handle exceptions: obtain the given column value of
 * the row then parse and parse it to an entity. In case of parsing errors:
 * apply the specific error handling strategy.
 *
 * @param column the column to be parsed
 * @param row the row being parsed
 * @param parser transformation function
 * @param E the generic type of the entity being parsed
 * @return the (transformed) entity or null
 * @receiver ErrorHandling
 */
fun <E> ErrorHandling.handleParsing(
    column: String,
    row: Row,
    parser: (String) -> E?
): E? = this.handleGetValue(column, row)?.let { value ->

    this.handle(null, { parseErrorMessage(null, column, row) }) {
        parser(value)
    }
}
