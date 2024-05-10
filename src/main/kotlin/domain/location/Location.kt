package domain.location

import domain.data.ZoneData
import utils.units.Coordinate
import utils.units.GPSCoordinate
import utils.units.UnitIntervalValue
import utils.units.share

/**
 * A location is the most generic representation of placement/whereabouts of entities.
 */
interface Location {

    fun distance(other: Location, metric: (Location) -> DistanceMetric) = metric(this).visit(other)

    fun duration(other: Location, metric: (Location) -> DurationMetric) = metric(this).visit(other)
    // Current time and mode of transportation could be encoded in metric / visitor?

    fun cost(other: Location, metric: (Location) -> CostMetric) = metric(this).visit(other)

    fun <R> accept(visitor: LocationVisitor<R>): R = visitor.visit(this)
}

/**
 * A position is a location that can be assigned a specific GPS coordinate on the globe.
 * @property coordinate the coordinate of the position
 */
interface Position : Location {
    val coordinate: Coordinate

    override fun <R> accept(visitor: LocationVisitor<R>) = visitor.visit(this)
}

interface ZoneLocation : Position {
    val zone: ZoneData
    override fun <R> accept(visitor: LocationVisitor<R>) = visitor.visit(this)
}

interface RoadPosition : Position {
    val road: Long // TODO replace by link id
    val roadAccess: UnitIntervalValue

    override fun <R> accept(visitor: LocationVisitor<R>) = visitor.visit(this)
}

class RoadPositionInZone(
    roadPosition: RoadPosition,
    override val zone: ZoneData
) : RoadPosition by roadPosition, ZoneLocation {

    override fun <R> accept(visitor: LocationVisitor<R>) = visitor.visit(this)
}

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
