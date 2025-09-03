package domain.shared.location

import units.Currency
import units.Distance
import kotlin.time.Duration

/**
 * A [LocationMetric] provides methods to compute a metric between origin and destination [Location] elements.
 *
 * @param R the generic result type of the metrics result unit
 */
fun interface LocationMetric<R> {

    fun evaluate(origin: Location, destination: Location): R
}

typealias CostMetric = LocationMetric<Currency>

typealias DistanceMetric = LocationMetric<Distance>

typealias DurationMetric = LocationMetric<Duration>

class FlightDistance : DistanceMetric {

    override fun evaluate(origin: Location, destination: Location): Distance =
        origin.coordinate.distance(destination.coordinate)
}

class ConstantMetric<R>(
    val value: R,
) : LocationMetric<R> {

    override fun evaluate(origin: Location, destination: Location): R = value
}
