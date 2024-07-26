package domain.location

import domain.enums.Mode
import units.Currency
import units.Distance
import utils.units.Time
import kotlin.time.Duration

interface Metrics {

    fun cost(from: Location, to: Location, mode: Mode, time: Time): Currency {
        return costMetric(mode, time).let { from.evaluate(to, it) }
    }

    fun distance(from: Location, to: Location, mode: Mode): Distance {
        return distanceMetric(mode).let { from.evaluate(to, it) }
    }

    fun duration(from: Location, to: Location, mode: Mode, time: Time): Duration {
        return durationMetric(mode, time).let { from.evaluate(to, it) }
    }

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}
