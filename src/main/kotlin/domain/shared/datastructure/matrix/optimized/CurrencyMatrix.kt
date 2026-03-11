package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.CostMetric
import domain.shared.location.LocationOld
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Currency

fun interface DoubleToCurrency {
    fun from(x: Double): Currency
}

class CurrencyMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: DoubleToCurrency,
) : CostMetric {
    operator fun get(row: ZoneId, column: ZoneId): Currency {
        return converter.from(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: LocationOld,
        destination: LocationOld,
    ): Currency {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}
