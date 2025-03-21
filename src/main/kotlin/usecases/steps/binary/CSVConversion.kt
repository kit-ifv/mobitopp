package usecases.steps.binary

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
    INT {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeInt(element.toInt())
        }
    },
    LONG {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeLong(element.toLong())
        }
    },
    BOOL {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeBoolean(element.toBoolean())
        }
    },
    STRING {
        /**
         * Scales the [element] to have exactly the length of [stringLength]. If [element] is too short,
         * it gets padded with '.', if it's too long, it everything after [stringLength] is getting ignored.
         */
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            val scaledString = element.take(stringLength).padEnd(stringLength, '.')
            dataStream.writeChars(scaledString)
        }
    },
    FLOAT {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeFloat(element.toFloat())
        }
    },
    DOUBLE {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeDouble(element.toDouble())
        }
    },
    CHAR {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeChar(element.toInt())
        }
    },
    SHORT {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeShort(element.toInt())
        }
    },
    BYTE {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeByte(element.toInt())
        }
    };

    /**
     * Parses element to the datatype and writes it onto the dataStream.
     * @param stringLength The length of a string written onto the [dataStream]. Only gets used in [STRING].
     */
    abstract fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int)
}

/**
 * Converts a CSV files into binary files.
 */
class CSVBinaryConverter {

    /**
     * This function convert csv files to a binary file in a standardized format.
     * The CSV file needs to contain at least one column with an ID.
     * The binary file is written to the same location as the csv, unless otherwise specified.
     *
     * Writes into following format:
     * ```
     * Int: number of elements found in the binary file.
     * Long: maximal length of strings in the binary file in characters.
     * List<Long>: All IDs of elements sequentially.
     * List<<List<DatatypeForRowElement>>: One row of the csv after another, without the ID. The given sequence of
     * columns is kept. Strings are cut to max-String-Many
     * ```
     *
     * @param csvFile The file to convert.
     * @param datatypeMapping Should map each column-name to it's appropriate datatype.
     * @param outputFile Path to a binary file, where the result of the conversion should be stored. If not specified, a
     * binary file will get created at the same location and same name as the [csvFile].
     * @param stringLength The number of characters of a string that will be transferred to the binary file. Longer
     * strings will get cut. Shorter strings will get padded with '.'
     * @param idColumnName The name of the column, which contains the IDs to be written.
     */
    fun makeCSVBinary(
        csvFile: Path,
        datatypeMapping: Map<(String), DataType>,
        stringLength: Int,
        outputFile: Path?,
        idColumnName: String
    ) {
        require(csvFile.exists()) { "Can't convert nonexistent csv file does not exist: $csvFile" }
        require(csvFile.endsWith(".csv")) { "Pls enter a csv file: $csvFile" }

        val reader = DefaultCsvReader(csvFile.toFile(), showProgressBar = false)
        val idColumn = reader.columns.first { it == idColumnName }
        val nonIDColumns = reader.columns.filter { it != idColumn }
        val datatypeGivenForAllColumns = nonIDColumns.all { datatypeMapping.containsKey(it) }
        if (!datatypeGivenForAllColumns) {
            val columnsWithoutDatatype = nonIDColumns.filter { !datatypeMapping.containsKey(it) }
            throw IllegalArgumentException("No datatype provided for following columns: $columnsWithoutDatatype")
        }

        val ids = reader.rows().map { row -> row.invoke(idColumn) }.toList()
        val numElements = ids.size
        val outputLocation: Path = outputFile ?: Path(csvFile.toString().replace(".csv", ".bin"))

        handleStream(outputLocation) { outputStream ->
            // Write the amount of elements that are expected to be found in this file.
            outputStream.writeInt(numElements)
            // Write IDs of the elements.
            ids.forEach { outputStream.writeLong(it.toLong()) }
            // Write elements.
            writeElements(reader.rows(), nonIDColumns, outputStream, datatypeMapping, stringLength)
        }
    }

    /**
     * Writes all rows after another onto the dataStream. For each row, only [columnsToWrite] are written.
     */
    private fun writeElements(
        rows: Sequence<Row>,
        columnsToWrite: List<String>,
        dataStream: DataOutputStream,
        datatypeMapping: Map<(String), DataType>,
        stringLength: Int
    ) {
        rows.forEach { row ->
            columnsToWrite.forEach { columnName ->
                val rowElement = row.invoke(columnName)
                datatypeMapping[columnName]?.writeToStream(dataStream, rowElement, stringLength)
            }
        }
    }

    /**
     * Creates and closes a DataOutputStream to [outputLocation] and executes write on it.
     */
    private fun handleStream(outputLocation: Path, write: (dataStream: DataOutputStream) -> Unit) {
        Files.newOutputStream(outputLocation).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    outputStream.run(write)
                }
            }
        }
    }

    /**
     * Convenience function. Takes any CSV file and converts it to a binary file.
     * The binary file is put into the same directory, as the CSV file.
     * @param mapping Object generator for a row of the csv file.
     * @param binaryWriter A BinaryWriter, able to write objects of the type [T].
     */
    fun <T> makeCSVBinary(csvFile: Path, mapping: (Row) -> T?, binaryWriter: BinaryWriter<T>) {
        val reader = DefaultCsvParser(mapping = mapping)
        val outputLocation = Path(csvFile.toString().replace(".csv", ".bin"))
        binaryWriter.toBinary(
            outputLocation,
            reader.parse(csvFile.toFile()).toList()
        )
    }
}
