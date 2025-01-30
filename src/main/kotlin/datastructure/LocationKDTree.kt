package datastructure

import domain.location.Location
import domain.roadnetwork.toUTM
import units.Distance
import units.DistanceUnit
import units.toDistance

/**
 * A KD-tree built by locations using the UTM Coordinate Representation.
 * TODO test that this datastructure works with very distant points in UTM
 */
class LocationKDTree(locations: List<Location>) {
    private val tree = ReadOnlyKDTree(locations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    fun sequenceFor(location: Location): Sequence<WithMetric<Location, Distance>> {
        return tree.findUntil(
            location,
            { doubleArrayOf(it.coordinate.toUTM().e, it.coordinate.toUTM().n) },
            { it.toDistance(DistanceUnit.METERS) }
        )
    }

    fun nearestNeighbor(element: Location): Location {
        return tree.findUntil(element, converter = {
            doubleArrayOf(
                it.coordinate.toUTM().e,
                it.coordinate.toUTM().n
            )
        }).first().item
    }
}
