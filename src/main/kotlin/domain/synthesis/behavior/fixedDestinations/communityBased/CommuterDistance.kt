package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.abs

/**
 * This implementation of assigning an agent to a location takes in the stated commute distance of an agent and tries
 * to find the location which most closely matches the specified commute distance, while still having unsaturated demand.
 * If all demands are saturated, the best location without regard to saturation is used as a fallback.
 */
open class CommuterDistance<T> : AssignAgentsInCommunity<T> where T : HasCommuteDistance, T : MinimumPersonAttributes {
    override fun assign(communityDemandPlaner: CommunityDemandPlaner<T>): List<StandardLocation> =
        communityDemandPlaner.plan { agent, demand, locations ->
            require(locations.isNotEmpty()) {
                "Cannot assign a location from an empty location list $locations"
            }
            val filteredLocations = locations.filter { !demand.isSaturated(it) }
            if (filteredLocations.isEmpty()) {
                locations.minBy { differenceToCommuteDistance(agent, it) }
            } else {
                filteredLocations.minBy { differenceToCommuteDistance(agent, it) }
            }
        }

    open fun differenceToCommuteDistance(agent: SurveyPerson<T>, location: StandardLocation): Distance = abs(

        agent.homeLocation.distance(location) -
                agent.attributes.distanceWork,
    )
}