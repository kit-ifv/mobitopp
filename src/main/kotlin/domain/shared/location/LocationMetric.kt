package domain.shared.location

import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import kotlin.time.Duration

/**
 * A [LocationMetric] provides methods to compute a metric between origin and destination [LocationOld] elements.
 *
 * @param R the generic result type of the metrics result unit
 */
fun interface LocationMetric<R> {

    fun evaluate(origin: LocationOld, destination: LocationOld): R
}

typealias CostMetric = LocationMetric<Currency>

typealias DistanceMetric = LocationMetric<Distance>

typealias DurationMetric = LocationMetric<Duration>

class FlightDistance : DistanceMetric {

    override fun evaluate(origin: LocationOld, destination: LocationOld): Distance = TODO("swapping to coordinate makes this calculation inaccessible")
//        origin.coordinate.distance(destination.coordinate)
}

class ConstantMetric<R>(
    val value: R,
) : LocationMetric<R> {

    override fun evaluate(origin: LocationOld, destination: LocationOld): R = value
}
