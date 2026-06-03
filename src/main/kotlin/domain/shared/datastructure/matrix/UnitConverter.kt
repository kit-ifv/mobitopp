package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.optimized.DoubleToCurrency
import domain.shared.datastructure.matrix.optimized.DoubleToDistance
import domain.shared.datastructure.matrix.optimized.DoubleToDuration
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.toCurrency
import edu.kit.ifv.units.toDistance
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Collects conversion functions for the 3 main types of matrices found in the simulation.
 *
 * Defaults:
 * - time in minutes
 * - distance in kilometers
 * - currency in euros
 */
data class UnitConverter(
    val timeConverter: DoubleToDuration = DoubleToDuration { it.minutes },
    val distanceConverter: DoubleToDistance = DoubleToDistance { it.kilometers },
    val currencyConverter: DoubleToCurrency = DoubleToCurrency { it.euros },

    ) {

    companion object {
        fun fromUnits(
            distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
            currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
            timeUnit: DurationUnit = DurationUnit.MINUTES,
        ): UnitConverter = UnitConverter(
            { it.toDuration(timeUnit) },
            { it.toDistance(distanceUnit) },
            { it.toCurrency(currencyUnit) },
        )
    }
}