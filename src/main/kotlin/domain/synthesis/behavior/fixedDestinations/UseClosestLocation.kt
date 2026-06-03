package domain.synthesis.behavior.fixeddestinations

import domain.shared.location.StandardLocation
import domain.shared.location.jts.LocationKDTree
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * This is a preallocated locator strategy which returns the location with the smallest distance to agent, based on the
 * distance of coordinates.
 */
class UseClosestLocation(potentialLocations: List<StandardLocation>) : SimpleLocator<MinimumPersonAttributes> {
    private val locationTree = LocationKDTree(potentialLocations)
    override fun locate(agent: SurveyPerson<*>): StandardLocation = locationTree.nearestNeighbor(agent.homeLocation)
}
