package synthesis.fixedDestinations

import domain.location.DistanceMetric
import domain.location.Location
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.DistanceUnit
import kotlin.math.abs

private typealias Agent<T> = SynthesisPerson<out T>
/**
 * A group locator assigns locations to a collection of agents instead of individually assigning locations.
 */
fun interface GroupLocator<T> {
    fun match(
        agents: Collection<Agent<T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<Agent<T>, Location>>
}


/**
 * A community based locator groups the agents based on the community number of their home location, defined by the
 * demand and then assigns the work locations using a group locator for each group. Since the community demands are
 * independent of one another this strategy can simply filter the target locations based on whether a commute demand
 * to their community number exists.
 */
class CommunityBasedGroupLocator<T>(
    val demands: CommuterDemandsMatrix,
    val strategy: AssignAgentsInCommunity<T>
) : GroupLocator<T> {
    override fun match(
        agents: Collection<Agent<T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<Agent<T>, Location>> {
        val targets = agents.groupBy { it.homeLocation.toCommunity() }
        return targets.flatMap { (communityNumber, agents) ->
            val demandsForCommunity = demands[communityNumber]
            val locationsInTargetCommunities = potentialLocations.filter { it.toCommunity() in demandsForCommunity }
            strategy.assign(agents, demandsForCommunity, locationsInTargetCommunities)
        }
    }

    private fun Location.toCommunity(): CommunityNumber {
        return demands.convert(this)
    }
}

/**
 * Assign the agents of a given community a corresponding location.
 */
fun interface AssignAgentsInCommunity<T> {
    fun assign(
        communityDemandPlaner: CommunityDemandPlaner<T>
    ): List<Pair<Agent<T>, Location>>

    /**
     * Convenience function to construct the wrapper for the Demand planner automatically.
     */
    fun assign(
        agents: Collection<Agent<T>>,
        demand: MutableCommunityDemand,
        potentialLocations: Collection<Location>
    ) = assign(CommunityDemandPlaner(agents, demand, potentialLocations))
}

/**
 * Find the best location for a target agent based on the demand and available locations.
 */
fun interface BestLocationFromDemand<T> {
    fun bestLocation(
        agent: Agent<T>,
        demand: CommunityDemand,
        locations: Collection<Location>
    ): Location
}

/**
 * Gather all agents in a communitynumber and get their demand, then try to assign stuff.
 */
data class CommunityDemandPlaner<T>(
    val agents: Collection<Agent<T>>,
    val demand: MutableCommunityDemand,
    val potentialLocations: Collection<Location>
) {
    /**
     * The standard function to a assign a location for each agent, find the best location as defined by the strategy
     * and decrease the demand in the community where the target location resides.
     */
    fun plan(
        strategy: BestLocationFromDemand<T> = BestLocationFromDemand { _, _, loc -> loc.first() }
    ): List<Pair<SynthesisPerson<out T>, Location>> {
        return agents.associateWith { agent ->

            val targetLocation = strategy.bestLocation(agent, demand, potentialLocations)
            demand.decreaseDemandFor(targetLocation)
            targetLocation
        }.toList()
    }
}


/**
 * An example implementation of assigning an agent a location: Use the location with the smallest possible distance,
 * which still has an unsaturated demand. If all demands are saturated, use the first
 */
class TrivialDemands<T>(private val metric: DistanceMetric) : AssignAgentsInCommunity<T> {
    override fun assign(
        communityDemandPlaner: CommunityDemandPlaner<T>
    ): List<Pair<SynthesisPerson<out T>, Location>> {

        return communityDemandPlaner.plan { a, dem, loc ->
            loc.sortedBy {
                metric.evaluate(it, a.homeLocation)
            }.firstOrNull { !dem.isSaturated(it) }?:loc.first()

        }

    }
}

/**
 * This implementation of assigning an agent to a location takes in the stated commute distance of an agent and tries
 * to find the location which most closely matches the specified commute distance, while still having unsaturated demand.
 * If all demands are saturated, the best location without regard to saturation is used as a fallback.
 */
class UsingCommuteDistance<T : CommuteDistance>(private val metric: DistanceMetric) : AssignAgentsInCommunity<T> {
    override fun assign(
        communityDemandPlaner: CommunityDemandPlaner<T>
    ): List<Pair<SynthesisPerson<out T>, Location>> {

        return communityDemandPlaner.plan { agent, demand, locations ->
            require(locations.isNotEmpty()) {
                "Cannot assign a location from an empty location list $locations"
            }
            val filteredLocations = locations.filter { !demand.isSaturated(it) }
            if (filteredLocations.isEmpty()) {
                println("Be advised that the demand for all Locations $locations is saturated. Agent $agent still requires a solution, we will use the best remaining demand")
                locations.minBy { differenceToCommuteDistance(agent, it) }
            } else {
                filteredLocations.minBy { differenceToCommuteDistance(agent, it) }
            }


        }

    }

    private fun differenceToCommuteDistance(agent: SynthesisPerson<out T>, location: Location): Double {
        return abs(
            (metric.evaluate(
                location,
                agent.homeLocation
            ) - agent.info.distanceWork).toDouble(DistanceUnit.METERS)
        )

    }
}







