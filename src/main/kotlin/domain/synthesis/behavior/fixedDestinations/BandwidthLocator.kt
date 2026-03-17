package domain.synthesis.behavior.fixedDestinations

import core.datastructure.kdtree.WithMetric
import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.shared.location.LocationKDTree
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.behavior.domain.SynthesisPerson
import edu.kit.ifv.mobitopp.discretechoice.models.DiscreteChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.structure.RuleBasedStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.openMultinomialLogit
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.kilometers
import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

val standardBandwidthModel = RuleBasedStructure<
    WithMetric<StandardLocation, Distance>,
    LocationAlternative,
    BandwidthParameters
    > {
    ruleForAll { option, characteristics ->
        val (loc, distance) = option
        ln(characteristics.attractiveness(loc)) /
            (bDistance * distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))
    }
}.openMultinomialLogit("DefaultBandwidthLocationSelector")

//    DiscreteChoiceModel<Location, LocationAlternative, BandwidthParameters>(
//        AllocatedLogit.create {
//            ruleForAll {
//                ln(it.attractiveness) / (bDistance * it.distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))
//            }
//        },
//    )

/**
 * The bandwidth locator first determines which potential locations are valid targets by filtering the locations which
 * are within the commute distance of the agent +/- the poleRadius defined in the [BandwidthParameters]. If no locations
 * are within the band around the home location of the agent, all locations are considered valid.
 *
 * As second step a discrete choice model is used to determine the utility of each location individually. The input
 * for the discrete choice model can be found in [LocationAlternative]
 */
class BandwidthLocator(
    private val potentialLocations: List<StandardLocation>,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
    var parameters: BandwidthParameters = BandwidthParameters(), // TODO why variable?
    var model: DiscreteChoiceModel<WithMetric<StandardLocation, Distance>, LocationAlternative, BandwidthParameters> =
        standardBandwidthModel.build(parameters),
) : SimpleLocator<HasCommuteDistance> {
    private val locationTree = LocationKDTree(potentialLocations)

    @Suppress("MagicNumber")
    private val random = Random(42L) // TODO what is random source of opportunities?

    override fun locate(
        agent: SynthesisPerson<out HasCommuteDistance>,
    ): StandardLocation {
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
    fun validTargetsForAgent(agent: SynthesisPerson<out HasCommuteDistance>): Set<WithMetric<StandardLocation, Distance>> {
        val poleRadius = parameters.poleRadius
        return locationTree.sequenceFor(
            agent.homeLocation,
        )
            .dropWhile { it.item.distance(agent.homeLocation) <= agent.information.distanceWork - poleRadius }
            .takeWhile { it.item.distance(agent.homeLocation) <= agent.information.distanceWork + poleRadius }
            .toSet()
    }

}

data class BandwidthParameters(
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0,
)

/**
 * Contains all relevant information for the discrete choice within the [BandwidthLocator] to select a proper target.
 */
@Suppress("MagicNumber") // The small attractiveness as default seems to cause issues.
data class LocationAlternative(
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType
) {
    /**
     * We can extrapolate the attractiveness by simply evaluating the location.
     */
    fun attractiveness(location: StandardLocation) = location.zoneID.let {
        attractivenessModel.attractivenessFor(it, activityType)
    }
}
