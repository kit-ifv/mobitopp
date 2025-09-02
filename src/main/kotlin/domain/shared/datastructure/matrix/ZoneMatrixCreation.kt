package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixCreationInstruction
import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.location.ZoneId

fun interface ZoneMatrixCreation : MatrixCreationInstruction<ZoneId> {
    override fun createMatrix(yamlInfo: YamlInfo): ZoneIdMatrix
}