package domain.shared.location

import core.datastructure.kdtree.ReadOnlyKDTree
import utils.WithMetric
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.toDistance

/**
 * A KD-tree built by locations using the UTM Coordinate Representation.
 */
class LocationKDTree(locations: List<StandardLocation>) {
    private val tree = ReadOnlyKDTree(locations, { it.position.x }, { it.position.y })

    fun sequenceFor(location: StandardLocation): Sequence<WithMetric<StandardLocation, Distance>> = tree.findUntil(
        location,
        { doubleArrayOf(it.position.x, it.position.y) },
        { it.toDistance(DistanceUnit.METERS) },
    )

    fun nearestNeighbor(element: StandardLocation): StandardLocation = tree.findUntil(element, converter = {
        doubleArrayOf(
            it.position.x,
            it.position.y,
        )
    }).first().item
}
