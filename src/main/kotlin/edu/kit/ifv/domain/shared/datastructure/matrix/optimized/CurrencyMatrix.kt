package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneIdMatrix
import edu.kit.ifv.domain.shared.location.zone.CostZoneMetric
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Currency

class CurrencyMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: DoubleToCurrency) :
    CostZoneMetric {
    operator fun get(row: ZoneId, column: ZoneId): Currency = converter.from(translatedMatrix[row, column])

    override fun evaluate(origin: ZoneId, destination: ZoneId): Currency = this[origin, destination]
}
