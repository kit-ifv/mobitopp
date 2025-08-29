@file:Suppress("MaximumLineLength")

package domain.shared.datastructure.matrix.visum

import core.datastructure.matrix.DoubleMatrix
import core.datastructure.matrix.Indexer
import core.datastructure.matrix.MatrixFormat
import core.datastructure.matrix.MatrixParser
import core.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.ZoneId
import java.nio.file.Path
import kotlin.time.measureTime

/**
 * Represents a matrix of values parsed from a Visum file.
 *
 * @param path The path to the Visum file.
 * @param converter Function to convert Double to generic type O.
 */
class VisumMatrix(path: Path) : ZoneIdMatrix {

    private val data by lazy {
        println("Starting parsing of $path")
        lateinit var matrixData: DoubleArray
        lateinit var indexData: Array<ZoneId>
        val duration = measureTime {
            val parser = VisumMatrixParser(path)
            matrixData = parser.getArray()
            indexData = parser.getZoneIds()
        }
        println("Parsing of $path took $duration")
        matrixData to indexData.withIndex().associate { (index, zoneId) -> zoneId to index }
    }

    private val indexLookup by lazy {data.second  }
    override val matrix by lazy { DoubleMatrix(data.first, indexLookup.size) }
    override val converter = Indexer<ZoneId>{
        indexLookup[it] ?: throw IllegalArgumentException("Column $it not found in index lookup")
    }

    override fun get(row: ZoneId, column: ZoneId): Double {
        val rowIndex = indexLookup[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            indexLookup[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")
        return matrix[rowIndex, columnIndex]
    }

}

val VisumMatrixFormat = MatrixFormat(
    key = "visum_matrix",
    parser = object : MatrixParser<ZoneId> {
        override fun getMatrix(path: Path) = VisumMatrix(path)
    },
)
