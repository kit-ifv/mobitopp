package domain.shared.location

import domain.shared.location.attributes.HasZoneId
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
