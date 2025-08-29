package core.datastructure.matrix

import domain.shared.location.CostMetric
import domain.shared.location.DurationMetric
import domain.shared.location.ZoneId
import utils.units.AbsoluteTime

/**
 * A lookup structure for matrices.
 *
 * The index type [I] is generic: it could represent zones, locations,
 * or any other identifier that can be mapped to integer indices via an [Indexer]
 *
 * @param M the mode dimension (e.g. transport mode).
 * @param I the index type for rows and columns (e.g. ZoneId).
 */
interface MatrixLookup<M, I> {
    operator fun get(mode: M, time: AbsoluteTime): IndexedDoubleMatrix<I>
}
/**
 * Specialization of [MatrixLookup] for zone-based indices.
 *
 * Unlike the generic [MatrixLookup], this interface removes the
 * type parameter for indices, ensuring that [ZoneId] (a value class)
 * is passed around unboxed wherever possible.
 *
 * This avoids boxing overhead in performance-critical lookups.
 *
 * @param M the mode dimension (e.g. transport mode).
 */
interface ZoneMatrixLookup<M> : MatrixLookup<M, ZoneId>{
    override fun get(mode: M, time: AbsoluteTime): ZoneIdMatrix

}
class CostMultiMatrix<M>(
    private val rawMatrix: ZoneMatrixLookup<M>,
    private val converter: DoubleToCurrency,
) {
    operator fun get(mode: M, time: AbsoluteTime): CostMetric {
        return CurrencyMatrix(rawMatrix[mode, time], converter)
    }
}
class DurationMultiMatrix<M>(
    private val rawMatrix: ZoneMatrixLookup<M>,
    private val converter: DoubleToDuration,
) {
    operator fun get(mode: M, time: AbsoluteTime): DurationMetric {
        return DurationMatrix(rawMatrix[mode, time], converter)
    }
}

fun <T, M, I, O> T.matrixAt(
    mode: M,
    time: AbsoluteTime,
): IndexedDoubleMatrix<I> where T : MatrixLookup<M, I>  = this[mode, time]
