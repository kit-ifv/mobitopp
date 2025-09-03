package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixCreationInstruction
import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.location.ZoneId
import java.nio.file.Path

/**
 * Specialization of [MatrixCreationInstruction] where the configuration is [YamlInfo].
 *
 * @param I the type of row/column keys (e.g. [ZoneId])
 */
fun interface YamlMatrixCreationInstruction<I> : MatrixCreationInstruction<I, YamlInfo>

/**
 * Specialization of [YamlMatrixCreationInstruction] for [ZoneId]-based matrices.
 *
 * Always produces a [ZoneIdMatrix], avoiding boxing by fixing the index type.
 */
fun interface ZoneMatrixCreation : MatrixCreationInstruction<ZoneId, YamlInfo> {
    override fun createMatrix(config: YamlInfo): ZoneIdMatrix

    fun createMatrix(path: Path) = createMatrix(YamlInfo("Unknown Parser", path))
}

/**
 * A specialization when we know that the creation of the Matrices will result in a guaranteed standard matrix.
 * Useful for caching and creating backup binary matrices, as these instructions require an actual matrix to write
 * a binary file and cannot operate on [ConstantZoneIdMatrix] as it does not meet the format requirements of
 * [domain.shared.datastructure.matrix.binary.StandardMatrixBinaryFormat]
 */
fun interface StandardMatrixCreation : ZoneMatrixCreation {
    override fun createMatrix(config: YamlInfo): StandardMatrix
}
