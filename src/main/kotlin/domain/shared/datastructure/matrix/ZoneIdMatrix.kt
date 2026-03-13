package domain.shared.datastructure.matrix

import core.datastructure.matrix.TranslatedDoubleMatrix
import domain.shared.location.ZoneId

/**
 * A specialization of [core.datastructure.matrix.TranslatedDoubleMatrix] for zone-based indices.
 *
 * Provides efficient lookups by [domain.shared.location.ZoneId] (unboxed)
 */
interface ZoneIdMatrix : TranslatedDoubleMatrix<ZoneId> {
    override operator fun get(row: ZoneId, column: ZoneId): Double
}
