package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.core.datastructure.matrix.MatrixCreationInstruction
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlInfo
import edu.kit.ifv.domain.shared.location.zone.ZoneId
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
