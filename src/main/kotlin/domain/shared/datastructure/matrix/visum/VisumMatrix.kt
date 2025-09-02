@file:Suppress("MaximumLineLength")

package domain.shared.datastructure.matrix.visum

import core.datastructure.matrix.DoubleMatrix
import core.datastructure.matrix.IndexEncoder
import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.ZoneId
import java.nio.file.Path

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type O.
 */
class VisumMatrix(path: Path) : ZoneIdMatrix {

    private val data by lazy {
        val (matrixData, indexData) = VisumMatrixParser(path)
        matrixData to indexData.withIndex().associate { (index, zoneId) -> zoneId to index }
    }

    private val indexLookup by lazy { data.second }
    val matrix by lazy { DoubleMatrix(data.first, indexLookup.size) }
    val converter = IndexEncoder<ZoneId> {
        indexLookup[it] ?: throw IllegalArgumentException("Column $it not found in index lookup")
    }

    override fun get(row: ZoneId, column: ZoneId): Double {
        val rowIndex = indexLookup[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            indexLookup[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")
        return matrix[rowIndex, columnIndex]
    }

}
