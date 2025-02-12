package domain.location

import domain.data.Zone
import units.Coordinate
import units.Distance
import units.GPSCoordinate
import units.UnitIntervalValue
import units.meters
import units.share

data class RoadAccess(val roadId: Long, val position: UnitIntervalValue, val lateralDistance: Distance = 0.meters)

data class Location(
    val coordinate: Coordinate,
    val zone: Zone?,
    val roadAccess: RoadAccess?,
) {

    fun inSameZone(other: Location): Boolean = this.zone == other.zone

    fun requireZone(): Zone = requireNotNull(zone) {
        "Expected Location $this to specify a zone, but found null!"
    }

    fun withZone(zone: Zone): Location {
        require(this.zone == null) {
            "Cannot add '$zone' to location '$this', as zone is already defined!"
        }

        return this.copy(zone = zone)
    }

    fun withRoadAccess(access: RoadAccess): Location {
        require(this.roadAccess == null) {
            "Cannot add '$access' to location '$this', as roadAccess is already defined!"
        }

        return this.copy(roadAccess = access)
    }
}

/**
 * Parse the legacy mobiTopp String format of coordinate and road access:
 *
 * @return the parsed Location
 */
@Suppress("MagicNumber")
fun String.parseRoadPosition(): Location {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    require(res.size == 4) {
        "Cannot parse '$this' as RoadPosition: expected format LONG:LAT,ROAD_ID,ROAD_POS"
    }

    return Location(
        coordinate = GPSCoordinate.decimalDegree(res[1].toDouble(), res[0].toDouble()),
        zone = null,
        roadAccess = RoadAccess(
            roadId = res[2].toLong(),
            position = res[3].toDouble().share(),
        )
    )
}

val LOCATIONUNKNOWN = Location(
    coordinate = GPSCoordinate.decimalDegree(0.0, 0.0),
    zone = null,
    roadAccess = null,
)
