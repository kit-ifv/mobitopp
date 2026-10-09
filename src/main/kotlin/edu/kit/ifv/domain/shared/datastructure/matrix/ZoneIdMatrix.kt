package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.core.datastructure.matrix.TranslatedDoubleMatrix
import edu.kit.ifv.domain.shared.location.zone.ZoneId

/**
 * A specialization of [core.datastructure.matrix.TranslatedDoubleMatrix] for zone-based indices.
 *
 * Provides efficient lookups by [ZoneId] (unboxed)
 */
interface ZoneIdMatrix : TranslatedDoubleMatrix<ZoneId> {
    override operator fun get(row: ZoneId, column: ZoneId): Double
    fun getIndexed(rowIndex: Int, columnIndex: Int): Double
}
