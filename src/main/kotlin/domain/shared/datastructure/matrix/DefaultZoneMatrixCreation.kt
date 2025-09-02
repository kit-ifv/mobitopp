package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.visum.VisumMatrix
import domain.shared.datastructure.matrix.yaml.YamlInfo

/**
 * The standard implementation on how to get a Zone Matrix Creation instruction from
 */
object DefaultZoneMatrixCreation: ZoneMatrixCreation {
    override fun createMatrix(config: YamlInfo): ZoneIdMatrix {
        val (description, path) = config
        return when(description) {
            "visum_matrix" -> VisumMatrix(path)
            "constant_matrix" -> ConstantZoneIdMatrix(path.toString().toDouble())
            else -> throw NoSuchElementException("$config")
        }
    }
}