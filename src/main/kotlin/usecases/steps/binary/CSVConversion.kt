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
    BOOLEAN {
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            dataStream.writeBoolean(element.toBoolean())
        }
    },
    STRING {
        /**
         * Scales the [element] to have exactly the length of [stringLength]. If [element] is too short,
         * it gets padded with '.', if it's too long, it everything after [stringLength] is getting ignored.
         * Writes characters utf8 encoded.
         */
        override fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int) {
            val scaledString = element.take(stringLength).padEnd(stringLength, '.').toCharArray()
            scaledString.forEach { dataStream.writeChar(it.code) }
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
            dataStream.writeChar(element.toCharArray().first().code)
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
     * The binary file is written to the same location as the csv, unless otherwise specified through [outputFile].
     *
     * Writes into following format:
     * ```
     * Int: number of elements found in the binary file.
     *
     * Int: maximal length of strings in the binary file in characters.
     *
     * List<<List<DatatypeForRowElement>>: One row of the csv after another. The given sequence of columns is kept.
     *                                      Strings are cut and padded to [stringLength]-many Characters.
     * ```
     *
     * @param csvFile The file to convert.
     * @param datatypeMapping Should map each column-name to it's appropriate datatype.
     * @param stringLength The number of characters of a string that will be transferred to the binary file. Longer
     * strings will get cut. Shorter strings will get padded with '.'
     * @param outputFile Path to a binary file, where the result of the conversion should be stored. If not specified, a
     * binary file will get created at the same location and same name as the [csvFile].
     * @return path to the created binary file.
     */
    fun makeCSVBinary(
        csvFile: Path,
        datatypeMapping: Map<(String), DataType>,
        stringLength: Int,
        outputFile: Path? = null
    ): Path {
        require(csvFile.exists()) { "Can't convert nonexistent csv file does not exist: $csvFile" }
        require(csvFile.toString().endsWith(".csv")) { "Pls enter a csv file: $csvFile" }

        val reader = DefaultCsvReader(csvFile.toFile(), showProgressBar = false)
        val datatypeGivenForAllColumns = reader.columns.all { datatypeMapping.containsKey(it) }
        if (!datatypeGivenForAllColumns) {
            val columnsWithoutDatatype = reader.columns.filter { !datatypeMapping.containsKey(it) }
            throw IllegalArgumentException("No datatype provided for following columns: $columnsWithoutDatatype")
        }
        val numElements = reader.rows().count()
        val outputLocation: Path = outputFile ?: Path(csvFile.toString().replace(".csv", ".bin"))

        handleStream(outputLocation) { outputStream ->
            // Write the amount of elements that are expected to be found in this file.
            outputStream.writeInt(numElements)
            // Write the string length to be expected from this binary file.
            outputStream.writeInt(stringLength)
            // Write elements.
            writeElements(reader.rows(), reader.columns.toList(), outputStream, datatypeMapping, stringLength)
        }
        return outputLocation
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
     * Creates and closes a DataOutputStream to [outputLocation]. While open executes write on it.
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
