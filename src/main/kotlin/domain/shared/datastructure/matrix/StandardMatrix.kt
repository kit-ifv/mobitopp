package domain.shared.datastructure.matrix

import core.datastructure.matrix.DoubleMatrix
import core.datastructure.matrix.IndexEncoder
import core.datastructure.matrix.MappedDoubleMatrix
import domain.shared.datastructure.matrix.visum.VisumMatrixParser
import domain.shared.location.ZoneId
import java.nio.file.Path

class StandardMatrix private constructor(
    override val matrix: DoubleMatrix,
    private val indexLookup: Map<ZoneId, Int>
) :
    ZoneIdMatrix,
    MappedDoubleMatrix<ZoneId> {

    override val converter: IndexEncoder<ZoneId> = IndexEncoder { indexLookup[it]!! }
    val size get() = keys.size
    val keys = indexLookup.keys


    override fun equals(other: Any?): Boolean {
        if (other !is StandardMatrix) return false
        return matrix == other.matrix && indexLookup == other.indexLookup
    }

    override fun hashCode(): Int {
        var result = matrix.hashCode()
        result = 31 * result + indexLookup.hashCode()
        return result
    }
    companion object {
        /**
         * Represents a matrix of values parsed from a Visum file.
         *
         * @param path The path to the Visum file.
         * @param converter Function to convert Double to generic type O.
         */
        fun parseAsVisumMatrix(path: Path): StandardMatrix {
            val (matrixData, indexData) = VisumMatrixParser(path)
            return fromValues(matrixData, indexData)
        }

        fun fromValues(values: DoubleArray, zoneIds: Array<ZoneId>): StandardMatrix {
            require(values.size == zoneIds.size * zoneIds.size) {
                "Cannot create a matrix from values ${values.size} when ${zoneIds.size} is not matching"
            }
            val doubleMatrix = DoubleMatrix(values, zoneIds.size)
            val indexLookup = zoneIds.withIndex().associate { (index, zoneId) -> zoneId to index }
            return StandardMatrix(doubleMatrix, indexLookup)
        }

        fun fromValues(values: Collection<Double>, zoneIds: Collection<ZoneId>): StandardMatrix {
            return fromValues(values.toDoubleArray(), zoneIds.toTypedArray())
        }
    }
}
