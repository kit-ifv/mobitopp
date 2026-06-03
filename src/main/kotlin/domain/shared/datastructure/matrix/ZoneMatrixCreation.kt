package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixCreationInstruction
import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.location.zone.ZoneId
import java.nio.file.Path

/**
 * Specialization of [YamlMatrixCreationInstruction] for [ZoneId]-based matrices.
 *
 * Always produces a [ZoneIdMatrix], avoiding boxing by fixing the index type.
 */
fun interface ZoneMatrixCreation : MatrixCreationInstruction<ZoneId, YamlInfo> {
    override fun createMatrix(config: YamlInfo): ZoneIdMatrix

    fun createMatrix(path: Path) = createMatrix(YamlInfo("Unknown Parser", path))
}

