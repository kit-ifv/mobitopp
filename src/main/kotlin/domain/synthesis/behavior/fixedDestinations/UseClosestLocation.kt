package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.DistanceMetric
import domain.shared.location.LocationKDTree
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * This is a preallocated locator strategy which returns the location with the smallest distance to agent, based on the
 * distance of coordinates.
 */
class UseClosestLocation(potentialLocations: List<StandardLocation>) : SimpleLocator<MinimumPersonAttributes> {
    private val locationTree = LocationKDTree(potentialLocations)
    override fun locate(
        agent: SurveyPerson<*>,
    ): StandardLocation {
        return locationTree.nearestNeighbor(agent.homeLocation)
    }
}

/**
 * In case that flight distance is not used, but metric is used instead. Cannot use KD-Trees, as the underlying "Metric"
 * may be asymmetrical.
 */
class MetricBasedClosestLocation(
    private val metric: DistanceMetric,
    private val locations: Collection<StandardLocation>
) : SimpleLocator<MinimumPersonAttributes> {
    override fun locate(agent: SurveyPerson<*>): StandardLocation {
        return locations.minBy { metric.evaluate(agent.homeLocation, it) }
    }
}
