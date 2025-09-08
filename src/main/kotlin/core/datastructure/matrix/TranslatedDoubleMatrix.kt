package core.datastructure.matrix

/**
 * A double-valued origin-destination matrix with generic row/column indices.
 *
 * Internally, indices of type [I] are converted to integers via an [IndexEncoder].
 * The underlying matrix is stored as a [DoubleMatrix].
 *
 * @param I the type of row/column indices (ZoneId, LocationId, etc.).
 */
interface TranslatedDoubleMatrix<I> {
    operator fun get(row: I, column: I): Double
}
