package domain.shared.datastructure.matrix

import domain.shared.location.ZoneId

/**
 * Return a single value for every request.
 */
class ConstantZoneIdMatrix(val value: Double) : ZoneIdMatrix {
    override fun get(row: ZoneId, column: ZoneId): Double = value
}
