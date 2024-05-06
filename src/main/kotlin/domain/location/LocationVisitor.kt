package domain.location

import units.Currency
import units.Distance
import kotlin.time.Duration

/**
 * A LocationVisitor is a visitor for [Location] elements to treat them depending on their effective concrete type.
 *
 * A LocationVisitor can handle four different effective [Location] types:
 * - [Position]
 * - [ZoneLocation]
 * - [RoadPosition]
 * - [RoadPositionInZone]
 *
 * @param R the generic result type of the visitor
 * @property origin the origin [Location] used to evaluate the origin/destination pair.
 */
interface LocationVisitor<R> {
    val origin: Location

    /**
     * Visit the given destination [Location]
     *
     * @param destination the [Location] to be visited
     * @return the result of visiting the given destination [Location]
     */
    fun visit(destination: Location): R {
        return destination.accept(this)
    }

    /**
     * Visit the given destination [Position]
     *
     * @param destination the [Position] to be visited
     * @return the result of visiting the given destination [Position]
     */
    fun visit(destination: Position): R

    /**
     * Visit the given destination [RoadPosition]
     *
     * @param destination the [RoadPosition] to be visited
     * @return the result of visiting the given destination [RoadPosition]
     */
    fun visit(destination: RoadPosition): R

    /**
     * Visit the given destination [ZoneLocation]
     *
     * @param destination the [ZoneLocation] to be visited
     * @return the result of visiting the given destination [ZoneLocation]
     */
    fun visit(destination: ZoneLocation): R

    /**
     * Visit the given destination [RoadPositionInZone]
     *
     * @param destination the [RoadPositionInZone] to be visited
     * @return the result of visiting the given destination [RoadPositionInZone]
     */
    fun visit(destination: RoadPositionInZone): R
}

/**
 * A [LocationVisitor] with an origin [Position]
 */
interface FromPositionMetric<R> : LocationVisitor<R> {
    override val origin: Position
}

/**
 * A [LocationVisitor] with an origin [ZoneLocation]
 */
interface FromZoneMetric<R> : LocationVisitor<R> {
    override val origin: ZoneLocation
}

/**
 * A [LocationVisitor] with an origin [RoadPosition]
 */
interface FromRoadMetric<R> : LocationVisitor<R> {
    override val origin: RoadPosition
}

/**
 * A [LocationVisitor] with an origin [RoadPositionInZone]
 */
interface FromRoadInZoneMetric<R> : LocationVisitor<R> {
    override val origin: RoadPositionInZone
}

typealias DistanceMetric = LocationVisitor<Distance>

/**
 * A [DistanceMetric] with an origin [Position]
 */
interface DistanceFromPosition : DistanceMetric, FromPositionMetric<Distance>

/**
 * A [DistanceMetric] with an origin [ZoneLocation]
 */
interface DistanceFromZone : DistanceMetric, FromZoneMetric<Distance>

/**
 * A [DistanceMetric] with an origin [RoadPosition]
 */
interface DistanceFromRoad : DistanceMetric, FromRoadMetric<Distance>

/**
 * A [DistanceMetric] with an origin [RoadPositionInZone]
 */
interface DistanceFromRoadInZone : DistanceMetric, FromRoadInZoneMetric<Distance>

typealias DurationMetric = LocationVisitor<Duration>

/**
 * A [DurationMetric] with an origin [Position]
 */
interface DurationFromPosition : DurationMetric, FromPositionMetric<Duration>

/**
 * A [DurationMetric] with an origin [ZoneLocation]
 */
interface DurationFromZone : DurationMetric, FromZoneMetric<Duration>

/**
 * A [DurationMetric] with an origin [RoadPosition]
 */
interface DurationFromRoad : DurationMetric, FromRoadMetric<Duration>

/**
 * A [DurationMetric] with an origin [RoadPositionInZone]
 */
interface DurationFromRoadInZone : DurationMetric, FromRoadInZoneMetric<Duration>

typealias CostMetric = LocationVisitor<Currency>

/**
 * A [CostMetric] with an origin [Position]
 */
interface CostFromPosition : CostMetric, FromPositionMetric<Currency>

/**
 * A [CostMetric] with an origin [ZoneLocation]
 */
interface CostFromZone : CostMetric, FromZoneMetric<Currency>

/**
 * A [CostMetric] with an origin [RoadPosition]
 */
interface CostFromRoad : CostMetric, FromRoadMetric<Currency>

/**
 * A [CostMetric] with an origin [RoadPositionInZone]
 */
interface CostFromRoadInZone : CostMetric, FromRoadInZoneMetric<Currency>

/**
 * A [DistanceMetric] with an origin [Position] to compute the flight distance to a destination [Location].
 *
 * @property origin the origin [Position]
 */
class FlightDistance(
    override val origin: Position
) : DistanceFromPosition {
    override fun visit(destination: Position) = distanceTo(destination)

    override fun visit(destination: RoadPosition) = distanceTo(destination)

    override fun visit(destination: ZoneLocation) = distanceTo(destination)

    override fun visit(destination: RoadPositionInZone) = distanceTo(destination)

    private fun distanceTo(position: Position) = position.coordinate.distance(this.origin.coordinate)
}
