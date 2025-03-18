package utils.binary

import utils.csv.DefaultCsvParser
import utils.csv.DefaultCsvReader
import utils.csv.Row
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists


enum class DataType {
    INT, LONG, BOOL, STRING {
        override fun maxByteLength(): Int {
            throw Error("Please specify the maximum String length, otherwise the String cannot be properly decoded afterwards.")
        }
    },
    FLOAT, DOUBLE, CHAR; /** Float not in [TrackingBuffer], unwanted? **/

    /**
     * @return the maximum length the datatype has in bytes.
     */
    open fun maxByteLength(): Int = 2
}

/**
 * Converts a CSV files into binary files.
 */
class CSVBinaryConverter {

    /**
     * Takes any CSV file, which contains at least one column with an ID and converts it to a binary file.
     * The binary file is put into the same directory, as the CSV file.
     */
    fun <T> makeCSVBinary(csvFile: Path, mapping: (Row) -> T?, binaryWriter: BinaryWriter<T>) {
        val reader = DefaultCsvParser(mapping = mapping)
        val outputLocation: Path = Path(csvFile.toString().replace(".csv", ".bin"))
        binaryWriter.toBinary(
            outputLocation, reader.parse(csvFile.toFile()).toList()
        ) // if the intermediate step is kept, this could be somehow pipelined
    }

    /**
     * YES, this is the function you searched for! Easily convert csv files to performant binary files!
     * Takes any CSV file, which contains at least one column with an ID and converts it to a binary file.
     * The binary file is written to the same location as the csv, unless otherwise specified.
     *
     * Writes into following format:
     * ```
     * Int: number of elements found in the binary file.
     * List<Long>: All IDs of elements sequentially.
     * List<<List<DatatypeForRowElement>>: One row of the csv after another, without the id. The given sequence of columns is kept.
     * ```
     *
     * @param csvFile The file to convert.
     * @param datatypeMapping Should map each column-name to it's appropriate datatype.
     * @param outputFile Path to a binary file, where the result of the conversion should be stored. If not specified, a
     * binary file will get created at the same location and same name as the [csvFile].
     */
    fun makeCSVBinary(
        csvFile: Path,
        datatypeMapping: Map<(String), DataType>,
        outputFile: Path?,
    ) {
        require(csvFile.exists()) { "csv file does not exist: $csvFile" }
        require(csvFile.endsWith(".csv")) { "Pls enter a csv file: $csvFile" }

        val reader = DefaultCsvReader(csvFile.toFile(), showProgressBar = false)
        val idColumn = reader.columns.first { it.contains("id|ID") }
        val otherColumns = reader.columns.filter { it != idColumn }
        require(otherColumns.all { datatypeMapping.containsKey(it) }) {
            "No datatype provided for following columns: ${
                otherColumns.filter {
                    !datatypeMapping.containsKey(
                        it
                    )
                }
            }"
        }

        val ids = reader.rows().map { row -> row.invoke(idColumn) }.toList()
        val numElements = ids.size

        val outputLocation: Path = outputFile ?: Path(csvFile.toString().replace(".csv", ".bin"))


        Files.newOutputStream(outputLocation).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    outputStream.writeInt(numElements) // Write the amount of elements that are expected to be found in this file
                    ids.forEach { outputStream.writeLong(it.toLong()) } // Write ID's of the elements

                    /**
                     * Takes a column name and maps it to a strategy for writing that column element of the row onto the bytebuffer.
                     */
                    val writeMap = createColumnMapping(datatypeMapping, otherColumns, outputStream)

                    reader.rows().forEach { row ->
                        otherColumns.forEach { columnName ->
                            writeMap[columnName]?.invoke(row) // Write elements
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a map, which maps column names onto strategies of writing an element of a row onto the [outputStream].
     */
    private fun createColumnMapping(
        datatypeMapping: Map<(String), DataType>,
        columnsWithoutID: List<String>,
        outputStream: DataOutputStream
    ): Map<String, (Row) -> Unit> {
        val writeMap: MutableMap<String, (Row) -> Unit> = mutableMapOf()
        columnsWithoutID.forEach { columnName ->
            writeMap[columnName] = when (datatypeMapping[columnName]!!) {
                DataType.LONG -> { row: Row -> outputStream.writeLong(row.invoke(columnName).toLong()) }
                DataType.INT -> { row: Row -> outputStream.writeInt(row.invoke(columnName).toInt()) }
                DataType.BOOL -> { row: Row ->
                    outputStream.writeBoolean(
                        row.invoke(columnName).toBoolean()
                    )
                }

                DataType.STRING -> { row: Row ->
                    outputStream.writeChars(
                        row.invoke(columnName).padEnd(datatypeMapping[columnName]!!.maxByteLength(), '.')
                    )
                }

                DataType.FLOAT -> { row: Row -> outputStream.writeFloat(row.invoke(columnName).toFloat()) }
                DataType.DOUBLE -> { row: Row ->
                    outputStream.writeDouble(
                        row.invoke(columnName).toDouble()
                    )
                }

                DataType.CHAR -> { row: Row -> outputStream.writeChar(row.invoke(columnName).toInt()) }
            }
        }
        return writeMap
    }
}
