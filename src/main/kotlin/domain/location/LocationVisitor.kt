package domain.location

import units.Currency
import units.Distance
import kotlin.time.Duration

interface LocationVisitor<R> {
    val location: Location

    fun visit(location: Location): R {
        return location.accept(this)
    }

    fun visit(position: Position): R
    fun visit(roadPosition: RoadPosition): R
    fun visit(locationInZone: ZoneLocation): R
    fun visit(roadPositionInZone: RoadPositionInZone): R

}

interface ToPositionMetric<R>: LocationVisitor<R> {
    override val location: Position
}

interface ToZoneMetric<R>: LocationVisitor<R> {
    override val location: ZoneLocation
}

interface ToRoadMetric<R>: LocationVisitor<R> {
    override val location: RoadPosition
}

interface ToRoadInZoneMetric<R>: LocationVisitor<R> {
    override val location: RoadPositionInZone
}

typealias DistanceMetric = LocationVisitor<Distance>
interface DistanceToPosition: DistanceMetric, ToPositionMetric<Distance>
interface DistanceToZone: DistanceMetric, ToZoneMetric<Distance>
interface DistanceToRoad: DistanceMetric, ToRoadMetric<Distance>
interface DistanceToRoadInZone: DistanceMetric, ToRoadInZoneMetric<Distance>

typealias DurationMetric = LocationVisitor<Duration>
interface DurationToPosition: DurationMetric, ToPositionMetric<Duration>
interface DurationToZone: DurationMetric, ToZoneMetric<Duration>
interface DurationToRoad: DurationMetric, ToRoadMetric<Duration>
interface DurationToRoadInZone: DurationMetric, ToRoadInZoneMetric<Duration>

typealias CostMetric = LocationVisitor<Currency>
interface CostToPosition: CostMetric, ToPositionMetric<Currency>
interface CostToZone: CostMetric, ToZoneMetric<Currency>
interface CostToRoad: CostMetric, ToRoadMetric<Currency>
interface CostToRoadInZone: CostMetric, ToRoadInZoneMetric<Currency>

class FlightDistance(
    override val location: Position
): DistanceToPosition {
    override fun visit(position: Position) = distanceTo(position)

    override fun visit(roadPosition: RoadPosition) = distanceTo(roadPosition)

    override fun visit(locationInZone: ZoneLocation) = distanceTo(locationInZone)

    override fun visit(roadPositionInZone: RoadPositionInZone) = distanceTo(roadPositionInZone)

    private fun distanceTo(position: Position) = position.coordinate.distance(this.location.coordinate)

}
