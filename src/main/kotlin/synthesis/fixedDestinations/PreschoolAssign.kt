package synthesis.fixedDestinations

import datastructure.ReadOnlyKDTree
import datastructure.WithMetric
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.Location
import domain.roadnetwork.toUTM
import modeling.discreteChoice.Logit
import modeling.discreteChoice.OtherDiscreteChoiceModel
import synthesis.ActivitySchedule
import synthesis.SurveyPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import units.toDistance
import usecases.AttractivenessModel
import kotlin.math.ln
import kotlin.math.pow

fun interface PreschoolAssign {
    fun assignPreschoolLocation(person: SurveyPerson, home: Location): FixedLocationOutput
}

fun interface LocationFinder {

    fun find(person: SurveyPerson, home: Location,activityType: ActivityType): Location
}

class UseClosestLocation(potentialLocations: List<Location>) : LocationFinder {
    private val locationTree =
        ReadOnlyKDTree(potentialLocations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    override fun find(
        person: SurveyPerson,
        home: Location,
        activityType: ActivityType
    ): Location {
        return locationTree.nearestNeighbor(home) { doubleArrayOf(it.coordinate.toUTM().e, it.coordinate.toUTM().n) }
    }

}

data class BandwidthParameters(
    val poleDistance: Distance = 10.kilometers, // TODO this is an attribute of the person that needs to be extracted from the panel data
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0 // TODO sehr wahrscheinlich kilometer statt meter. Rausfinden
)

private data class UtilityFunctionParameters(
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType
)

class LocationKDTree(locations: List<Location>) {
    private val tree = ReadOnlyKDTree(locations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    fun sequenceFor(location: Location): Sequence<WithMetric<Location, Distance>> {
        return tree.findUntil(
            location,
            { doubleArrayOf(it.coordinate.toUTM().e, it.coordinate.toUTM().n) },
            { it.toDistance(DistanceUnit.METERS) })
    }
}

class UseBandwidthLocation(
    private val potentialLocations: List<Location>, val attractivenessModel: AttractivenessModel,
    val parameters: BandwidthParameters
) : LocationFinder {
    private val locationTree = LocationKDTree(potentialLocations)
    private val model =
        OtherDiscreteChoiceModel<WithMetric<Location, Distance>, UtilityFunctionParameters>(Logit()) { x, p ->
            val attractiveness = p.run {
                x.item.zone?.let { attractivenessModel.attractivenessFor(it.id, activityType) }
                    ?: 0.0.also { System.err.println("Cannot find attractiveness for location ${x.item}") }
            }
            ln(attractiveness) / (parameters.bDistance * x.metric.toDouble(DistanceUnit.KILOMETERS)
                .pow(parameters.aDistance))

        }

    override fun find(
        person: SurveyPerson,
        home: Location,
        activityType: ActivityType
    ): Location {
        var validTargets =
            locationTree.sequenceFor(
                home,
            )
                .dropWhile { it.item.distance(home) <= parameters.poleDistance - parameters.poleRadius }
                .takeWhile { it.item.distance(home) <= parameters.poleDistance + parameters.poleRadius }.toList()
        if(validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(home) }.map{ WithMetric(it, it.distance(home)) }
        }


        //TODO code fallback if no location is found!!
        return model.select(validTargets.toSet(), UtilityFunctionParameters(attractivenessModel, activityType)).item

    }
}

private fun Location.distance(other: Location) = coordinate.distance(other.coordinate)

class ClosestDistanceAssigner(schools: List<Location>) : PreschoolAssign {
    private val schoolFinder: ReadOnlyKDTree<Location> =
        ReadOnlyKDTree(schools, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    override fun assignPreschoolLocation(person: SurveyPerson, home: Location): FixedLocationOutput {
        val closestSchool =
            schoolFinder.nearestNeighbor(home) { doubleArrayOf(home.coordinate.toUTM().e, home.coordinate.toUTM().n) }
        //TODO pass activity type
        return FixedLocationOutput(person, closestSchool, LegacyActivityType.EDUCATION_PRIMARY)
    }
}