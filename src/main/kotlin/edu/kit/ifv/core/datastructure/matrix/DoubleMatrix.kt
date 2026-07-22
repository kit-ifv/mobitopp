package edu.kit.ifv.core.datastructure.matrix

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
class DoubleMatrix(val values: DoubleArray, val numColumns: Int) {
    val size: Int get() = values.size
    operator fun get(row: Int, column: Int): Double {
        val index = row * numColumns + column
        return values[index]
    }

    fun values(): List<Double> = values.toList()

    override fun toString(): String = "Matrix: $size [${values.joinToString()}]"

    fun transpose(): DoubleMatrix {
        val numRows: Int = values.size / numColumns
        val transposedValues =
            DoubleArray(values.size)

        for (row in 0 until numRows) {
            val sourceOffset =
                row * numColumns

            for (column in 0 until numColumns) {
                val sourceIndex =
                    sourceOffset + column

                val targetIndex =
                    column * numRows + row

                transposedValues[targetIndex] =
                    values[sourceIndex]
            }
        }

        return DoubleMatrix(
            values = transposedValues,
            numColumns = numRows,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleMatrix) return false
        return values.contentEquals(other.values) && numColumns == other.numColumns
    }

    override fun hashCode(): Int {
        var result = numColumns
        result = 31 * result + values.contentHashCode()
        result = 31 * result + size
        return result
    }
}
