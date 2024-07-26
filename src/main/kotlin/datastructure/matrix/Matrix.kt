package datastructure.matrix

/**
 * An abstract class representing a matrix.
 *
 * @param I The type of index used for rows and columns.
 * @param O The type of elements stored in the matrix.
 */
interface Matrix<I, O> {

    /**
     * Retrieves the element at the specified row and column.
     *
     * @param row The index of the row.
     * @param column The index of the column.
     * @return The element at the specified row and column.
     */
    operator fun get(row: I, column: I): O
}

interface IntoMatrix<I, O> {
    fun into()
}

class ConstantMatrix<O>(val value: O) : Matrix<Any, O> {
    override fun get(row: Any, column: Any): O = value
}
