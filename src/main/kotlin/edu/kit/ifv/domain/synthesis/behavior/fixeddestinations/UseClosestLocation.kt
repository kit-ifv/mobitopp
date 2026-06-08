package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.jts.LocationKDTree
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

/**
 * This is a preallocated locator strategy which returns the location with the smallest distance to agent, based on the
 * distance of coordinates.
 */
class UseClosestLocation(potentialLocations: List<StandardLocation>) : SimpleLocator<MinimumPersonAttributes> {
    private val locationTree = LocationKDTree(potentialLocations)
    override fun locate(agent: SurveyPerson<*>): StandardLocation = locationTree.nearestNeighbor(agent.homeLocation)
}
