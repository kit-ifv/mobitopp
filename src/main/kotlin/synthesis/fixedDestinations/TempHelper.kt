package synthesis.fixedDestinations

import domain.location.DistanceMetric
import domain.location.Location
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.DistanceUnit
import kotlin.math.abs

private typealias Agent<T> = SynthesisPerson<out T>
/**
 * TODO very similar to Groupbased activity locator
 */
fun interface MatchAgentsToLocations<T> {
    fun match(
        agents: Collection<Agent<T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<Agent<T>, Location>>
}







class TempLocationSorter<T>(
    val demands: CommuterDemandsMatrix,
    val strategy: AssignAgentsInCommunity<T>
) : MatchAgentsToLocations<T> {
    override fun match(
        agents: Collection<Agent<T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<Agent<T>, Location>> {
        val targets = agents.groupBy { it.homeLocation.toCommunity() }
        return targets.flatMap { (communityNumber, agents) ->
            val concreteDemands = demands[communityNumber]
            val concreteLocations = potentialLocations.filter { it.toCommunity() in concreteDemands }
            strategy.assign(agents, concreteDemands, concreteLocations)
        }
    }

    private fun Location.toCommunity(): CommunityNumber {
        return demands.convert(this)
    }
}

fun interface AssignAgentsInCommunity<T> {
    fun assign(
        communityDemandPlaner: CommunityDemandPlaner<T>
    ): List<Pair<Agent<T>, Location>>

    fun assign(
        agents: Collection<Agent<T>>,
        demand: MutableCommunityDemand,
        potentialLocations: Collection<Location>
    ) = assign(CommunityDemandPlaner(agents, demand, potentialLocations))
}


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
    fun plan(
        lambda: BestLocationFromDemand<T> = BestLocationFromDemand { _, _, loc -> loc.first() }
    ): List<Pair<SynthesisPerson<out T>, Location>> {
        return agents.associateWith { agent ->

            val targetLocation = lambda.bestLocation(agent, demand, potentialLocations)
            demand.decreaseDemandFor(targetLocation)
            targetLocation
        }.toList()
    }
}



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







