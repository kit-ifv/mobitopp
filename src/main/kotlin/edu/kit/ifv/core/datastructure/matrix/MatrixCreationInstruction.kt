package edu.kit.ifv.core.datastructure.matrix
/**
 * Generic instruction for creating a [TranslatedDoubleMatrix].
 *
 * @param I the type of row/column keys
 * @param C the type of configuration or mobitopp object used during creation
 *          (e.g. [YamlInfo], a file path, or any other metadata)
 */
fun interface MatrixCreationInstruction<I, C> {
    fun createMatrix(config: C): TranslatedDoubleMatrix<I>
}
