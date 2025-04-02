package usecases.steps.binary

import utils.csv.DefaultCsvReader
import utils.csv.Row
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Functional interface for writing a string encoded element onto a [DataOutputStream].
 */
fun interface WriteStrategy {
    fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int)
}

/**
 * Default WriteStrategies for basic datatypes.
 */
@Suppress("ConstructorParameterNaming")
data class DataType(
    val INT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeInt(element.toInt())
    },
    val LONG: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeLong(element.toLong())
    },
    val BOOLEAN: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeBoolean(element.toBoolean())
    },
    val STRING: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeString(element, stringLength)
    },
    val FLOAT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeFloat(element.toFloat())
    },
    val DOUBLE: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeDouble(element.toDouble())
    },
    val CHAR: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeChar(element.toCharArray().first().code)
    },
    val SHORT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeShort(element.toInt())
    },
    val BYTE: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeByte(element.toInt())
    }
)

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
     * List<<List<DatatypeForRowElement>>: One row of the csv after another. The sequence of columns given by
     *                                      [datatypeMapping] is kept. Strings are cut and padded to [stringLength]-
     *                                      many characters.
     * ```
     *
     * @param csvFile The file to convert.
     * @param datatypeMapping Maps all columns, which should be converted, to a [WriteStrategy]. Conversions for basic
     * datatypes are given by [DataType].
     * @param stringLength The number of characters of a string that will be transferred to the binary file. Longer
     * strings will get cut. Shorter strings will get padded with '.'
     * @param outputFile Path to a binary file, where the result of the conversion should be stored. If not specified, a
     * binary file will get created at the same location and same name as the [csvFile].
     * @return path to the created binary file.
     */
    fun makeCSVBinary(
        csvFile: Path,
        datatypeMapping: Map<(String), WriteStrategy>,
        stringLength: Int,
        outputFile: Path? = null
    ): Path {
        require(csvFile.exists()) { "Can't convert nonexistent csv file does not exist: $csvFile" }
        require(csvFile.toString().endsWith(".csv")) { "Pls enter a csv file: $csvFile" }

        val reader = DefaultCsvReader(csvFile.toFile(), showProgressBar = false)
        require(reader.columns.containsAll(datatypeMapping.keys)) {
            val wrongNames = datatypeMapping.keys.filter { !reader.columns.contains(it) }
            "The csv doesn't contain columns with the following names: $wrongNames"
        }
        val numElements = reader.rows().count()
        val outputLocation: Path = outputFile ?: Path(csvFile.toString().replace(".csv", ".bin"))

        handleStream(outputLocation) { outputStream ->
            // Write the amount of elements that are expected to be found in this file.
            outputStream.writeInt(numElements)
            // Write the string length to be expected from this binary file.
            outputStream.writeInt(stringLength)
            // Write elements.
            writeElements(reader.rows(), outputStream, datatypeMapping, stringLength)
        }
        return outputLocation
    }

    /**
     * Writes all rows after another onto the dataStream. Only columns for which [datatypeMapping] has a key are
     * written. [datatypeMapping] dictates they order, in which elements of the row are written.
     */
    private fun writeElements(
        rows: Sequence<Row>,
        dataStream: DataOutputStream,
        datatypeMapping: Map<(String), WriteStrategy>,
        stringLength: Int
    ) {
        rows.forEach { row ->
            datatypeMapping.forEach { (columnName, strategy) ->
                strategy.writeToStream(dataStream, row.invoke(columnName), stringLength)
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
}
