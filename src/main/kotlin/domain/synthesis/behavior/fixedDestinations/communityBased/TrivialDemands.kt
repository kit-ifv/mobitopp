package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.DistanceMetric
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes

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