@file:Suppress("MaximumLineLength")

package datastructure.matrix

import domain.data.ZoneId
import java.nio.file.Path
import kotlin.time.measureTime

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type O.
 */
class VisumMatrix<O>(path: Path, private val converter: (Double) -> O) : Matrix<ZoneId, O> {

    private val data by lazy {
        println("Starting parsing of $path")
        lateinit var matrixData: DoubleArray
        lateinit var indexData: Array<ZoneId>
        val duration = measureTime {
            val parser = MatrixParser(path)
            matrixData = parser.getArray()
            indexData = parser.getZoneIds()
        }
        println("Parsing of $path took $duration")
        matrixData to indexData.withIndex().associate { (index, zoneId) -> zoneId to index }
    }
    private val matrix get() = data.first

    private val indexLookup get() = data.second

//    private val parser: () ->  IVisumParser =  {MatrixParser(path)}
//    private val parser: IVisumParser = VisumParser(path)

    /**
     * Get the value at the specified row and column in the matrix.
     *
     * @param row The origin Zone.
     * @param column The destination Zone.
     * @return The value at the specified row and column.
     * @throws IllegalArgumentException if the row or column key is not found in the index lookup.
     */
    override fun get(row: ZoneId, column: ZoneId): O {
        val rowIndex = indexLookup[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            indexLookup[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")

//         Calculate the index in the one-dimensional matrix
        val index = rowIndex * indexLookup.size + columnIndex
        return converter(matrix[index])
    }

    fun toFloatMatrix(): FloatMatrix<O> {
        return FloatMatrix(indexLookup.size, indexLookup, matrix.map { it.toFloat() }.toFloatArray(), converter)
    }
}
