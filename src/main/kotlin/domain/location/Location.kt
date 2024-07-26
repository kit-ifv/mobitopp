package domain.location

import domain.data.Zone
import units.Coordinate
import units.GPSCoordinate
import units.UnitIntervalValue
import units.share

/**
 * Location is a generic representation of placement/whereabouts of entities.
 * It provides a method to evaluate a [LocationMetric] between two arbitrary [Location]s.
 *
 * Here the double-dispatch pattern is used, since there are four different specific types of locations.
 *  - [Position]
 *  - [ZoneLocation]
 *  - [RoadPosition]
 *  - [RoadPositionInZone]
 *
 * [LocationMetric]s can evaluate any combination of two specific location types.
 * To call the correct evaluation method of the [LocationMetric], first [Location.evaluate] knows the specific
 * [Location] type of the callee  and passes it to the corresponding version of [Location.evaluateFrom].
 * [Location.evaluateFrom] is called on the destination [Location], hence theis secend callee now nws the specific
 * [Location] type of the destination (itself) and the origin [Location] (the first callee which is given as parameter).
 */
interface Location {

    fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R
    fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R
    fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R
    fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R
    fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R
}

/**
 * A Position is a [Location] with a specific coordinate.
 *
 * @property coordinate the coordinate of the location
 */
interface Position : Location {
    val coordinate: Coordinate

    override fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R =
        destination.evaluateFrom(this, metric)

    override fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R =
        metric.visit(origin, this)
}

/**
 * A ZoneLocation is a [Position] within a certain zone.
 *
 * @property zone the zone containing the location
 */
interface ZoneLocation : Position {
    val zone: Zone
    // TODO this is linked to domain description which might change in the future
    //  -> rethink this part (imported from  legacy mobitopp)
    // TODO maybe extract minimum interface for zones or refencece only zone id

    override fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R =
        destination.evaluateFrom(this, metric)

    override fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R =
        metric.visit(origin, this)
}

/**
 * A RoadPosition is a [Position] located on or at a certain road.
 *
 * @property road the road (id) on/at which the location lies
 * @property roadAccess the access point along the road, specified by a percentage value in [0,1]
 */
interface RoadPosition : Position {
    val road: Long // TODO replace by link id
    val roadAccess: UnitIntervalValue

    override fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R =
        destination.evaluateFrom(this, metric)

    override fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R =
        metric.visit(origin, this)
}

/**
 * A RoadPositionInZone is a [RoadPosition] with additional [ZoneLocation] information.
 *
 * @param roadPosition the [RoadPosition] contained by the given zone
 * @property zone the zone containing the location
 */
class RoadPositionInZone(
    roadPosition: RoadPosition,
    override val zone: Zone
) : RoadPosition by roadPosition, ZoneLocation {

    override fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R =
        destination.evaluateFrom(this, metric)

    override fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R =
        metric.visit(origin, this)

    override fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R =
        metric.visit(origin, this)
}

/**
 * Parse the legacy mobiTopp String format of [RoadPosition]:
 *
 * @return the parsed [RoadPosition]
 */
@Suppress("MagicNumber")
fun String.parseRoadPosition(): RoadPosition {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    require(res.size == 4)

    return object : RoadPosition {
        override val road = res[2].toLong()
        override val roadAccess = res[3].toDouble().share()
        override val coordinate = GPSCoordinate.decimalDegree(res[1].toDouble(), res[0].toDouble())
    }
}

object LOCATIONUNKNOWN : Location {
    private const val message = "Distance, cost and duration should never be called on LOCATIONUNKNOWN!"
    override fun <R> evaluate(destination: Location, metric: LocationMetric<R>): R = error(message)

    override fun <R> evaluateFrom(origin: Position, metric: LocationMetric<R>): R = error(message)

    override fun <R> evaluateFrom(origin: ZoneLocation, metric: LocationMetric<R>): R = error(message)

    override fun <R> evaluateFrom(origin: RoadPosition, metric: LocationMetric<R>): R = error(message)

    override fun <R> evaluateFrom(origin: RoadPositionInZone, metric: LocationMetric<R>): R = error(message)
}

data class ZoneLocationImpl(override val coordinate: Coordinate, override val zone: Zone) : ZoneLocation
