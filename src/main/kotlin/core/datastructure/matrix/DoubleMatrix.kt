package core.datastructure.matrix

/**
 * A lightweight 2-D matrix backed by a contiguous [DoubleArray].
 *
 * - Provides `(row, column)` access via [get].
 * - Stored in row-major order for cache-friendly access.
 * - Non-generic to avoid boxing overhead.
 * - The underlying array is hidden to prevent accidental mutation;
 *   use [values] to obtain an immutable [List] view.
 *
 * @property numColumns number of columns in the matrix
 */
class DoubleMatrix(private val values: DoubleArray, val numColumns: Int) {
    operator fun get(row: Int, column: Int): Double {
        val index = row * numColumns + column
        return values[index]
    }


    fun values(): List<Double> {
        return values.toList()
    }
}