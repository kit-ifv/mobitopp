package domain.shared.location

import domain.shared.enums.Mode
import domain.shared.location.attributes.HasZoneID
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import utils.units.Time
import kotlin.time.Duration

interface Metrics {

    fun cost(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time): Currency {
        return costMetric(mode, time).evaluate(from, to)
    }

    fun distance(from: HasZoneID, to: HasZoneID, mode: Mode): Distance {
        return distanceMetric(mode).evaluate(from, to)
    }

    fun duration(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time): Duration {
        return durationMetric(mode, time).evaluate(from, to)
    }

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}
