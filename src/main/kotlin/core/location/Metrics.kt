package core.location

import domain.shared.enums.Mode
import units.Currency
import units.Distance
import utils.units.Time
import kotlin.time.Duration

interface Metrics {

    fun cost(from: Location, to: Location, mode: Mode, time: Time): Currency {
        return costMetric(mode, time).evaluate(from, to)
    }

    fun distance(from: Location, to: Location, mode: Mode): Distance {
        return distanceMetric(mode).evaluate(from, to)
    }

    fun duration(from: Location, to: Location, mode: Mode, time: Time): Duration {
        return durationMetric(mode, time).evaluate(from, to)
    }

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}
