package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlInfo

object VisumMatrixCreator : StandardMatrixCreation {
    override fun createMatrix(config: YamlInfo): StandardMatrix = StandardMatrix.parseAsVisumMatrix(config.path)
}