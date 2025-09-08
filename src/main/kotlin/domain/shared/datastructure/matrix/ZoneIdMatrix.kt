package domain.shared.datastructure.matrix

import core.datastructure.matrix.TranslatedDoubleMatrix
import domain.shared.location.Location
import domain.shared.location.ZoneId

/**
 * A specialization of [core.datastructure.matrix.TranslatedDoubleMatrix] for zone-based indices.
 *
 * Provides efficient lookups by [domain.shared.location.ZoneId] (unboxed) and convenience
 * access by [domain.shared.location.Location], which is mapped to its enclosing [domain.shared.location.ZoneId].
 */
interface ZoneIdMatrix : TranslatedDoubleMatrix<ZoneId> {
    override operator fun get(row: ZoneId, column: ZoneId): Double

    operator fun get(row: Location, column: Location): Double {
        return get(row.requireZone().id, column.requireZone().id)
    }
}
