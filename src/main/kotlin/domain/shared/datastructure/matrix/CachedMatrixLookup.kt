package domain.shared.datastructure.matrix

import core.datastructure.matrix.ZoneMatrixLookup
import core.datastructure.matrix.ZoneIdMatrix
import core.datastructure.matrix.ZoneMatrixCreation
import utils.units.AbsoluteTime



class CachedMatrixLookup<M>(
    private val yaml: MatrixLookup<M>,
    private val matrixCreator: ZoneMatrixCreation,
): ZoneMatrixLookup<M> {
    private val cache: MatrixCache<M> = MatrixCache()

    override fun get(mode: M, time: AbsoluteTime): ZoneIdMatrix {
        cache[mode, time]?.let { (validUntilExclusive, matrix) ->
            if(time < validUntilExclusive) {
                return matrix
            }
        }
        val (yamlInfo, expiration) = yaml[mode, time]
        val matrix = matrixCreator.createMatrix(yamlInfo)
        cache[mode, expiration] = matrix
        return matrix
    }
}

/**
 * This cache is
 */
private class MatrixCache<M>() {
    private val cache: MutableMap<M, Pair<AbsoluteTime, ZoneIdMatrix>> = mutableMapOf()
    operator fun get(mode: M, time: AbsoluteTime): Pair<AbsoluteTime, ZoneIdMatrix>? {
        return cache[mode]
    }

    operator fun set(mode: M, validUntilExclusive: AbsoluteTime, matrix: ZoneIdMatrix) {
        cache[mode] = validUntilExclusive to matrix
    }
}