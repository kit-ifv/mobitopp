package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.CostMetric
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Currency
import utils.Identifiable

fun interface DoubleToCurrency {
    fun from(x: Double): Currency
}

class CurrencyMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: DoubleToCurrency) :
    CostMetric {
    operator fun get(row: ZoneId, column: ZoneId): Currency = converter.from(translatedMatrix[row, column])

    override fun evaluate(origin: Identifiable<ZoneId>, destination: Identifiable<ZoneId>): Currency =
        this[origin.id, destination.id]
}
