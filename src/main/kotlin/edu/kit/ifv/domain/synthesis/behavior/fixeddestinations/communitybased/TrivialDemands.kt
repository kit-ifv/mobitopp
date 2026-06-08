package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased
import edu.kit.ifv.domain.shared.location.DistanceMetric
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

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
