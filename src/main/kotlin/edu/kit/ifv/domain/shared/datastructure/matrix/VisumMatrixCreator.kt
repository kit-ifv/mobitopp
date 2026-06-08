package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlInfo

object VisumMatrixCreator : StandardMatrixCreation {
    override fun createMatrix(config: YamlInfo): StandardMatrix = StandardMatrix.parseAsVisumMatrix(config.path)
}
