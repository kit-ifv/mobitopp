package synthesis

import domain.data.Zone
import domain.location.Location
import units.Coordinate
import units.Distance
import units.GPSCoordinate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Assign a Location to a household with no information other than the household and the zone
 */
fun interface AssignHouseholdLocations<AREA, H> {
    fun generateLocation(zone: AREA, household: H): Location
}

/**
 * Assign a list of locations, because sometimes it makes sense to handle the group as a whole (To avoid location
 * collisions, for example)
 */
fun interface GroupAssignHouseholdLocations<AREA, H> {
    fun generateLocations(
        zone: AREA,
        householdsToLocate: List<H>
    ): List<Pair<H, Location>>
}

class TrivialGroupStrategy<AREA, H>(
    val singularStrategy: AssignHouseholdLocations<AREA, H>
) : GroupAssignHouseholdLocations<AREA, H> {
    override fun generateLocations(
        zone: AREA,
        householdsToLocate: List<H>
    ): List<Pair<H, Location>> {
        return householdsToLocate.map { it to singularStrategy.generateLocation(zone, it) }
    }
}

class AssignAroundZoneCentroid<H>(private val radius: Distance) : AssignHouseholdLocations<Zone, H> {
    override fun generateLocation(zone: Zone, household: H): Location {
        return Location(zone.centroid.coordinate.randomCoordinate(radius, zone.random), zone, null)
        // TODO zone rng or hh rng?, if hh rng -> require H: StochasticActor
    }
}

@Suppress("MagicNumber") // Earth radius in meters is relatively safe to assume what it means
fun Coordinate.randomCoordinate(radius: Distance, random: Random): Coordinate {
    val lat1 = latitudeRadians.toDouble()
    val lon1 = longitudeRadians.toDouble()

    // Random distance from the center within the radius (in meters)
    val randomDistance = random.nextDouble() * radius.inWholeMeters

    // Random bearing (angle) in radians
    val randomAngle = random.nextDouble(0.0, 2 * Math.PI)

    // Earth radius in meters
    val earthRadius = 6371000.0

    // Change in latitude (in radians)
    val deltaLat = randomDistance / earthRadius

    // Change in longitude (in radians)
    val deltaLon = randomDistance / (earthRadius * cos(lat1))

    // Calculate the new latitude and longitude
    val newLat = lat1 + deltaLat * cos(randomAngle)
    val newLon = lon1 + deltaLon * sin(randomAngle)

    // Convert the new latitude and longitude back to degrees
    val newLatitude = Math.toDegrees(newLat)
    val newLongitude = Math.toDegrees(newLon)

    return GPSCoordinate.decimalDegree(newLatitude, newLongitude)
}
