package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * Gather all agents in a communitynumber and get their demand, then try to assign stuff.
 */
data class CommunityDemandPlaner<T : MinimumPersonAttributes>(
    val agents: Collection<SurveyPerson<T>>,
    val demand: MutableCommunityDemand,
    val potentialLocations: Collection<StandardLocation>,
) {
    /**
     * The standard function to assign a location for each agent, find the best location as defined by the strategy
     * and decrease the demand in the community where the target location resides.
     */
    fun plan(strategy: BestLocationFromDemand<T>): List<StandardLocation> {
        val size = agents.size
        if (size > demand.total) {
            System.err.println(
                "\nThe amount of agents ($size) to be assigned in community ${demand.communityID} " +
                    "exceeds the the total demand ${demand.total}. There will be inaccuracies in assignment",
            )
        }
        return agents.map { agent ->
            val targetLocation = strategy.bestLocation(agent, demand, potentialLocations)
            demand.decreaseDemandFor(targetLocation)
            targetLocation
        }
    }
}