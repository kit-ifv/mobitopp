package domain.shared.datastructure.matrix.visum

import core.datastructure.matrix.DoubleMatrix
import core.datastructure.matrix.IndexEncoder
import core.datastructure.matrix.MappedDoubleMatrix
import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.ZoneId
import java.nio.file.Path

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type O.
 */
class VisumMatrix(path: Path) : ZoneIdMatrix, MappedDoubleMatrix<ZoneId> {

    private val data by lazy {
        val (matrixData, indexData) = VisumMatrixParser(path)
        matrixData to indexData.withIndex().associate { (index, zoneId) -> zoneId to index }
    }

    private val indexLookup by lazy { data.second }
    override val matrix by lazy { DoubleMatrix(data.first, indexLookup.size) }
    override val converter = IndexEncoder<ZoneId> {
        indexLookup[it] ?: throw IllegalArgumentException("Column $it not found in index lookup")
    }
    }
