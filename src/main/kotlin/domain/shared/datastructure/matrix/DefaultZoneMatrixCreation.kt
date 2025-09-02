package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.visum.VisumMatrix
import domain.shared.datastructure.matrix.yaml.YamlInfo

object DefaultZoneMatrixCreation: ZoneMatrixCreation {
    override fun createMatrix(yamlInfo: YamlInfo): ZoneIdMatrix {
        val (description, path) = yamlInfo
        return when(description) {
            "visum_matrix" -> VisumMatrix(path)
            "constant_matrix" -> ConstantZoneIdMatrix(path.toString().toDouble())
            else -> throw NoSuchElementException("$yamlInfo")
        }
    }
}