package edu.kit.ifv.domain.synthesis.behavior.householdlocation

import edu.kit.ifv.CoordinateGenerator
import edu.kit.ifv.VisumPolyZone
import edu.kit.ifv.VisumZoneId
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.StandardLocationImpl
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.shared.location.zone.attributes.HasVisumId
import org.locationtech.jts.geom.Point

/**
 * Generates locations using the given [distributor]. Essentially uses the functionality of the [distributor] to
 * distribute coordinates inside [edu.kit.ifv.VisumPolyZone]-areas according to a landuse-model.
 *
 * @param polyZones The zones used for generating locations.
 * @param distributor A [edu.kit.ifv.LanduseDistributedCoordinates]-[CoordinateGenerator] initialized with the land-use-model and
 * weights for the landuse-types that should be used to distribute the generated coordinates inside zones.
 */
class ZoneDistributedLocations<Z, T>(
    private val polyZones: Map<VisumZoneId, VisumPolyZone>,
    private val distributor: CoordinateGenerator,
) : AssignHouseholdLocations<Zone<Z>, T>,
    GroupAssignHouseholdLocations<Zone<Z>, T> where Z : HasVisumId, Z : HasRegionType {

    /**
     * Generates one location inside the polyzone, which matches the visumID of the given [zone].
     */
    override fun generateLocation(zone: Zone<Z>, household: T): StandardLocation {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.attributes.visumId)]
            ?: polyzoneNotFound(zone.attributes.visumId)
        val coordinate = distributor.generateOneCoordinate(polyZone)
        return StandardLocationImpl(coordinate, zone)
    }

    /**
     * Generates `householdsToLocate.size` many locations inside the polyzone, which matches the visumID of the given
     * [zone].
     */
    override fun generateLocations(zone: Zone<Z>, householdsToLocate: List<T>): List<Pair<T, StandardLocation>> {
        val polyZone: VisumPolyZone = polyZones[VisumZoneId(zone.attributes.visumId)]
            ?: polyzoneNotFound(zone.attributes.visumId)
        val generatedLocations =
            distributor.generateCoordinates(polyZone, householdsToLocate.size).map {
                StandardLocation(
                    it,
                    zone,
                    RoadAccess.Companion.INVALID,
                )
            }
        return householdsToLocate.zip(generatedLocations)
    }

    private fun CoordinateGenerator.generateOneCoordinate(polyZone: VisumPolyZone): Point =
        this.generateCoordinates(polyZone, 1).first()

    private fun polyzoneNotFound(id: Number): Nothing {
        error("Polyzone with visumID $id not found")
    }
}
