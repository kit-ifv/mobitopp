package synthesis.fixedDestinations

import datastructure.ReadOnlyKDTree
import datastructure.WithMetric
import domain.enums.ActivityType
import domain.location.Location
import domain.roadnetwork.toUTM
import modeling.discreteChoice.AllocatedLogit
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.DiscreteChoiceModel
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import units.toDistance
import usecases.AttractivenessModel
import kotlin.math.ln
import kotlin.math.pow



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





