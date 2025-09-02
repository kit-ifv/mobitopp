package domain.shared.datastructure.matrix.yaml

import domain.shared.datastructure.matrix.CachedMatrixLookup
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import utils.ExpiringLookup

fun interface YamlMatrixLookup<M> : ExpiringLookup<M, YamlInfo> {
    fun cached(matrixCreator: ZoneMatrixCreation): CachedMatrixLookup<M> {
        return CachedMatrixLookup(this, matrixCreator)
    }
}