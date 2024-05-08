package utils.matrix

/**
 * An abstract class representing a matrix.
 *
 * @param I The type of index used for rows and columns.
 * @param O The type of elements stored in the matrix.
 */
abstract class Matrix<I, O> {

    /**
     * Retrieves the element at the specified row and column.
     *
     * @param row The index of the row.
     * @param column The index of the column.
     * @return The element at the specified row and column.
     */
    abstract fun get(row: I, column: I): O
}
