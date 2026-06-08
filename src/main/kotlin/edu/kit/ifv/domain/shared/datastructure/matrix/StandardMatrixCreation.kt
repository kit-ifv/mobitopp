package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlInfo

/**
 * A specialization when we know that the creation of the Matrices will result in a guaranteed standard matrix.
 * Useful for caching and creating backup binary matrices, as these instructions require an actual matrix to write
 * a binary file and cannot operate on [ConstantZoneIdMatrix] as it does not meet the format requirements of
 * [domain.shared.datastructure.matrix.binary.StandardMatrixBinaryFormat]
 */
fun interface StandardMatrixCreation : ZoneMatrixCreation {
    override fun createMatrix(config: YamlInfo): StandardMatrix
}
