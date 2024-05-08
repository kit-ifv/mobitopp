@file:Suppress("MaximumLineLength")

package utils.matrix

import data.ZoneId
import java.nio.file.Path

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type T.
 */
class VisumMatrix<T>(path: Path, private val converter: (Double) -> T): Matrix<ZoneId, T>() {
    private lateinit var matrix: Array<Double>

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    private fun getMatrix(): Array<Double> {
        if (!this::matrix.isInitialized) {
            matrix = parser.getArray()
        }
        return matrix
    }

    private lateinit var indexLookup: HashMap<ZoneId, Int>

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    private fun getIndexLookup(): HashMap<ZoneId, Int> {
        if (!this::indexLookup.isInitialized) {
            val zoneIds = parser.getZoneIds()
            indexLookup = HashMap()
            for ((index, zoneId) in zoneIds.withIndex()) {
                indexLookup[zoneId] = index
            }
        }
        return indexLookup
    }

    private val parser: VisumParser

    init {
        this.parser = VisumParser(path)
    }

    /**
     * Get the value at the specified row and column in the matrix.
     *
     * @param row The origin Zone.
     * @param column The destination Zone.
     * @return The value at the specified row and column.
     * @throws IllegalArgumentException if the row or column key is not found in the index lookup.
     */
    override fun get(row: ZoneId, column: ZoneId): T {
        val rowIndex = getIndexLookup()[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex = getIndexLookup()[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")
        val matrix = getMatrix()

        // Calculate the index in the one-dimensional matrix
        val index = rowIndex * getIndexLookup().size + columnIndex

        return converter(matrix[index])
    }
}
