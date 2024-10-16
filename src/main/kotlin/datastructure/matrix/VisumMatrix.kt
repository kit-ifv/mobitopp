@file:Suppress("MaximumLineLength")

package datastructure.matrix

import domain.data.ZoneId
import java.nio.file.Path

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type O.
 */
class VisumMatrix<O>(path: Path, private val converter: (Double) -> O) : Matrix<ZoneId, O> {
    private val matrix by lazy { parser.getArray() }

    private val indexLookup by lazy { parser.getZoneIds().withIndex().associate { (index, zoneId) -> zoneId to index } }

    private val parser: IVisumParser = VisumParser(path)

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

        // Calculate the index in the one-dimensional matrix
        val index = rowIndex * indexLookup.size + columnIndex

        return converter(matrix[index])
    }
}
