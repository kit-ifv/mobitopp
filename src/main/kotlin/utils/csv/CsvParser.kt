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
abstract class RowCsvParser<E> : CsvParser<E> {
    //TODO add validation: check if all required columns are available

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
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 */
open class DefaultRowCsvParser<E>(
    protected val mapping: (String) ->((String) -> E) -> E,
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARN_DROP
): RowCsvParser<E>() {
    override fun parse(row: Row): E? {

        val t = row::convert

        return exceptionHandling.handleParseRow(row) {
            mapping(row::convert)
        }
    }

}

/*
* parser = CsvParser {column ->
*   Household(
*       year = int.column("year", ),
*       size = boolean.column("size")
*       size =
*   )
* }
*
* */

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
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARN_DROP,
    protected val parser: (String) -> E?,
) : RowCsvParser<E>() {
    override fun parse(row: Row): E? {
        return exceptionHandling.handleParseValue(row, valueColumn, parser)
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
 * @property keyParser parsing functions for the keys
 * @property valueParser parsing functions for the values
 *     when parsing errors occur
 */
open class CsvPairParser<K, V>(
    protected val keyParser: RowCsvParser<K>,
    protected val valueParser: RowCsvParser<V>,
) : RowCsvParser<Pair<K, V>>() {
    override fun parse(row: Row): Pair<K, V>? {

        val key: K? = keyParser.parse(row)
        val value: V? = valueParser.parse(row)

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
 * Execute parsing and handle exceptions: obtain the given column value of
 * the row then parse and parse it to an entity. In case of parsing errors:
 * apply the specific error handling strategy.
 *
 * @param row the row being parsed
 * @param E the generic type of the entity being parsed
 * @return the (transformed) entity or null
 * @receiver ErrorHandling
 */
fun <E> ErrorHandling.handleParseRow(
    row: Row,
    mapping: () -> E?,
): E? = this.handle(mapping){
    "Could not parse row ${row.index()} in ${row.source()}: $row"
}

/**
 * Execute parsing and handle exceptions: obtain the given column value of
 * the row then parse and parse it to an entity. In case of parsing errors:
 * apply the specific error handling strategy.
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
    }) {
        "Could not find column $column in row $row."
    }

    cell?.let {
        parser(cell)
    }

}) {
    "Could not parse column $column of row ${row.index()} in ${row.source()}: $row"
}
