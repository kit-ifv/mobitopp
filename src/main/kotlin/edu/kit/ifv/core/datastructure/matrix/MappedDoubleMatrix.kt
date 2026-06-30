package edu.kit.ifv.core.datastructure.matrix
/**
 * A [TranslatedDoubleMatrix] backed by a raw [DoubleMatrix] with an [IndexEncoder].
 *
 * - Provides double-valued lookups by external keys of type [I].
 * - Keys are translated into zero-based indices via [converter].
 * - Backed by a contiguous [DoubleArray] through [DoubleMatrix] for efficiency.
 *
 * This avoids boxing by keeping the underlying storage primitive,
 * while still allowing domain-specific index types (e.g. `ZoneId`).
 *
 * @param I the type of external key used for row and column indices
 * @property matrix the underlying primitive double matrix
 * @property converter maps external keys [I] to integer indices
 */
interface MappedDoubleMatrix<I> : TranslatedDoubleMatrix<I> {

    val matrix: DoubleMatrix
    val converter: IndexEncoder<I>
    override operator fun get(row: I, column: I): Double {
        val rowIndex = converter.toIndex(row)
        val columnIndex = converter.toIndex(column)
        return matrix[rowIndex, columnIndex]
    }

    fun values() = matrix.values()
}
