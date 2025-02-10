package domain.data

import Mutable
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.Location
import units.Distance
import units.GPSCoordinate
import utils.ID
import utils.Identifiable
import utils.random.SeededActor

typealias ZoneId = ID<Zone>

/**
 * Zone - a traffic assignment zone in a transport model.
 *
 * Zones have:
 * - a [visumId] to reference the base visum model
 * - a [name] as description (mostly for debugging and logging)
 * - //TODO naming of these properties might be confusing
 * - an [areaType] as classification of the zones regional function/characteristics
 * - a [regionType] Integer code
 * - a survey area [classification]
 * - a number of public [parkingPlaces]
 * - a flag to denote whether it [isDestination] in destination choice
 * - a [relief] - the standard deviation of elevation in the zone
 * - a [centroid] location
 *
 * @property id
 * @constructor
 *
 * @param centroid
 * @param seed
 */
@Mutable
abstract class Zone(
    override val id: ZoneId,
    centroid: Location,
    seed: Long,
) : SeededActor<Zone>(seed), Identifiable<ZoneId> {

    abstract val visumId: Long // TODO not a general property of zone, only here because we use visum
    abstract val name: String
    abstract val regionType: AreaType // Region type and area type are the same. RegionType is the more adequate name
    abstract val classification: ZoneClassification
    abstract val parkingPlaces: Int
    abstract val isDestination: Boolean
    abstract val relief: Distance

    val centroid: Location = centroid.copy(zone = this)

    operator fun contains(location: Location): Boolean = location.zone == this
}

@Mutable
abstract class LegacyZone(
    id: ZoneId,
    centroid: Location,
    seed: Long,
) : Zone(id, centroid, seed) {

    abstract val matrixColumn: Int
}

fun Zone.point(gpsCoordinate: GPSCoordinate) = Location(gpsCoordinate, zone = this, roadAccess = null)
