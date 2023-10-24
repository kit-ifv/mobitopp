package utils.csv

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
 * Error handling strategies for csv parsing.
 *
 * If an exception is thrown while parsing a value of a Row, different
 * strategies can be applied to cope with these parsing errors:
 * - SILENT_DROP: drop the entity/row without warning
 * - SILENT_KEEP: keep the entity/row without warning
 * - WARN_DROP: drop the entity/row and print a warning
 * - WARN_KEEP: keep the entity/row but print a warning
 * - THROW: throw an exception with detailed message
 */
enum class ParserErrorHandling { //TODO introduce interface? TODO maybe separate error handling for missing column

    /** Upon parsing errors: Drop the entity/row without warning. */
    @Deprecated("Using silent error handling (SILENT_DROP) for csv parsing is discouraged!")
    SILENT_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? = null
    },

    /** Upon parsing errors: Keep the entity/row without warning. */
    @Deprecated("Using silent error handling (SILENT_KEEP) for csv parsing is discouraged!")
    SILENT_KEEP {
        // TODO should this be discouraged ore removed entirely?
        override fun <E> handleException(e: Exception, entity: E, message: String): E? = entity
    },

    /** Upon parsing errors: Drop the entity/row and print a warning */
    WARN_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("WARNING (dropping row): $message")
            return null
        }
    },

    /** Upon parsing errors: Keep the entity/row but print a warning */
    WARN_KEEP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("WARNING (keeping row): $message")
            return entity
        }
    },

    /** Upon parsing errors: Drop the entity/row and print error and stack trace */
    ERROR_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("ERROR (dropping row): $message")
            e.printStackTrace()
            return null
        }
    },

    /** Upon parsing errors: Keep the entity/row but print error and stack trace */
    ERROR_KEEP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("ERROR (keeping row): $message")
            e.printStackTrace()
            return entity
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            throw IllegalArgumentException(message, e) //TODO which specific exception type should we use here?
        }
    };

    /**
     * Handle parsing: obtain the given column value of the row then parse
     * and set it in the given entity. In case of parsing errors: apply the
     * specific error handling strategy.
     *
     * @param entity the entity for which the value is parsed
     * @param column the column of the value to be parsed
     * @param row the row containing the value to be parsed
     * @param runnable a function that applies the parsing and returns the entity or null
     * @param E the generic type of the entity to be processed
     * @return the (updated) entity, may be null if errors occurred and the
     *     error handling strategies drops the entity/row
     */
    fun <E> handleParsing(
        entity: E?,
        column: String,
        row: Row,
        runnable: () -> E?
    ): E? { //TODO make parser class to add name attribute and description for error handling

        return try {
            runnable() ?:
            handleException( //on first null returned by any parser handle exception
                RuntimeException("Parsing row ${row.index()} in column $column returned null."),
                entity,
                message(entity, column, row)
            )

        } catch (e: NumberFormatException) { //TODO add more exceptions, can we build this without exceptions?
            handleException(e, entity, message(entity, column, row))
        }
    }

    private fun <E> message(entity: E, column: String, row: Row) =
        "Could not parse column $column of row ${row.index()} in ${row.source()} for entity $entity. Value: ${
            row.get(
                column
            )
        }"

    /**
     * Handle exception: this abstract method specifies whit is to be done in
     * case of parsing errors. It is implemented by the concrete error handling
     * strategies.
     *
     * @param e the exception that was caught
     * @param entity the entity being processed
     * @param message the error message
     * @param E the generic type of the processed entity
     * @return the given entity if it is to be kept despite the parsing errors,
     *     or null if it is to be dropped
     */
    protected abstract fun <E> handleException(e: Exception, entity: E, message: String): E?

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
    protected val exceptionHandling: ParserErrorHandling = ParserErrorHandling.WARN_KEEP
) : RowCsvParser<E>() {
    protected var idCount = 0

    override fun parse(row: Row): E? {
        var entity: E? = entitySpawner(idCount++)

        columParsers.forEach { (col, parser) ->
            entity = exceptionHandling.handleParsing(entity, col, row) {
                entity?.let{ parser(it, row.get(col)) }
            }
        }

        return entity
    }
}

/**
 * A RowCsvParser for parsing values of a single column.
 * This is for convenience, as no entity spawner has to be provided as in [DefaultRowCsvParser].
 *
 * @param E type of the values to be parsed
 * @constructor create a row based csv parser for the given column
 * @property valueColumn name of the column to be parsed
 * @property parser parsing functions for given columns
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 */
open class CsvValueParser<E> (
    protected val valueColumn: String,
    protected val exceptionHandling: ParserErrorHandling = ParserErrorHandling.WARN_KEEP,
    protected val parser: (String) -> E?,
) : RowCsvParser<E>() {
    override fun parse(row: Row): E? {

        return exceptionHandling.handleParsing(null, valueColumn, row) {
            parser(row.get(valueColumn))
        }

    }
}

/**
 * A RowCsvParser for parsing key value pairs defined by two columns.
 * The [DefaultRowCsvParser] is unhandy for pairs as they are immutable and cannot be modified.
 * This implementation does not require a´n entity spawner.
 * This parser can be transformed into a [MapCsvParser] or [MapMergeCsvParser].
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
open class CsvPairParser<K, V> (
    protected val keyColumn: String,
    protected val valueColumn: String,
    protected val exceptionHandling: ParserErrorHandling = ParserErrorHandling.WARN_KEEP,
    protected val keyParser: (String) -> K?,
    protected val valueParser: (String) -> V?,
) : RowCsvParser<Pair<K, V>>() {
    override fun parse(row: Row): Pair<K, V>? {

        return exceptionHandling.handleParsing(null, valueColumn, row) {
            val key = keyParser(row.get(keyColumn))
            val value = valueParser(row.get(valueColumn))

            key?.let { k -> value?.let { v -> k to v } }
        }

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
 * A default implementation of RowCsvParser.
 * In case of duplicate keys, the last occurrence is present in the resulting [Map]
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
