package domain.location

import domain.data.ZoneData
import units.Currency
import units.Distance
import utils.units.Coordinate
import utils.units.GPSCoordinate
import utils.units.UnitIntervalValue
import utils.units.share
import kotlin.time.Duration

/**
 * Location is a generic representation of placement/whereabouts of entities.
 */
interface Location {

    /**
     * Compute the distance from this location to the given other location
     * by using the given [DistanceMetric].
     *
     * @param other the [Location] to which the distance is computed
     * @param metric the [DistanceMetric] to be used to compute the distance
     */
    fun distance(other: Location, metric: (Location) -> DistanceMetric): Distance = metric(this).visit(other)

    /**
     * Compute the travel duration from this location to the given other location
     * by using the given [DurationMetric].
     *
     * @param other the [Location] to which the duration is computed
     * @param metric the [DurationMetric] to be used to compute the distance
     */
    fun duration(other: Location, metric: (Location) -> DurationMetric): Duration = metric(this).visit(other)
    // Current time and mode of transportation could be encoded in metric / visitor?

    /**
     * Compute the cost of traveling from this location to the given other location
     * by using the given [CostMetric].
     *
     * @param other the [Location] to which the cost is computed
     * @param metric the [CostMetric] to be used to compute the distance
     */
    fun cost(other: Location, metric: (Location) -> CostMetric): Currency = metric(this).visit(other)

    /**
     * Accept the given [LocationVisitor] by letting it visit this [Location].
     *
     * @param visitor the visitor to be accepted
     * @param R the generic result type of the [LocationVisitor]
     * @return the result of the visitor visiting this [Location]
     */
    fun <R> accept(visitor: LocationVisitor<R>): R
}

/**
 * A Position is a [Location] with a specific coordinate.
 *
 * @property coordinate the coordinate of the location
 */
interface Position : Location {
    val coordinate: Coordinate

    /**
     * Accept the given [LocationVisitor] by letting it visit this [Position].
     *
     * @param visitor the visitor to be accepted
     * @param R the generic result type of the [LocationVisitor]
     * @return the result of the visitor visiting this [Position]
     */
    override fun <R> accept(visitor: LocationVisitor<R>): R = visitor.visit(this)
}

/**
 * A ZoneLocation is a [Position] within a certain zone.
 *
 * @property zone the zone containing the location
 */
interface ZoneLocation : Position {
    val zone: ZoneData
    // TODO this is linked to domain description which might change in the future
    //  -> rethink this part (imported from  legacy mobitopp)
    // TODO maybe extract minimum interface for zones
    /**
     * Accept the given [LocationVisitor] by letting it visit this [ZoneLocation].
     *
     * @param visitor the visitor to be accepted
     * @param R the generic result type of the [LocationVisitor]
     * @return the result of the visitor visiting this [ZoneLocation]
     */
    override fun <R> accept(visitor: LocationVisitor<R>): R = visitor.visit(this)
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

    /**
     * Accept the given [LocationVisitor] by letting it visit this [RoadPosition].
     *
     * @param visitor the visitor to be accepted
     * @param R the generic result type of the [LocationVisitor]
     * @return the result of the visitor visiting this [RoadPosition]
     */
    override fun <R> accept(visitor: LocationVisitor<R>): R = visitor.visit(this)
}

/**
 * A RoadPositionINZone is a [RoadPosition] with additional [ZoneLocation] information.
 *
 * @param roadPosition the [RoadPosition] contained by the given zone
 * @property zone the zone containing the location
 */
class RoadPositionInZone(
    roadPosition: RoadPosition,
    override val zone: ZoneData
) : RoadPosition by roadPosition, ZoneLocation {

    /**
     * Accept the given [LocationVisitor] by letting it visit this [RoadPositionInZone].
     *
     * @param visitor the visitor to be accepted
     * @param R the generic result type of the [LocationVisitor]
     * @return the result of the visitor visiting this [RoadPositionInZone]
     */
    override fun <R> accept(visitor: LocationVisitor<R>): R = visitor.visit(this)
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
        override val coordinate = GPSCoordinate.degrees(res[1].toDouble(), res[0].toDouble())
    }
}
