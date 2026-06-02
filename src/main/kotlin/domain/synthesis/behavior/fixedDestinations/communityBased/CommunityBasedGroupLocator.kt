package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.fixedDestinations.SimpleGroupLocator
import utils.collections.addProgressBar

/**
 * A community based locator groups the agents based on the community number of their home location, defined by the
 * demand and then assigns the work locations using a group locator for each group. Since the community demands are
 * independent of one another this strategy can simply filter the target locations based on whether a commute demand
 * to their community number exists. Feed this implementation with all locations that are suitable for your assignment
 */
class CommunityBasedGroupLocator<T : MinimumPersonAttributes>(
    val demands: CommuterDemandsMatrix,
    val strategy: AssignAgentsInCommunity<T>,
    potentialLocations: Collection<StandardLocation>,
) : SimpleGroupLocator<T> {

    private val filteredLocations: Collection<StandardLocation> = potentialLocations.filter { it.hasCommunityMapping() }
    override fun match(agents: Collection<SurveyPerson<T>>): List<StandardLocation> {
        val targets = agents.groupBy { it.homeLocation.toCommunity() }
        verifyDemand(targets.keys)
        verifyLocationsPresent(targets.keys)

        return targets.entries.addProgressBar(
            "Assigning demands for communities",
        ).flatMap { (communityNumber, agents) ->
            val demandsForCommunity = demands[communityNumber]
            val locationsInTargetCommunities = filteredLocations.filter { it.toCommunity() in demandsForCommunity }
            strategy.assign(agents, demandsForCommunity, locationsInTargetCommunities)
        }
    }

    private fun verifyDemand(targets: Collection<CommunityNumber>) {
        val badTargets = targets.filter { demands[it].isEmpty() }
        require(badTargets.isEmpty()) {
            "The following communities have agents in need of location assignment, but no associated " +
                "demand: ${badTargets.joinToString()}"
        }
    }

    private fun verifyLocationsPresent(targets: Collection<CommunityNumber>) {
        val badTargets = targets.filter { filteredLocations.none { loc -> loc.toCommunity() in demands[it] } }
        require(badTargets.isEmpty()) {
            "The following communities have demand, but no location is found in the" +
                " target communities: ${badTargets.joinToString()}"
        }
    }

    private fun StandardLocation.toCommunity(): CommunityNumber = demands.convert(this)

    private fun StandardLocation.hasCommunityMapping(): Boolean = runCatching { toCommunity() }.isSuccess
}