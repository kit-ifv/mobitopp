package core.datastructure.matrix
/**
 * Maps an external key [I] to an integer index for use in array- or matrix-based structures
 * into zero-based indices of a backing [DoubleArray] or [DoubleMatrix].
 *
 * Non-generic boxing is avoided by working directly with primitive [Int] indices.
 *
 * @param I the type of key to be encoded as an index
 */
fun interface IndexEncoder<I> {
    fun toIndex(indexableElement: I): Int
}