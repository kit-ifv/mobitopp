package utils.binary

import utils.csv.DefaultCsvParser
import utils.csv.DefaultCsvReader
import utils.csv.Row
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists


/**
 * Converts a CSV file to a binary file.
 */
class CSVBinaryConverter {

    /**
     * Takes any CSV file, which contains at least one column with an ID and converts it to a binary file.
     */
    fun <T>makeCSVBinary(csvFile: Path, mapping: (Row) -> T?, binaryWriter: BinaryWriter<T>) {
        val reader = DefaultCsvParser(mapping = mapping)
        binaryWriter.toBinary(csvFile, reader.parse(csvFile.toFile(), ).toList()) // if the intermediate step is kept, this could be somehow pipelined
    }
    // this is the lazy approach...
    // actually reading each row and automatically saving the correct type would be more efficient, cause no intermediate elements are getting created.

    /**
     * Takes any CSV file, which contains at least one column with an ID and converts it to a binary file.
     * The binary file is written to the same location as the csv, unless otherwise specified. //TODO make that possible
     *
     * Writes following format:
     * Long: num Elements in file
     * List<Long>: All IDS of Elements
     * List<<List<SmallestDatatypeForElement>>: so each element after another (The Datatyperecognition is kinda hard no? Probably externally needed
     */
    fun makeCSVBinary(csvFile: Path) {
        require(csvFile.exists()) { "csv file does not exist: $csvFile" }
        require(csvFile.endsWith(".csv")) { "Pls enter a csv file: $csvFile" }
        val reader = DefaultCsvReader(csvFile.toFile(), showProgressBar = false)
        val rows = reader.rows()
        val idColumn = reader.columns.first { it.contains("id|ID") }
        val otherColumns = reader.columns.filterNot { it == idColumn }
        val ids = rows.map { row -> row.invoke(idColumn)}.toList()
        val expectedNumElements = otherColumns.size

        val outputLocation = Path(csvFile.toString().replace(".csv", ".bin"))

        val outputStream: DataOutputStream = DataOutputStream(FileOutputStream(outputLocation.toFile()))
        outputStream.writeInt(expectedNumElements) // Write the amount of elements that are expected to be found in this file
        ids.forEach { outputStream.writeLong(it.toLong()) } // Write ID's of the elements
        rows.forEach { row -> otherColumns.forEach { outputStream.writeLong(row.invoke(it).toLong()) } } //TODO LONG IS WRONG HERE, THIS NEEDS TO BE PROPER DATATYPE
    }
}