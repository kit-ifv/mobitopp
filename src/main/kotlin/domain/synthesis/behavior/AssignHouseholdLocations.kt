package domain.synthesis.behavior

import CoordinateGenerator
import LanduseDistributedCoordinates
import domain.VisumPolyZone
import domain.VisumZoneId
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import edu.kit.ifv.units.Coordinate
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.GPSCoordinate
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

/**
 * Generates locations using the given [distributor]. Essentially uses the functionality of the [distributor] to
 * distribute coordinates inside [VisumPolyZone]-areas according to a landuse-model.
 *
 * @param polyZones The zones used for generating locations.
 * @param distributor A [LanduseDistributedCoordinates]-[CoordinateGenerator] initialized with the land-use-model and
 * weights for the landuse-types that should be used to distribute the generated coordinates inside zones.
 */
class ZoneDistributedLocations<T>(
    private val polyZones: Map<VisumZoneId, VisumPolyZone>,
    private val distributor: LanduseDistributedCoordinates,
) : AssignHouseholdLocations<Zone, T>, GroupAssignHouseholdLocations<Zone, T> {

    /**
     * Generates one location inside the polyzone, which matches the visumID of the given [zone].
     */
    override fun generateLocation(zone: Zone, household: T): Location {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.visumId.toInt())]
            ?: polyzoneNotFound(zone.visumId)
        return Location(distributor.generateOneCoordinate(polyZone), zone, null)
    }

    /**
     * Generates `householdsToLocate.size` many locations inside the polyzone, which matches the visumID of the given
     * [zone].
     */
    override fun generateLocations(
        zone: Zone,
        householdsToLocate: List<T>
    ): List<Pair<T, Location>> {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.visumId.toInt())]
            ?: polyzoneNotFound(zone.visumId)
        val generatedLocations =
            distributor.generateCoordinates(polyZone, householdsToLocate.size).map {
                Location(it, zone, null)
            }
        return householdsToLocate.zip(generatedLocations)
    }

    private fun LanduseDistributedCoordinates.generateOneCoordinate(polyZone: VisumPolyZone): GPSCoordinate {
        return this.generateCoordinates(polyZone, 1).first()
    }

    private fun polyzoneNotFound(id: Long): Nothing {
        error("Polyzone with visumID $id not found")
    }
}
