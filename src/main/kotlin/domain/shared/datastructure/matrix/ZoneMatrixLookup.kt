package domain.shared.datastructure.matrix

import core.datastructure.matrix.MatrixLookup
import domain.shared.location.ZoneId
import utils.units.AbsoluteTime

/**
 * Specialization of [core.datastructure.matrix.MatrixLookup] for zone-based indices.
 *
 * Unlike the generic [core.datastructure.matrix.MatrixLookup], this interface removes the
 * type parameter for indices, ensuring that [domain.shared.location.ZoneId] (a value class)
 * is passed around unboxed wherever possible.
 *
 * This avoids boxing overhead in performance-critical lookups.
 *
 * @param M the mode dimension (most likely transport mode).
 */
interface ZoneMatrixLookup<M> : MatrixLookup<M, ZoneId> {
    override fun get(mode: M, time: AbsoluteTime): ZoneIdMatrix

}