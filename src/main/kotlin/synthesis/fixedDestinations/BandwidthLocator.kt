package synthesis.fixedDestinations

import datastructure.WithMetric
import domain.enums.ActivityType
import domain.location.Location
import modeling.discreteChoice.AllocatedLogit
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.DiscreteChoiceModel
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import usecases.AttractivenessModel
import kotlin.math.ln
import kotlin.math.pow

class BandwidthLocator(
    private val potentialLocations: List<Location>,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
    val parameters: BandwidthParameters = BandwidthParameters()
) : SimpleLocator<CommuteDistance> {
    private val locationTree = LocationKDTree(potentialLocations)
    private val model =
        DiscreteChoiceModel<Location, LocationSituation, BandwidthParameters>(
            AllocatedLogit.create(potentialLocations) {
                ruleForAll {
                    val zoneId = it.choice.zone?.id

                    val attractiveness =
                        zoneId?.let { zId -> attractivenessModel.attractivenessFor(zId, it.activityType) }
                            ?: 0.000001.also { System.err.println("Cannot find attractiveness for location") }
                    ln(attractiveness) / (bDistance * it.distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))
                }
            }
        )

    override fun locate(
        agent: SynthesisPerson<out CommuteDistance>,
    ): Location {
        var validTargets =
            locationTree.sequenceFor(
                agent.homeLocation,
            )
                .dropWhile { it.item.distance(agent.homeLocation) <= agent.info.distanceWork - parameters.poleRadius }
                .takeWhile { it.item.distance(agent.homeLocation) <= agent.info.distanceWork + parameters.poleRadius }
                .toList()
        if (validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(agent.homeLocation) }
                .map { WithMetric(it, it.distance(agent.homeLocation)) }
        }
        val converted = validTargets.map { LocationSituation(it.item, it.metric, activityType) }.toSet()
        return model.select(converted, parameters)
    }

    private fun Location.distance(other: Location) = coordinate.distance(other.coordinate)
}

data class BandwidthParameters(
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0
)
private data class LocationSituation(
    override val choice: Location,
    val distance: Distance,
    val activityType: ActivityType
) : ChoiceSituation<Location>()