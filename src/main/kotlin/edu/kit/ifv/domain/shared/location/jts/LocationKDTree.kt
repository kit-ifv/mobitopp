package edu.kit.ifv.domain.shared.location.jts
import edu.kit.ifv.core.datastructure.kdtree.ReadOnlyKDTree
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.toDistance
import edu.kit.ifv.utils.WithMetric

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
