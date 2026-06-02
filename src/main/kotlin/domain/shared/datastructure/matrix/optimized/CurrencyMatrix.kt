package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.CostZoneMetric
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Currency

fun interface DoubleToCurrency {
    fun from(x: Double): Currency
}

class CurrencyMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: DoubleToCurrency) :
    CostZoneMetric {
    operator fun get(row: ZoneId, column: ZoneId): Currency = converter.from(translatedMatrix[row, column])

    override fun evaluate(origin: ZoneId, destination: ZoneId): Currency = this[origin, destination]
}
