package domain.shared.location

import domain.shared.enums.Mode
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import utils.units.Time
import kotlin.time.Duration

interface Metrics {

    fun cost(from: HasZone, to: HasZone, mode: Mode, time: Time): Currency {
        return costMetric(mode, time).evaluate(from, to)
    }

    fun distance(from: HasZone, to: HasZone, mode: Mode): Distance {
        return distanceMetric(mode).evaluate(from, to)
    }

    fun duration(from: HasZone, to: HasZone, mode: Mode, time: Time): Duration {
        return durationMetric(mode, time).evaluate(from, to)
    }

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}
