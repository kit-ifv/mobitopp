package domain.shared.location

import domain.shared.location.attributes.HasZoneID
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import kotlin.time.Duration

/**
 * A [LocationMetric] provides methods to compute a metric between origin and destination location elements.
 *
 * @param R the generic result type of the metrics result unit
 */
fun interface LocationMetric<R> {

    fun evaluate(origin: HasZoneID, destination: HasZoneID): R
}

typealias CostMetric = LocationMetric<Currency>

typealias DistanceMetric = LocationMetric<Distance>

typealias DurationMetric = LocationMetric<Duration>

class ConstantMetric<R>(
    val value: R,
) : LocationMetric<R> {

    override fun evaluate(origin: HasZoneID, destination: HasZoneID): R = value
}
