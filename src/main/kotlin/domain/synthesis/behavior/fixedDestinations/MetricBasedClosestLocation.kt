package domain.synthesis.behavior.fixeddestinations

import domain.shared.location.DistanceMetric
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * In case that flight distance is not used, but metric is used instead. Cannot use KD-Trees, as the underlying "Metric"
 * may be asymmetrical.
 */
class MetricBasedClosestLocation(
    private val metric: DistanceMetric,
    private val locations: Collection<StandardLocation>,
) : SimpleLocator<MinimumPersonAttributes> {
    override fun locate(agent: SurveyPerson<*>): StandardLocation = locations.minBy {
        metric.evaluate(agent.homeLocation, it)
    }
}
