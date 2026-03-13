package domain.synthesis.behavior

import CoordinateGenerator
import LanduseDistributedCoordinates
import domain.VisumPolyZone
import domain.VisumZoneId
import domain.shared.location.HasZone
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZonedRoadAccessLocation
import domain.shared.location.toPoint
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.KCoordinate
import edu.kit.ifv.units.WGS84Coordinate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Assign a Location to a household with no information other than the household and the zone
 */
fun interface AssignHouseholdLocations<AREA, H> {
    fun generateLocation(zone: AREA, household: H): StandardLocation
}

fun <AREA : Zone, H> AssignHouseholdLocations<AREA, H>.generateZoneLocations(zone: AREA, household: H): HasZone {
    return generateLocation(zone, household).withZone(zone.id)
}

fun <AREA : Zone, H> AssignHouseholdLocations<AREA, H>.generateZoneRoadLocations(
    zone: AREA,
    household: H,
): ZonedRoadAccessLocation {
    return generateZoneLocations(zone, household).withRoadAccess(TODO())
}

/**
 * Assign a list of locations, because sometimes it makes sense to handle the group as a whole (To avoid location
 * collisions, for example)
 */
fun interface GroupAssignHouseholdLocations<AREA, H> {
    fun generateLocations(
        zone: AREA,
        householdsToLocate: List<H>,
    ): List<Pair<H, StandardLocation>>
}

class TrivialGroupStrategy<AREA, H>(
    val singularStrategy: AssignHouseholdLocations<AREA, H>,
) : GroupAssignHouseholdLocations<AREA, H> {
    override fun generateLocations(
        zone: AREA,
        householdsToLocate: List<H>,
    ): List<Pair<H, StandardLocation>> {
        return householdsToLocate.map { it to singularStrategy.generateLocation(zone, it) }
    }
}

class AssignAroundZoneCentroid<AREA : Zone, H>(private val radius: Distance) : AssignHouseholdLocations<AREA, H> {
    override fun generateLocation(zone: AREA, household: H): StandardLocation {
        return zone.centroid
//        return LocationOld(zone.centroid.coordinate.randomCoordinate(radius, zone.random), zone, null)
    }
}


fun KCoordinate.randomCoordinate(radius: Distance, random: Random): KCoordinate {
    return this // TODO we should go away from KCoordinate and fully embrace JTS : Coordinate.
}


@Suppress("MagicNumber") // Earth radius in meters is relatively safe to assume what it means
fun WGS84Coordinate.randomWGSCoordinate(radius: Distance, random: Random): KCoordinate {
    val lat1 = y.toDouble()
    val lon1 = x.toDouble()

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

    return WGS84Coordinate.decimalDegree(newLatitude, newLongitude)
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
    private val distributor: CoordinateGenerator,
) : AssignHouseholdLocations<Zone, T>, GroupAssignHouseholdLocations<Zone, T> {

    /**
     * Generates one location inside the polyzone, which matches the visumID of the given [zone].
     */
    override fun generateLocation(zone: Zone, household: T): StandardLocation {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.visumId.toInt())]
            ?: polyzoneNotFound(zone.visumId)
        val coordinate = distributor.generateOneCoordinate(polyZone)
        return StandardLocation(coordinate.toPoint(), zone, RoadAccess.INVALID)
//        return Location.wgs(coordinate.x, coordinate.y).withZone(zone.id).withRoadAccess(TODO())

    }

    /**
     * Generates `householdsToLocate.size` many locations inside the polyzone, which matches the visumID of the given
     * [zone].
     */
    override fun generateLocations(
        zone: Zone,
        householdsToLocate: List<T>,
    ): List<Pair<T, StandardLocation>> {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.visumId.toInt())]
            ?: polyzoneNotFound(zone.visumId)
        val generatedLocations =
            distributor.generateCoordinates(polyZone, householdsToLocate.size).map {
                StandardLocation(
                    it.toPoint(), zone, RoadAccess.INVALID
                )

            }
        return householdsToLocate.zip(generatedLocations)

//        return householdsToLocate.zip(generatedLocations)
    }

    private fun CoordinateGenerator.generateOneCoordinate(polyZone: VisumPolyZone): WGS84Coordinate {
        return this.generateCoordinates(polyZone, 1).first()
    }

    private fun polyzoneNotFound(id: Long): Nothing {
        error("Polyzone with visumID $id not found")
    }
}
