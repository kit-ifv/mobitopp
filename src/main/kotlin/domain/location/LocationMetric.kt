@file:Suppress("ComplexInterface", "TooManyFunctions", "MethodOverloading")

package domain.location

import units.Currency
import units.Distance
import kotlin.time.Duration

/**
 * A [LocationMetric] provides methods to compute a metric between origin and destination [Location] elements.
 *
 * A LocationVisitor can handle four different effective [Location] types:
 * - [Position]
 * - [ZoneLocation]
 * - [RoadPosition]
 * - [RoadPositionInZone]
 *
 * It provides evaluation methods for all combinations of two specific [Location] types.
 *
 * It is the receiver of the double dispatch pattern of the [Location] interface.
 *
 * @param R the generic result type of the visitor
 */
interface LocationMetric<R> {

    fun visit(origin: Position, destination: Position): R
    fun visit(origin: Position, destination: ZoneLocation): R
    fun visit(origin: Position, destination: RoadPosition): R
    fun visit(origin: Position, destination: RoadPositionInZone): R

    fun visit(origin: ZoneLocation, destination: Position): R
    fun visit(origin: ZoneLocation, destination: ZoneLocation): R
    fun visit(origin: ZoneLocation, destination: RoadPosition): R
    fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R

    fun visit(origin: RoadPosition, destination: Position): R
    fun visit(origin: RoadPosition, destination: ZoneLocation): R
    fun visit(origin: RoadPosition, destination: RoadPosition): R
    fun visit(origin: RoadPosition, destination: RoadPositionInZone): R

    fun visit(origin: RoadPositionInZone, destination: Position): R
    fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R
    fun visit(origin: RoadPositionInZone, destination: RoadPosition): R
    fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R
}

interface PositionLevelMetric<R> : LocationMetric<R> {

    fun evaluate(origin: Position, destination: Position): R

    override fun visit(origin: Position, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: RoadPositionInZone): R = evaluate(origin, destination)

    override fun visit(origin: ZoneLocation, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R = evaluate(origin, destination)

    override fun visit(origin: RoadPosition, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R = evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R = evaluate(origin, destination)
}

interface ZoneLevelMetric<R> : LocationMetric<R> {
    fun evaluate(origin: ZoneLocation, destination: ZoneLocation): R
    fun mapPosition(position: Position): ZoneLocation
    fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation

    override fun visit(origin: Position, destination: Position): R =
        evaluate(mapPosition(origin), mapPosition(destination))

    override fun visit(origin: Position, destination: ZoneLocation): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: Position, destination: RoadPosition): R =
        evaluate(mapPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: Position, destination: RoadPositionInZone): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: ZoneLocation, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R =
        evaluate(origin, destination)

    override fun visit(origin: ZoneLocation, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPosition, destination: Position): R =
        evaluate(mapRoadPosition(origin), mapPosition(destination))

    override fun visit(origin: RoadPosition, destination: ZoneLocation): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPosition, destination: RoadPosition): R =
        evaluate(mapRoadPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPositionInZone, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R =
        evaluate(origin, destination)
}

interface RoadPositionLevelMetric<R> : LocationMetric<R> {

    fun evaluate(origin: RoadPosition, destination: RoadPosition): R

    fun mapPosition(position: Position): RoadPosition

    fun mapZoneLocation(zoneLocation: ZoneLocation): RoadPosition

    override fun visit(origin: Position, destination: Position): R =
        evaluate(mapPosition(origin), mapPosition(destination))

    override fun visit(origin: Position, destination: ZoneLocation): R =
        evaluate(mapPosition(origin), mapZoneLocation(destination))

    override fun visit(origin: Position, destination: RoadPosition): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: Position, destination: RoadPositionInZone): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: ZoneLocation, destination: Position): R =
        evaluate(mapZoneLocation(origin), mapPosition(destination))

    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R =
        evaluate(mapZoneLocation(origin), mapZoneLocation(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPosition): R =
        evaluate(mapZoneLocation(origin), destination)

    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R =
        evaluate(mapZoneLocation(origin), destination)

    override fun visit(origin: RoadPosition, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: RoadPosition, destination: ZoneLocation): R =
        evaluate(origin, mapZoneLocation(destination))

    override fun visit(origin: RoadPosition, destination: RoadPosition): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R =
        evaluate(origin, mapZoneLocation(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R =
        evaluate(origin, destination)
}

interface RoadPositionInZoneLevelMetric<R> : LocationMetric<R> {

    fun evaluate(origin: RoadPositionInZone, destination: RoadPositionInZone): R
    fun mapZoneLocation(zoneLocation: ZoneLocation): RoadPositionInZone
    fun mapPosition(position: Position): RoadPositionInZone
    fun mapRoadPosition(roadPosition: RoadPosition): RoadPositionInZone

    override fun visit(origin: Position, destination: Position): R =
        evaluate(mapPosition(origin), mapPosition(destination))

    override fun visit(origin: Position, destination: ZoneLocation): R =
        evaluate(mapPosition(origin), mapZoneLocation(destination))

    override fun visit(origin: Position, destination: RoadPosition): R =
        evaluate(mapPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: Position, destination: RoadPositionInZone): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: ZoneLocation, destination: Position): R =
        evaluate(mapZoneLocation(origin), mapPosition(destination))

    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R =
        evaluate(mapZoneLocation(origin), mapZoneLocation(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPosition): R =
        evaluate(mapZoneLocation(origin), mapRoadPosition(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R =
        evaluate(mapZoneLocation(origin), destination)

    override fun visit(origin: RoadPosition, destination: Position): R =
        evaluate(mapRoadPosition(origin), mapPosition(destination))

    override fun visit(origin: RoadPosition, destination: ZoneLocation): R =
        evaluate(mapRoadPosition(origin), mapZoneLocation(destination))

    override fun visit(origin: RoadPosition, destination: RoadPosition): R =
        evaluate(mapRoadPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPositionInZone, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R =
        evaluate(origin, mapZoneLocation(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R =
        evaluate(origin, destination)
}

typealias CostMetric = LocationMetric<Currency>

typealias DistanceMetric = LocationMetric<Distance>

typealias DurationMetric = LocationMetric<Duration>

class FlightDistance : DistanceMetric, PositionLevelMetric<Distance> {

    override fun evaluate(origin: Position, destination: Position): Distance =
        origin.coordinate.distance(destination.coordinate)
}

class ConstantMetric<R>(
    val value: R,
) : PositionLevelMetric<R> {

    override fun evaluate(origin: Position, destination: Position): R = value
}
