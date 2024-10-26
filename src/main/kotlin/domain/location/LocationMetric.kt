@file:Suppress("ComplexInterface", "TooManyFunctions", "MethodOverloading")

package domain.location

import domain.data.Zone
import domain.data.asLocation
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
    fun visit(origin: Position, destination: Zone): R

    fun visit(origin: ZoneLocation, destination: Position): R
    fun visit(origin: ZoneLocation, destination: ZoneLocation): R
    fun visit(origin: ZoneLocation, destination: RoadPosition): R
    fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R
    fun visit(origin: ZoneLocation, destination: Zone): R

    fun visit(origin: RoadPosition, destination: Position): R
    fun visit(origin: RoadPosition, destination: ZoneLocation): R
    fun visit(origin: RoadPosition, destination: RoadPosition): R
    fun visit(origin: RoadPosition, destination: RoadPositionInZone): R
    fun visit(origin: RoadPosition, destination: Zone): R

    fun visit(origin: RoadPositionInZone, destination: Position): R
    fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R
    fun visit(origin: RoadPositionInZone, destination: RoadPosition): R
    fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R
    fun visit(origin: RoadPositionInZone, destination: Zone): R

    fun visit(origin: Zone, destination: Position): R
    fun visit(origin: Zone, destination: ZoneLocation): R
    fun visit(origin: Zone, destination: RoadPosition): R
    fun visit(origin: Zone, destination: RoadPositionInZone): R
    fun visit(origin: Zone, destination: Zone): R
}

interface PositionLevelMetric<R> : LocationMetric<R> {

    fun evaluate(origin: Position, destination: Position): R

    override fun visit(origin: Position, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: RoadPositionInZone): R = evaluate(origin, destination)
    override fun visit(origin: Position, destination: Zone): R = evaluate(origin, destination.centroid)

    override fun visit(origin: ZoneLocation, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R = evaluate(origin, destination)
    override fun visit(origin: ZoneLocation, destination: Zone): R = evaluate(origin, destination.centroid)

    override fun visit(origin: RoadPosition, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R = evaluate(origin, destination)
    override fun visit(origin: RoadPosition, destination: Zone): R = evaluate(origin, destination.centroid)

    override fun visit(origin: RoadPositionInZone, destination: Position): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R = evaluate(origin, destination)
    override fun visit(origin: RoadPositionInZone, destination: Zone): R = evaluate(origin, destination.centroid)

    override fun visit(origin: Zone, destination: Position): R = evaluate(origin.centroid, destination)
    override fun visit(origin: Zone, destination: ZoneLocation): R = evaluate(origin.centroid, destination)
    override fun visit(origin: Zone, destination: RoadPosition): R = evaluate(origin.centroid, destination)
    override fun visit(origin: Zone, destination: RoadPositionInZone): R = evaluate(origin.centroid, destination)
    override fun visit(origin: Zone, destination: Zone): R = evaluate(origin.centroid, destination.centroid)
}

interface ZoneLevelMetric<R> : LocationMetric<R> {
    fun evaluate(origin: ZoneLocation, destination: ZoneLocation): R
    fun mapPosition(position: Position): ZoneLocation
    fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation
    fun mapZone(zone: Zone): ZoneLocation = zone.asLocation()

    override fun visit(origin: Position, destination: Position): R =
        evaluate(mapPosition(origin), mapPosition(destination))

    override fun visit(origin: Position, destination: ZoneLocation): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: Position, destination: RoadPosition): R =
        evaluate(mapPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: Position, destination: RoadPositionInZone): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: Position, destination: Zone): R =
        evaluate(mapPosition(origin), mapZone(destination))

    override fun visit(origin: ZoneLocation, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R =
        evaluate(origin, destination)

    override fun visit(origin: ZoneLocation, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R =
        evaluate(origin, destination)

    override fun visit(origin: ZoneLocation, destination: Zone): R =
        evaluate(origin, mapZone(destination))

    override fun visit(origin: RoadPosition, destination: Position): R =
        evaluate(mapRoadPosition(origin), mapPosition(destination))

    override fun visit(origin: RoadPosition, destination: ZoneLocation): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPosition, destination: RoadPosition): R =
        evaluate(mapRoadPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPosition, destination: Zone): R =
        evaluate(mapRoadPosition(origin), mapZone(destination))

    override fun visit(origin: RoadPositionInZone, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R =
        evaluate(origin, destination)

    override fun visit(origin: RoadPositionInZone, destination: Zone): R =
        evaluate(origin, mapZone(destination))

    override fun visit(origin: Zone, destination: Position): R =
        evaluate(mapZone(origin), mapPosition(destination))

    override fun visit(origin: Zone, destination: ZoneLocation): R =
        evaluate(mapZone(origin), destination)

    override fun visit(origin: Zone, destination: RoadPosition): R =
        evaluate(mapZone(origin), mapRoadPosition(destination))

    override fun visit(origin: Zone, destination: RoadPositionInZone): R =
        evaluate(mapZone(origin), destination)

    override fun visit(origin: Zone, destination: Zone): R =
        evaluate(mapZone(origin), mapZone(destination))
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

interface ZoneMetric<R> : LocationMetric<R> {
    fun evaluate(origin: Zone, destination: Zone): R
    fun mapZoneLocation(origin: ZoneLocation): Zone = origin.zone
    fun mapPosition(origin: Position): Zone
    fun mapRoadPosition(origin: RoadPosition): Zone
    fun mapRoadPositionZone(roadPositionInZone: RoadPositionInZone): Zone = roadPositionInZone.zone

    override fun visit(origin: Position, destination: Position): R =
        evaluate(mapPosition(origin), mapPosition(destination))

    override fun visit(origin: Position, destination: ZoneLocation): R =
        evaluate(mapPosition(origin), mapZoneLocation(destination))

    override fun visit(origin: Position, destination: RoadPosition): R =
        evaluate(mapPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: Position, destination: RoadPositionInZone): R =
        evaluate(mapPosition(origin), mapRoadPositionZone(destination))

    override fun visit(origin: Position, destination: Zone): R =
        evaluate(mapPosition(origin), destination)

    override fun visit(origin: ZoneLocation, destination: Position): R =
        evaluate(mapZoneLocation(origin), mapPosition(destination))

    override fun visit(origin: ZoneLocation, destination: ZoneLocation): R =
        evaluate(mapZoneLocation(origin), mapZoneLocation(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPosition): R =
        evaluate(mapZoneLocation(origin), mapRoadPosition(destination))

    override fun visit(origin: ZoneLocation, destination: RoadPositionInZone): R =
        evaluate(mapZoneLocation(origin), mapRoadPositionZone(destination))

    override fun visit(origin: ZoneLocation, destination: Zone): R =
        evaluate(mapZoneLocation(origin), destination)

    override fun visit(origin: RoadPosition, destination: Position): R =
        evaluate(mapRoadPosition(origin), mapPosition(destination))

    override fun visit(origin: RoadPosition, destination: ZoneLocation): R =
        evaluate(mapRoadPosition(origin), mapZoneLocation(destination))

    override fun visit(origin: RoadPosition, destination: RoadPosition): R =
        evaluate(mapRoadPosition(origin), mapRoadPosition(destination))

    override fun visit(origin: RoadPosition, destination: RoadPositionInZone): R =
        evaluate(mapRoadPosition(origin), mapRoadPositionZone(destination))

    override fun visit(origin: RoadPosition, destination: Zone): R =
        evaluate(mapRoadPosition(origin), destination)

    override fun visit(origin: RoadPositionInZone, destination: Position): R =
        evaluate(mapRoadPositionZone(origin), mapPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: ZoneLocation): R =
        evaluate(mapRoadPositionZone(origin), mapZoneLocation(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPosition): R =
        evaluate(mapRoadPositionZone(origin), mapRoadPosition(destination))

    override fun visit(origin: RoadPositionInZone, destination: RoadPositionInZone): R =
        evaluate(mapRoadPositionZone(origin), mapRoadPositionZone(destination))

    override fun visit(origin: RoadPositionInZone, destination: Zone): R =
        evaluate(mapRoadPositionZone(origin), destination)

    override fun visit(origin: Zone, destination: Position): R =
        evaluate(origin, mapPosition(destination))

    override fun visit(origin: Zone, destination: ZoneLocation): R =
        evaluate(origin, mapZoneLocation(destination))

    override fun visit(origin: Zone, destination: RoadPosition): R =
        evaluate(origin, mapRoadPosition(destination))

    override fun visit(origin: Zone, destination: RoadPositionInZone): R =
        evaluate(origin, mapRoadPositionZone(destination))

    override fun visit(origin: Zone, destination: Zone): R =
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

object ZoneEquality : ZoneMetric<Boolean> {
    override fun evaluate(origin: Zone, destination: Zone): Boolean {
        return origin.id == destination.id
    }

    override fun mapPosition(origin: Position): Zone {
        error("Cannot map an  position to a zone") // Maybe use closest zone centroid?
    }

    override fun mapRoadPosition(origin: RoadPosition): Zone {
        error("Cannot map an unknown road position to a zone yet")
    }
}

class ConstantMetric<R>(
    val value: R,
) : PositionLevelMetric<R> {

    override fun evaluate(origin: Position, destination: Position): R = value
}
