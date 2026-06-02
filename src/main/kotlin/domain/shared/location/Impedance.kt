package domain.shared.location

import domain.shared.enums.Mode
import domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import utils.units.Time
import kotlin.time.Duration

interface Impedance {

    fun cost(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Currency =
        costMetric(mode, time).evaluate(from, to)

    fun distance(from: HasZoneId, to: HasZoneId, mode: Mode): Distance = distanceMetric(mode).evaluate(from, to)

    fun duration(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Duration =
        durationMetric(mode, time).evaluate(from, to)

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}
