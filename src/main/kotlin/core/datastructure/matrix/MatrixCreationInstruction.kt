package core.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlInfo

fun interface MatrixCreationInstruction<I> {
    fun createMatrix(yamlInfo: YamlInfo): TranslatedDoubleMatrix<I>
}