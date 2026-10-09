package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.bandwidth

import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.DistanceZoneMetric
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.SimpleLocator
import edu.kit.ifv.mobitopp.discretechoice.models.DiscreteChoiceModel
import edu.kit.ifv.units.Distance
import edu.kit.ifv.utils.WithMetric
import kotlin.random.Random

/**
 * Selects a location whose distance from an agent's home zone lies within an
 * agent-specific bandwidth.
 *
 * Locations are ordered by their metric distance from each home zone. The
 * ordered lists are cached so that distances do not have to be recalculated
 * for every agent living in the same zone.
 *
 * For an agent with midpoint [agentBandwidthMidpointMeasurer], eligible
 * locations satisfy:
 *
 * ```
 * midpoint - parameters.poleRadius <= distance <= midpoint + parameters.poleRadius
 * ```
 *
 * If no location falls within this interval, [failureCallback] is invoked and
 * all available locations are passed to the discrete-choice model as a
 * fallback.
 *
 * @param potentialLocations Locations that may be selected.
 * @param distances Metric used to calculate distances between zones.
 * @param attractivenessModel Supplies the attractiveness of each location for
 * the selected [activityType].
 * @param activityType Activity for which a location is selected.
 * @param parameters Parameters used by the bandwidth choice model and for
 * determining the bandwidth radius.
 * @param model Discrete-choice model used to select among eligible locations.
 * @param failureCallback Called when the bandwidth contains no locations.
 * @param random Random-number generator used for the selection.
 * @param agentBandwidthMidpointMeasurer Determines the center of an agent's
 * distance bandwidth.
 *
 * @throws IllegalArgumentException if [potentialLocations] is empty or the
 * configured pole radius is negative.
 */
class MetricBandwidthLocator<T>(
    private val potentialLocations: List<StandardLocation>,
    private val distances: DistanceZoneMetric,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
    var parameters: BandwidthParameters = BandwidthParameters(),
    var model: DiscreteChoiceModel<WithMetric<StandardLocation, Distance>, LocationAlternative, BandwidthParameters> =
        standardBandwidthChoiceModel.build(
            parameters,
        ),
    private val failureCallback: (SurveyPerson<T>) -> Unit = {},
    private val random: Random,
    val agentBandwidthMidpointMeasurer: (SurveyPerson<T>) -> Distance,
) : SimpleLocator<T> where T : MinimumPersonAttributes {
    init {
        require(this.potentialLocations.isNotEmpty()) {
            "MetricBandwidthLocator requires at least one potential location."
        }

        require(parameters.poleRadius >= Distance.Companion.ZERO) {
            "The pole radius must not be negative: ${parameters.poleRadius}"
        }
    }
    private val cache: MutableMap<ZoneId, List<WithMetric<StandardLocation, Distance>>> = mutableMapOf()
    private val poleRadius get() = parameters.poleRadius
    private fun getOrInit(zoneId: ZoneId): List<WithMetric<StandardLocation, Distance>> = cache.getOrPut(zoneId) {
        potentialLocations.map { location ->
            WithMetric(location, distances.evaluate(zoneId, location.zoneId))
        }.sortedBy { it.metric }
    }
    override fun locate(agent: SurveyPerson<T>): StandardLocation {
        val locations = getOrInit(agent.homeLocation.zoneId)
        val agentDistance: Distance = agentBandwidthMidpointMeasurer(agent)
        val filteredLocations = locations
            .dropWhile { it.metric <= agentDistance - poleRadius }
            .takeWhile { it.metric <= agentDistance + poleRadius }
            .toSet()

        val validLocations = filteredLocations.ifEmpty { locations.toSet().also { failureCallback(agent) } }
        return context(LocationAlternative(attractivenessModel, activityType), random) {
            model.select(validLocations).item
        }
    }
}
