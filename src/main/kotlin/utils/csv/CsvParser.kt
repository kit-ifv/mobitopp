package utils.csv

import java.io.File

interface CsvParser<E> {

    fun parse(path: String): Sequence<E> {
        return parse(File(path))
    }

    fun parse(file: File): Sequence<E> {
        val csv = CsvReader.read(file)
        return parse(csv)
    }

    fun parse(csv: CsvReader): Sequence<E>

}

abstract class RowCsvParser<E> : CsvParser<E> {

    override fun parse(csv: CsvReader): Sequence<E> {
        return csv.rows().map { parse(it) }
    }

    abstract fun parse(row: Row): E

}

open class DefaultRowCsvParser<E> (
    protected val entitySpawner: (Int) -> E,
    protected val columParsers: Map<String, (E, String) -> Unit>
) : RowCsvParser<E>() {
    protected var idCount = 0

    override fun parse(row: Row): E {
        val entity = entitySpawner(idCount++)
        columParsers.forEach { (column, parser) -> parser(entity, row.get(column)) }
        return entity
    }
}

interface MapCsvParser<K, V> : CsvParser<Pair<K, V>> {

    fun parseMap(path: String): Map<K, V>  {
        return parseMap(File(path))
    }

    fun parseMap(file: File): Map<K, V> {
        val csv = CsvReader.read(file)
        return parseMap(csv)
    }

    fun parseMap(csv: CsvReader) : Map<K, V>
}

open class DefaultMapCsvParser<K, V>(
    protected val pairParser: CsvParser<Pair<K, V>>
) : MapCsvParser<K, V> {
    override fun parseMap(csv: CsvReader): Map<K, V> {
        return parse(csv).toMap()
    }

    override fun parse(csv: CsvReader) = pairParser.parse(csv)

}

open class MapMergeCsvParser<K, V>(
    protected val pairParser: CsvParser<Pair<K, V>>
) : MapCsvParser<K, List<V>> {
    override fun parseMap(csv: CsvReader): Map<K, List<V>> {
        return pairParser.parse(csv).groupBy( keySelector = { it.first }, valueTransform = { it.second })
    }

    override fun parse(csv: CsvReader): Sequence<Pair<K, List<V>>> {
        TODO("Not supported")
    }


}