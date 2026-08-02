package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import edu.kit.ifv.utils.codes.Encodable
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * A [ZoneMatrixLookup] that wraps another [domain.shared.datastructure.matrix.yaml.YamlMatrixLookup] and caches created matrices
 * to avoid repeated I/O or computation.
 *
 * ### Behavior
 * - Delegates lookups to a backing [yaml] lookup, which provides [domain.shared.datastructure.matrix.yaml.YamlMatrixLookup.get] and
 *   an optional expiration time for a given `(mode, time)` pair.
 * - Uses [matrixCreator] to build a [ZoneIdMatrix] from the underlying info
 *   when no valid cached result exists.
 * - Stores the result in a lightweight cache keyed by [M] (mode).
 *   Each cache entry is associated with an expiration time. If the current time
 *   is earlier than the cached entry’s expiration, the cached matrix is reused.
 *
 * @param M the type representing transport modes
 * @param yaml the underlying matrix lookup backed by YAML configuration
 * @param matrixCreator factory for constructing [ZoneIdMatrix] instances from the yaml info
 */

class CachedMatrixLookup<M : Encodable>(
    private val yaml: YamlMatrixLookup<M>,
    private val matrixCreator: ZoneMatrixCreation,
) : ZoneMatrixLookup<M> {
    private val cache: MatrixCache<M> = MatrixCache.fromRange(yaml.codeRange)

    // TODO this code is not parallel safe, because two threads will cause a double read from matrixCreator
    //
    override fun get(mode: M, time: AbsoluteTime): ZoneIdMatrix {
        cache[mode, time]?.let { (validUntilExclusive, matrix) ->
            if (time < validUntilExclusive) {
                return matrix
            }
        }
        val (yamlInfo, expiration) = yaml[mode, time]
        val matrix = matrixCreator.createMatrix(yamlInfo)
        cache[mode, expiration] = matrix
        return matrix
    }
    private class MatrixCache<M : Encodable>(initialSize: Int) {

        private val arrayCache: Array<Pair<AbsoluteTime, ZoneIdMatrix>?> = Array(initialSize + 1) { null }

//        private val cache: MutableMap<M, Pair<AbsoluteTime, ZoneIdMatrix>> = mutableMapOf()
        operator fun get(mode: M, time: AbsoluteTime): Pair<AbsoluteTime, ZoneIdMatrix>? {
            return arrayCache[mode.code]
//            return cache[mode]
        }

        operator fun set(mode: M, validUntilExclusive: AbsoluteTime, matrix: ZoneIdMatrix) {
            arrayCache[mode.code] = validUntilExclusive to matrix
        }

        companion object {
            fun <M : Encodable> fromRange(intRange: IntRange): MatrixCache<M> {
                require(intRange.first >= 0) { "intRange should not be less than 0" }
                return MatrixCache(intRange.endInclusive)
            }
        }
    }
}
