package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixCreationInstruction
import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.location.ZoneId
/**
 * Specialization of [MatrixCreationInstruction] where the configuration is [YamlInfo].
 *
 * @param I the type of row/column keys (e.g. [ZoneId])
 */
fun interface YamlMatrixCreationInstruction<I>: MatrixCreationInstruction<I, YamlInfo>
/**
 * Specialization of [YamlMatrixCreationInstruction] for [ZoneId]-based matrices.
 *
 * Always produces a [ZoneIdMatrix], avoiding boxing by fixing the index type.
 */
fun interface ZoneMatrixCreation : MatrixCreationInstruction<ZoneId, YamlInfo> {
    override fun createMatrix(config: YamlInfo): ZoneIdMatrix
}