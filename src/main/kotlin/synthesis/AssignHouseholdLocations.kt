package synthesis

import domain.location.Location
import units.Coordinate
import units.GPSCoordinate
import units.radians
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.*

fun interface AssignHouseholdLocations {
    fun assign(synthesisResults: Map<SynZone, List<SurveyHousehold>>): List<LocatedHousehold>
}


class AssignAroundCentroid(val radius: Double) : AssignHouseholdLocations {
    override fun assign(synthesisResults: Map<SynZone, List<SurveyHousehold>>): List<LocatedHousehold> {
        return synthesisResults.entries.flatMap { (zone, households) ->
            households.map {
                LocatedHousehold(
                    it,
                    Location(zone.centroid.coordinate.randomCoordinate(radius), null, null)
                )
            }
        }
    }

}

fun Coordinate.randomCoordinate(radiusInMeters: Double, random: Random = Random(1)): Coordinate {
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
    val temp = newLatitude.radians
    return GPSCoordinate(newLatitude.radians, newLongitude.radians)
}

class LocatedHousehold(private val surveyHousehold: SurveyHousehold, val location: Location) {
    val members get() = surveyHousehold.members

}