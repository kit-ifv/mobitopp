package domain.shared.location

import core.datastructure.kdtree.ReadOnlyKDTree
import core.datastructure.kdtree.WithMetric
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.toDistance

/**
 * A KD-tree built by locations using the UTM Coordinate Representation.
 * TODO test that this datastructure works with very distant points in UTM
 */
class LocationKDTree(locations: List<StandardLocation>) {
    private val tree = ReadOnlyKDTree(locations, { it.position.x }, { it.position.y })

    fun sequenceFor(location: StandardLocation): Sequence<WithMetric<StandardLocation, Distance>> {
        return tree.findUntil(
            location,
            { doubleArrayOf(it.position.x, it.position.y) },
            { it.toDistance(DistanceUnit.METERS) }
        )
    }

    fun nearestNeighbor(element: StandardLocation): StandardLocation {
        return tree.findUntil(element, converter = {
            doubleArrayOf(
                it.position.x,
                it.position.y
            )
        }).first().item
    }
}
