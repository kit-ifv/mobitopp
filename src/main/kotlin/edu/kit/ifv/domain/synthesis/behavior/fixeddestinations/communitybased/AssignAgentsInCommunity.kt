package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

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
