package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.DistanceMetric
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.abs

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
