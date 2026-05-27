package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.DistanceMetric
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.fixedDestinations.SimpleGroupLocator
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.abs
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

/**
 * Find the best location for a target agent based on the demand and available locations.
 */
fun interface BestLocationFromDemand<T : MinimumPersonAttributes> {
    fun bestLocation(
        agent: SurveyPerson<T>,
        demand: CommunityDemand,
        locations: Collection<StandardLocation>,
    ): StandardLocation
}

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

/**
 * An example implementation of assigning an agent a location: Use the location with the smallest possible distance,
 * which still has an unsaturated demand. If all demands are saturated, use the first
 */
class TrivialDemands<T : MinimumPersonAttributes>(private val metric: DistanceMetric) : AssignAgentsInCommunity<T> {
    override fun assign(communityDemandPlaner: CommunityDemandPlaner<T>): List<StandardLocation> =
        communityDemandPlaner.plan { a, dem, loc ->
            loc.sortedBy {
                metric.evaluate(a.homeLocation, it)
            }.firstOrNull { !dem.isSaturated(it) } ?: loc.first()
        }
}

/**
 * If a metric is present, the commuter distance can also be extracted using said metric.
 */
class MetricCommuterDistance<T>(private val metric: DistanceMetric) :
    CommuterDistance<T>() where T : HasCommuteDistance, T : MinimumPersonAttributes {

    override fun differenceToCommuteDistance(agent: SurveyPerson<T>, location: StandardLocation): Distance = abs(
        metric.evaluate(
            agent.homeLocation,
            location,
        ) - agent.attributes.distanceWork,
    )
}

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
