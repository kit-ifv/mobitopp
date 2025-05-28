package synthesis.fixedDestinations

import datastructure.LocationKDTree
import datastructure.WithMetric
import discreteChoice.DiscreteChoiceModel
import discreteChoice.models.ChoiceAlternative
import discreteChoice.structure.RuleBasedStructure
import discreteChoice.utility.openMultinomialLogit
import domain.enums.ActivityType
import domain.location.Location
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import usecases.AttractivenessModel
import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

val standardBandwidthModel = RuleBasedStructure<Location, LocationAlternative, BandwidthParameters> {
    ruleForAll {
        ln(it.attractiveness) / (bDistance * it.distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))
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
    private val potentialLocations: List<Location>,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
    var parameters: BandwidthParameters = BandwidthParameters(), // TODO why variable?
    var model: DiscreteChoiceModel<Location, LocationAlternative, BandwidthParameters> =
        standardBandwidthModel.build(parameters),
) : SimpleLocator<CommuteDistance> {
    private val locationTree = LocationKDTree(potentialLocations)

    @Suppress("MagicNumber")
    private val random = Random(42L) // TODO what is random source of opportunities?

    override fun locate(
        agent: SynthesisPerson<out CommuteDistance>,
    ): Location {
        var validTargets =
            validTargetsForAgent(agent)
        if (validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(agent.homeLocation) }
                .map { WithMetric(it, it.distance(agent.homeLocation)) }
        }
        val converted =
            validTargets.map { LocationAlternative(it.item, it.metric, attractivenessModel, activityType) }.toSet()
        return model.select(converted, random)
    }

    /**
     * Determine which locations are within the band radius of an agents home location, using the [parameters] pole
     * radius.
     */
    fun validTargetsForAgent(agent: SynthesisPerson<out CommuteDistance>) =
        locationTree.sequenceFor(
            agent.homeLocation,
        )
            .dropWhile { it.item.distance(agent.homeLocation) <= agent.info.distanceWork - parameters.poleRadius }
            .takeWhile { it.item.distance(agent.homeLocation) <= agent.info.distanceWork + parameters.poleRadius }
            .toList()

    private fun Location.distance(other: Location) = coordinate.distance(other.coordinate)
}

data class BandwidthParameters(
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0
)

/**
 * Contains all relevant information for the discrete choice within the [BandwidthLocator] to select a proper target.
 */
@Suppress("MagicNumber") // The small attractiveness as default seems to cause issues.
data class LocationAlternative(
    override val choice: Location,
    val distance: Distance,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType
) : ChoiceAlternative<Location>() {
    /**
     * We can extrapolate the attractiveness by simply evaluating the location.
     */
    val attractiveness = choice.zone?.id?.let {
        attractivenessModel.attractivenessFor(it, activityType)
    } ?: 0.00001
}
