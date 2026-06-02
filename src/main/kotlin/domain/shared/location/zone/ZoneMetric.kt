package domain.shared.location.zone

import domain.shared.location.LocationMetric
import domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import kotlin.time.Duration

fun interface ZoneMetric<R> : LocationMetric<R> {
    override fun evaluate(origin: HasZoneId, destination: HasZoneId): R = evaluate(origin.zoneId, destination.zoneId)
    fun evaluate(origin: ZoneId, destination: ZoneId): R
}

typealias CostZoneMetric = ZoneMetric<Currency>

typealias DistanceZoneMetric = ZoneMetric<Distance>

typealias DurationZoneMetric = ZoneMetric<Duration>
