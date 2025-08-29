package domain.shared.datastructure.matrix

import utils.WithExpiration
import utils.units.AbsoluteTime

interface ExpiringLookup<K, V> {
    operator fun get(mode: K, time: AbsoluteTime): WithExpiration<V>
}

interface MatrixLookup<M> : ExpiringLookup<M, YamlInfo>




