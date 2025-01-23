package synthesis.fixedDestinations

import datastructure.ReadOnlyKDTree
import domain.location.DistanceMetric
import domain.location.Location
import domain.roadnetwork.toUTM
import synthesis.domain.SynthesisPerson
import units.Distance

/**
 * This is a preallocated locator strategy which returns the location with the smallest distance to agent, based on the
 * distance of coordinates.
 */
class UseClosestLocation(potentialLocations: List<Location>) : SimpleLocator<Any> {
    private val locationTree =
        ReadOnlyKDTree(potentialLocations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    override fun locate(
        agent: SynthesisPerson<out Any>,
    ): Location {
        return locationTree.nearestNeighbor(agent.homeLocation) {
            doubleArrayOf(
                it.coordinate.toUTM().e,
                it.coordinate.toUTM().n
            )
        }
    }
}

/**
 * In case that flight distance is not used, but metric is used instead. Cannot use KD-Trees, as the underlying "Metric"
 * may be asymmetrical.
 */
class MetricBasedClosestLocation(private val metric: DistanceMetric, private val locations: Collection<Location>): SimpleLocator<Any> {
    override fun locate(agent: SynthesisPerson<out Any>): Location {
        return locations.minBy { metric.evaluate(agent.homeLocation, it) }
    }
}