package utils.csv

import java.io.File

interface Builder<E> {
    fun build(): E
}

interface Row {
    fun get(column: String): String
}

interface CsvReader {
    companion object {
        fun read(file: File) = DefaultCsvReader(file)
    }
    fun columns(): Collection<String>
    fun rows(): Sequence<Row>
}

open class DefaultCsvReader(
    protected val file: File
): CsvReader {

    private val columns: List<String>

    init {
        columns = listOf(file.path) //TODO
    }

    override fun columns(): Collection<String> {
        TODO("Not yet implemented")
    }

    override fun rows(): Sequence<Row> {
        TODO("Not yet implemented")
    }

}

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
        return parse(csv).groupBy( keySelector = { it.first }, valueTransform = { it.second })
    }

    override fun parse(csv: CsvReader) = pairParser.parse(csv)

}