package domain.shared.location.road

import core.datastructure.kdtree.ReadOnlyKDTree
import domain.LinkInfo
import domain.VisumNode
import domain.shared.location.Location
import domain.shared.location.attributes.HasRoadAccess
import edu.kit.ifv.JTSConverter
import org.jgrapht.Graph
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import kotlin.math.abs

/**
 * This class augments the [LinkInfo] class of the road network by calculating and storing the midpoint [midUTM] of
 * the origin and destination of the edge.
 */
private class LocatedLinkInfo(v: Point, u: Point, val edge: LinkInfo) {

    val midUTM = v.midPoint(u)

    fun Point.midPoint(other: Point): Point {
        val x = (x + other.x) / 2.0
        val y = (y + other.y) / 2.0
        return factory.createPoint(Coordinate(x, y))
    }
}

/**
 * A locatable graph is a representation of the road network, where in addition to the usual graph utility, a location
 * can be assigned to the closest road edge.
 */
class LocatableGraph(private val graph: Graph<VisumNode, LinkInfo>, private val srid: Int = 25832) :
    Graph<VisumNode, LinkInfo> by graph,
    VisumLinkIdLocator {
    /* Extract all edges from the road network and map them to the midpoint, for locating the closest edge.
       Since the input may be (very likely) a directed graph, an edge midpoint would be represented twice,
       thus filtering by the id removes duplicates and speeds up the search.
     */
    private val edgeSet = graph.edgeSet().map {
        graph.convertLink(it)
    }.groupBy { abs(it.edge.id?.toLong() ?: Long.MIN_VALUE) }.map { it.value.first() }

    private val edgeKdTree: ReadOnlyKDTree<LocatedLinkInfo> by lazy {
        ReadOnlyKDTree(
            edgeSet,
            { it.midUTM.x },
            { it.midUTM.y },

        )
    }

    fun readVisumLinkId(location: Location<HasRoadAccess>): Long = location.attributes.roadAccess.roadId

    fun visumLinkId(location: Location<*>): Long = linkIdFor(location)

    // TODO currently the calculation returns the closest midpoint, which does not necessarily represent the closest edge
    //  but it is good enough for approximation.
    override fun linkIdFor(location: Location<*>): Long {
        val utm = JTSConverter.convertPoint(location.position, srid)
        val edge = edgeKdTree.nearestNeighbor(utm) { doubleArrayOf(it.x, it.y) }
        return edge.edge.id?.toLong() ?: Long.MIN_VALUE
    }
}

private fun Graph<VisumNode, LinkInfo>.convertLink(linkInfo: LinkInfo): LocatedLinkInfo = LocatedLinkInfo(
    getEdgeSource(linkInfo).coordinate,
    getEdgeTarget(linkInfo).coordinate,
    linkInfo,
)
