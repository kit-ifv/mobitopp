package utils.csv

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.streams.asSequence


interface Row {
    fun get(column: String): String
}

class DefaulRow(
    protected val source: String,
    protected val rowNumber: Int,
    protected val columnIndex: Map<String, Int>,
    protected val values: List<String>
): Row {
    override fun get(column: String): String {
        require(column in columnIndex) {
            "The given column '$column' is missing in $source." +
            "Available columns: ${columnIndex.keys}"
        }

        val index = columnIndex[column]!!

        require(0 <= index && index < values.size) {
            "The given column's index is out of range in row $rowNumber of $source." +
            "Column: $column, index: $index, row length: ${values.size}, values: $values."
        }

        return values[index]
    }

}

interface CsvReader {
    companion object {
        fun read(file: File) = DefaultCsvReader(file)
    }
    fun columns(): Collection<String>
    fun rows(): Sequence<Row>
}

open class DefaultCsvReader(
    protected val file: File,
    protected val separator: String = ";"
): CsvReader {
    protected val columns: Map<String, Int>
    protected val name: String = file.name //TODO maybe use path instead?

    init {
        columns = readHeader(file)
    }

    private fun readHeader(file: File): Map<String, Int> { //TODO IO Error Handling
        val reader = BufferedReader(FileReader(file))
        val header = reader.readLine()
        reader.close()

        return parseHeader(header)
    }

    private fun parseHeader(header: String): Map<String, Int> {
        return parseLine(header).mapIndexed { index, s -> s to index }.toMap()
    }

    override fun columns(): Collection<String> {
        return columns.keys
    }

    override fun rows(): Sequence<Row> {
        val reader = BufferedReader(FileReader(file))
        var idCnt = 0

        return reader.lines()
                    .asSequence()
                    .drop(1)
                    .map { line -> parseSafely(idCnt++, line) }
                    .filterNotNull()

    }

    private fun parseSafely(index: Int, line: String): Row? {
        return parseRow(index, line) //TODO exception handling, line empty ...
    }

    private fun parseRow(index: Int, line: String) = DefaulRow(name, index, columns, parseLine(line))

    private fun parseLine(line: String): List<String> {
        return line.split(separator)
            .map { it.trim('"') }
    }

}