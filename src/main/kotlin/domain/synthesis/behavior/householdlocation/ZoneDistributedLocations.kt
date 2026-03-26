package domain.synthesis.behavior.householdlocation

import CoordinateGenerator
import domain.VisumPolyZone
import domain.VisumZoneId
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.toPoint
import edu.kit.ifv.units.WGS84Coordinate

/**
 * Generates locations using the given [distributor]. Essentially uses the functionality of the [distributor] to
 * distribute coordinates inside [domain.VisumPolyZone]-areas according to a landuse-model.
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
        return StandardLocation.Companion(coordinate.toPoint(), zone, RoadAccess.Companion.INVALID)
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
                StandardLocation.Companion(
                    it.toPoint(),
                    zone,
                    RoadAccess.Companion.INVALID
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
