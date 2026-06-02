package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * Assign the agents of a given community a corresponding location.
 */
fun interface AssignAgentsInCommunity<T : MinimumPersonAttributes> {
    fun assign(communityDemandPlaner: CommunityDemandPlaner<T>): List<StandardLocation>

    /**
     * Convenience function to construct the wrapper for the Demand planner automatically.
     */
    fun assign(
        agents: Collection<SurveyPerson<T>>,
        demand: MutableCommunityDemand,
        potentialLocations: Collection<StandardLocation>,
    ) = assign(CommunityDemandPlaner(agents, demand, potentialLocations))
}