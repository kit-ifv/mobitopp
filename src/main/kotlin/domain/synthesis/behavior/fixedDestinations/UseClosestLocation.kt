package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.DistanceMetric
import domain.shared.location.LocationOld
import domain.shared.location.LocationKDTree
import domain.synthesis.behavior.domain.SynthesisPerson

/**
 * This is a preallocated locator strategy which returns the location with the smallest distance to agent, based on the
 * distance of coordinates.
 */
class UseClosestLocation(potentialLocations: List<LocationOld>) : SimpleLocator<Any> {
    private val locationTree = LocationKDTree(potentialLocations)

    override fun locate(
        agent: SynthesisPerson<out Any>,
    ): LocationOld {
        return locationTree.nearestNeighbor(agent.homeLocation)
    }
}

/**
 * In case that flight distance is not used, but metric is used instead. Cannot use KD-Trees, as the underlying "Metric"
 * may be asymmetrical.
 */
class MetricBasedClosestLocation(
    private val metric: DistanceMetric,
    private val locations: Collection<LocationOld>
) : SimpleLocator<Any> {
    override fun locate(agent: SynthesisPerson<out Any>): LocationOld {
        return locations.minBy { metric.evaluate(agent.homeLocation, it) }
    }
}
