@file:Suppress("MaximumLineLength")

package domain.shared.datastructure.matrix.visum

import core.datastructure.matrix.FloatMatrix
import core.datastructure.matrix.Matrix
import core.datastructure.matrix.MatrixFormat
import core.datastructure.matrix.MatrixParser
import domain.shared.location.ZoneId
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
            val parser = VisumMatrixParser(path)
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

    fun toFloatMatrix(): FloatMatrix<ZoneId, O> {
        return FloatMatrix(indexLookup.size, indexLookup, matrix.map { it.toFloat() }.toFloatArray(), converter)
    }
}

val VisumMatrixFormat = MatrixFormat(
    key = "visum_matrix",
    parser = object : MatrixParser<ZoneId> {
        override fun <O> getMatrix(path: Path, converter: (Double) -> O) = VisumMatrix(path, converter)
    },
)
