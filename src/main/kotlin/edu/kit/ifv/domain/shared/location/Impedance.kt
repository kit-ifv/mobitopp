package edu.kit.ifv.domain.shared.location
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.utils.units.Time
import kotlin.time.Duration

interface Impedance {

    fun cost(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Currency = costMetric(
        mode,
        time,
    ).evaluate(from, to)

    fun distance(from: HasZoneId, to: HasZoneId, mode: Mode): Distance = distanceMetric(mode).evaluate(from, to)

    fun duration(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Duration = durationMetric(
        mode,
        time,
    ).evaluate(from, to)

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}

interface IndexAddressableImpedance: Impedance {
    fun costIndexed(fromIndex: Int, toIndex: Int, mode: Mode, time: Time): Double
    fun distanceIndexed(fromIndex: Int, toIndex: Int, mode: Mode): Double
    fun durationIndexed(fromIndex: Int, toIndex: Int, mode: Mode, time: Time): Double
}