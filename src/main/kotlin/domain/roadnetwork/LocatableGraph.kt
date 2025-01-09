package domain.roadnetwork

import datastructure.ReadOnlyKDTree
import domain.LinkInfo
import domain.Node
import domain.location.Location
import org.jgrapht.Graph
import units.Coordinate
import units.Distance
import units.DistanceUnit
import units.GPSCoordinate
import units.UTMPosition
import units.radians
import units.toDistance
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun interface VisumLinkIdLocator {
    // TODO change return type from Long to typed ID
    fun linkIdFor(location: Location): Long
}

/**
 * This class augments the [LinkInfo] class of the road network by calculating and storing the midpoint [midUTM] of
 * the origin and destination of the edge.
 */
private class LocatedLinkInfo(
    v: Coordinate,
    u: Coordinate,
    val edge: LinkInfo,

) {

    val midUTM = calculateMidpoint(v, u).toUTM()
    fun distanceToUTM(other: UTMPosition): Distance {
        return midUTM.distance(other)
    }
}
fun UTMPosition.distance(other: UTMPosition): Distance {
    return sqrt((e - other.e).pow(2) + (n - other.n).pow(2)).toDistance(DistanceUnit.METERS)
}

/**
 * A locatable graph is a representation of the road network, where in addition to the usual graph utility, a location
 * can be assigned to the closest road edge.
 */
class LocatableGraph(private val graph: Graph<Node, LinkInfo>) : Graph<Node, LinkInfo> by graph, VisumLinkIdLocator {
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
            { it.midUTM.e },
            { it.midUTM.n }

        )
    }

    fun visumLinkId(location: Location): Long {
        if (location.roadAccess != null) {
            return location.roadAccess.roadId
        }

        return linkIdFor(location)
    }

    // TODO currently the calculation returns the closest midpoint, which does not necessarily represent the closest edge
    //  but it is good enough for approximation.
    override fun linkIdFor(location: Location): Long {
        // Use UTM as baseline, WGS is imprecise, depending on location.
        val utm = GPSCoordinate.decimalDegree(
            location.coordinate.latitudeDegrees,
            location.coordinate.longitudeDegrees
        ).toUTM()
        val edge = edgeKdTree.find(utm) { doubleArrayOf(it.e, it.n) }
        return edge?.edge?.id?.toLong() ?: Long.MIN_VALUE
    }

    fun helpLinkId(location: Location): Long {
        return edgeSet.minBy { it.distanceToUTM(location.coordinate.toUTM()) }.edge.id?.toLong() ?: Long.MIN_VALUE
    }
}

fun Coordinate.toUTM(): UTMPosition {
    return GPSCoordinate.decimalDegree(latitudeDegrees, longitudeDegrees).toUTM()
}

private fun Graph<Node, LinkInfo>.convertLink(linkInfo: LinkInfo): LocatedLinkInfo {
    return LocatedLinkInfo(getEdgeSource(linkInfo).coordinate, getEdgeTarget(linkInfo).coordinate, linkInfo)
}

/**
 * Calculates the midpoint between two coordinates. Taken from https://stackoverflow.com/questions/4656802/midpoint-between-two-latitude-and-longitude
 *
 * @param coordinateFrom the starting coordinate
 * @param coordinateTo the ending coordinate
 * @return the midpoint coordinate
 */
fun calculateMidpoint(coordinateFrom: Coordinate, coordinateTo: Coordinate): GPSCoordinate {
    val lat1 = coordinateFrom.latitudeRadians.toDouble()
    val lat2 = coordinateTo.latitudeRadians.toDouble()
    val lon1 = coordinateFrom.longitudeRadians.toDouble()
    val lon2 = coordinateTo.longitudeRadians.toDouble()
    val dLon = lon2 - lon1
    val bX = cos(lat2) * cos(dLon)
    val bY = cos(lat2) * sin(dLon)
    val lat3 = atan2(sin(lat1) + sin(lat2), sqrt((cos(lat1) + bX) * (cos(lat1) + bX) + bY * bY))
    val lon3: Double = lon1 + atan2(bY, cos(lat1) + bX)

    return GPSCoordinate(lat3.radians, lon3.radians)
}
