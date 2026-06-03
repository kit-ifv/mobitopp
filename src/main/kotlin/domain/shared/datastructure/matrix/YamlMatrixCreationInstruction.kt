package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixCreationInstruction
import domain.shared.datastructure.matrix.yaml.YamlInfo

/**
 * Specialization of [core.datastructure.matrix.MatrixCreationInstruction] where the configuration is [domain.shared.datastructure.matrix.yaml.YamlInfo].
 *
 * @param I the type of row/column keys (e.g. [domain.shared.location.zone.ZoneId])
 */
fun interface YamlMatrixCreationInstruction<I> : MatrixCreationInstruction<I, YamlInfo>