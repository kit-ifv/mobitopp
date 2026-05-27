package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlInfo

/**
 * The original implementation, switching between parser instructions based on the key found in the matrix.
 */
object KeyBasedMatrixCreation : ZoneMatrixCreation {
    override fun createMatrix(config: YamlInfo): ZoneIdMatrix {
        val (description, path) = config
        return when (description) {
            "visum_matrix" -> StandardMatrix.parseAsVisumMatrix(path)
            "constant_matrix" -> ConstantZoneIdMatrix(path.toString().toDouble())
            else -> throw NoSuchElementException("$config")
        }
    }
}

object VisumMatrixCreator : StandardMatrixCreation {
    override fun createMatrix(config: YamlInfo): StandardMatrix = StandardMatrix.parseAsVisumMatrix(config.path)
}
