package domain.synthesis.behavior.fixeddestinations.bandwidth

import utils.WithMetric
import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.shared.location.jts.LocationKDTree
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.mobitopp.discretechoice.models.DiscreteChoiceModel
import edu.kit.ifv.units.Distance
import kotlin.random.Random

/**
 * The bandwidth locator first determines which potential locations are valid targets by filtering the locations which
 * are within the commute distance of the agent +/- the poleRadius defined in the [BandwidthParameters]. If no locations
 * are within the band around the home location of the agent, all locations are considered valid.
 *
 * As second step a discrete choice model is used to determine the utility of each location individually. The input
 * for the discrete choice model can be found in [LocationAlternative]
 */
class BandwidthLocator<T>(
    private val potentialLocations: List<StandardLocation>,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
    var parameters: BandwidthParameters = BandwidthParameters(),
    var model: DiscreteChoiceModel<WithMetric<StandardLocation, Distance>, LocationAlternative, BandwidthParameters> = standardBandwidthChoiceModel.build(
        parameters,
    ),
    private val randomSource: () -> Random = { Random(42) },
) : domain.synthesis.behavior.fixeddestinations.SimpleLocator<T> where T : HasCommuteDistance, T : MinimumPersonAttributes {
    private val locationTree = LocationKDTree(potentialLocations)

    @Suppress("MagicNumber")
    private val random = randomSource()

    override fun locate(agent: SurveyPerson<T>): StandardLocation {
        var validTargets =
            validTargetsForAgent(agent)
        if (validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(agent.homeLocation) }
                .map { WithMetric(it, it.distance(agent.homeLocation)) }.toSet()
        }

        return context(LocationAlternative(attractivenessModel, activityType), random) {
            model.select(validTargets).item
        }
    }

    /**
     * Determine which locations are within the band radius of an agents home location, using the [parameters] pole
     * radius.
     */

    fun validTargetsForAgent(agent: SurveyPerson<T>): Set<WithMetric<StandardLocation, Distance>> {
        val poleRadius = parameters.poleRadius
        return locationTree.sequenceFor(
            agent.homeLocation,
        )
            .dropWhile { it.item.distance(agent.homeLocation) <= agent.attributes.distanceWork - poleRadius }
            .takeWhile { it.item.distance(agent.homeLocation) <= agent.attributes.distanceWork + poleRadius }
            .toSet()
    }
}
