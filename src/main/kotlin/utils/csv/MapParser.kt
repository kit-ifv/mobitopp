package utils.csv

import java.nio.file.Path
import kotlin.io.path.Path

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
    fun parseMap(path: String, separator: String = SEMICOLON): Map<K, V> = parseMap(Path(path), separator)

    /**
     * Parse the given csv [Path] as map.
     *
     * @param path the csv path to be parsed
     * @return a map containing the parsed values by key
     */
    fun parseMap(path: Path, separator: String = SEMICOLON): Map<K, V> {
        val csv = CsvReader.of(path, separator)
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
 * A default implementation of [MapCsvParser]. In case of duplicate keys,
 * the last occurrence is present in the resulting [Map].
 *
 * @param K the generic key type
 * @param V the generic value type
 * @constructor creates a [DefaultMapCsvParser] using the given [CsvParser]
 * @property pairParser the [CsvParser] used to parse key-value [Pair]s
 */
open class DefaultMapCsvParser<K, V>(protected val pairParser: CsvParser<Pair<K, V>>) : MapCsvParser<K, V> {
    override fun parseMap(csv: CsvReader): Map<K, V> = parse(csv).toMap()

    override fun parse(csv: CsvReader) = pairParser.parse(csv)
}

fun <P, K, V> P.toMapParser() where P : CsvParser<Pair<K, V>> = DefaultMapCsvParser(this)

/**
 * A [MapCsvParser] which collects values with duplicate keys in a list.
 * The method parse(CsvReader) is not supported.
 *
 * @param K the generic key type
 * @param V the generic value type
 * @constructor creates a [DefaultMapCsvParser] using the given [CsvParser]
 * @property pairParser the [CsvParser] used to parse key-value [Pair]s
 */
open class MapMergeCsvParser<K, V>(protected val pairParser: CsvParser<Pair<K, V>>) : MapCsvParser<K, List<V>> {
    override fun parseMap(csv: CsvReader): Map<K, List<V>> = pairParser.parse(csv).groupBy(keySelector = {
        it.first
    }, valueTransform = { it.second })

    override fun parse(csv: CsvReader): Sequence<Pair<K, List<V>>> = throw UnsupportedOperationException(
        "MapMergeCsvParser#parse(CsvReader) is not supported and should not be called!",
    )
}

fun <P, K, V> P.toMapMergeParser() where P : CsvParser<Pair<K, V>> = MapMergeCsvParser(this)

/**
 * A RowCsvParser for parsing key value pairs defined by two columns.
 *
 * @param K type of the keys to be parsed
 * @param V type of the values to be parsed
 * @property keyParser a [CsvParser] for the keys
 * @property valueParser a [CsvParser] for the values
 */
class TwoColumnParser<K, V>(protected val keyParser: RowCsvParser<K>, protected val valueParser: RowCsvParser<V>) :
    RowCsvParser<Pair<K, V>> {
    override fun parse(row: Row): Pair<K, V>? {
        val key: K? = keyParser.parse(row)
        val value: V? = valueParser.parse(row)

        return key?.let { k -> value?.let { v -> k to v } }
    }

    fun asMapParser(): MapCsvParser<K, V> = DefaultMapCsvParser(this)
    fun asMergeMapParser(): MapMergeCsvParser<K, V> = MapMergeCsvParser(this)
}
