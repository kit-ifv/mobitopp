package domain.shared.location

import domain.shared.enums.Mode
import domain.shared.location.zone.CostZoneMetric
import domain.shared.location.zone.DistanceZoneMetric
import domain.shared.location.zone.DurationZoneMetric
import domain.shared.location.zone.attributes.HasZoneId
import domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import utils.units.Time
import kotlin.time.Duration

interface ZoneImpedance : Impedance {
    override fun costMetric(mode: Mode, time: Time): CostZoneMetric
    override fun distanceMetric(mode: Mode): DistanceZoneMetric
    override fun durationMetric(mode: Mode, time: Time): DurationZoneMetric

    override fun cost(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Currency =
        cost(from.zoneId, to.zoneId, mode, time)

    override fun distance(from: HasZoneId, to: HasZoneId, mode: Mode): Distance = distance(from.zoneId, to.zoneId, mode)

    override fun duration(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Duration =
        duration(from.zoneId, to.zoneId, mode, time)

    fun cost(from: ZoneId, to: ZoneId, mode: Mode, time: Time): Currency = costMetric(mode, time).evaluate(from, to)
    fun distance(from: ZoneId, to: ZoneId, mode: Mode): Distance = distanceMetric(mode).evaluate(from, to)
    fun duration(from: ZoneId, to: ZoneId, mode: Mode, time: Time): Duration = durationMetric(
        mode,
        time,
    ).evaluate(from, to)
}
