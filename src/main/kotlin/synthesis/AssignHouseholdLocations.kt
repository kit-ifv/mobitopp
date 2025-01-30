package synthesis

import domain.data.Zone
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.domain.SynthesisHousehold
import units.Coordinate
import units.GPSCoordinate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Assign a Location to a household with no information other than the household and the zone
 */
fun interface AssignHouseholdLocations<T> {
    fun generateLocation(zone: Zone, household: SynthesisHousehold<out T>): Location
}

/**
 * Assign a list of locations, because sometimes it makes sense to handle the group as a whole (To avoid location
 * collisions, for example)
 */
fun interface GroupAssignHouseholdLocations<T> {
    fun generateLocations(
        zone: Zone,
        householdsToLocate: List<SynthesisHousehold<out T>>
    ): List<Pair<SynthesisHousehold<out T>, Location>>
}

class TrivialGroupStrategy<T>(val singularStrategy: AssignHouseholdLocations<T>) : GroupAssignHouseholdLocations<T> {
    override fun generateLocations(
        zone: Zone,
        householdsToLocate: List<SynthesisHousehold<out T>>
    ): List<Pair<SynthesisHousehold<out T>, Location>> {
        return householdsToLocate.map { it to singularStrategy.generateLocation(zone, it) }
    }
}

class AssignAroundZoneCentroid(private val radius: Double) : AssignHouseholdLocations<Any> {
    override fun generateLocation(zone: Zone, household: SynthesisHousehold<out Any>): Location {
        return Location(zone.centroid.coordinate.randomCoordinate(radius), zone, null)
    }
}

@Suppress("MagicNumber") // Earth radius in meters is relatively safe to assume what it means
fun Coordinate.randomCoordinate(radiusInMeters: Double, random: Random = GlobalRandomizer): Coordinate {
    val lat1 = latitudeRadians.toDouble()
    val lon1 = longitudeRadians.toDouble()

    // Random distance from the center within the radius (in meters)
    val randomDistance = random.nextDouble() * radiusInMeters

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
