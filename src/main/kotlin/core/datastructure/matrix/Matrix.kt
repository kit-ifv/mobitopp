package core.datastructure.matrix

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

class ConstantMatrix<I, O>(val value: O) : Matrix<I, O> {
    override fun get(row: I, column: I): O = value
}

class ArrayMatrix<I, O>(
    val size: Int,
    val translation: Map<I, Int>,
    val values: Array<O>,
) : Matrix<I, O> {

    override fun get(row: I, column: I): O {
        val rowIndex =
            translation[row] ?: throw IllegalArgumentException("Row $row not found in index: $translation")
        val columnIndex =
            translation[column] ?: throw IllegalArgumentException("Column $column not found in index: $translation")

        val index = rowIndex * size + columnIndex

        return values[index]
    }
}

// TODO maybe apply the converter to the elements of the matrix directly,
//  unless this would waste storage space when <O> is complex -> alternative version: ArrayMatrix
class FloatMatrix<I, O>(
    val size: Int,
    val translation: Map<I, Int>,
    val floatArray: FloatArray,
    val converter: (Double) -> O
) : Matrix<I, O> {

    override fun get(row: I, column: I): O {
        val rowIndex =
            translation[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            translation[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")

        val index = rowIndex * size + columnIndex

        return converter(floatArray[index].toDouble())
    }
}
