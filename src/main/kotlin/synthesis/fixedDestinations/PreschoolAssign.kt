package synthesis.fixedDestinations

import datastructure.ReadOnlyKDTree
import datastructure.WithMetric
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.DistanceMetric
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.roadnetwork.toUTM
import modeling.discreteChoice.AllocatedLogit
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.DiscreteChoiceModel
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import units.toDistance
import usecases.AttractivenessModel
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.math.ln
import kotlin.math.pow


class UseClosestLocation(potentialLocations: List<Location>) : IndividualActivityLocator<Any>, GroupActivityLocator<Any> {
    private val locationTree =
        ReadOnlyKDTree(potentialLocations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    override fun find(
        individual: SynthesisPerson<out Any>,
        activityType: ActivityType
    ): Location {
        return locationTree.nearestNeighbor(individual.homeLocation) {
            doubleArrayOf(
                it.coordinate.toUTM().e,
                it.coordinate.toUTM().n
            )
        }
    }

    override fun find(
        group: Collection<SynthesisPerson<out Any>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out Any>, Location>> {
        return group.map { it to find(it, activityType) }
    }
}

data class BandwidthParameters(
    val poleDistance: Distance = 10.kilometers, // TODO this is an attribute of the person that needs to be extracted from the panel data
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0 // TODO sehr wahrscheinlich kilometer statt meter. Rausfinden
)

class LocationKDTree(locations: List<Location>) {
    private val tree = ReadOnlyKDTree(locations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    fun sequenceFor(location: Location): Sequence<WithMetric<Location, Distance>> {
        return tree.findUntil(
            location,
            { doubleArrayOf(it.coordinate.toUTM().e, it.coordinate.toUTM().n) },
            { it.toDistance(DistanceUnit.METERS) }
        )
    }
}

data class LocationSituation(
    override val choice: Location,
    val distance: Distance,
    val activityType: ActivityType
) : ChoiceSituation<Location>()

class UseBandwidthLocation(
    private val potentialLocations: List<Location>,
    val attractivenessModel: AttractivenessModel,
    val parameters: BandwidthParameters = BandwidthParameters()
) : IndividualActivityLocator<Any>, GroupActivityLocator<Any> {
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

    override fun find(
        individual: SynthesisPerson<out Any>,
        activityType: ActivityType
    ): Location {
        var validTargets =
            locationTree.sequenceFor(
                individual.homeLocation,
            )
                .dropWhile { it.item.distance(individual.homeLocation) <= parameters.poleDistance - parameters.poleRadius }
                .takeWhile { it.item.distance(individual.homeLocation) <= parameters.poleDistance + parameters.poleRadius }
                .toList()
        if (validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(individual.homeLocation) }
                .map { WithMetric(it, it.distance(individual.homeLocation)) }
        }
        val converted = validTargets.map { LocationSituation(it.item, it.metric, activityType) }.toSet()
        return model.select(converted, parameters)
    }

    override fun find(
        group: Collection<SynthesisPerson<out Any>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out Any>, Location>> {
        return group.map { it to find(it, activityType) }
    }
}


private fun Location.distance(other: Location) = coordinate.distance(other.coordinate)
