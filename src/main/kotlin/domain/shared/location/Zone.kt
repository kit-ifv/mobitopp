package domain.shared.location

import Mutable
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegionType
import units.Distance
import units.GPSCoordinate
import utils.Identifiable
import utils.random.StochasticActor
import kotlin.random.Random

@JvmInline
value class ZoneId(val value: Long)
// typealias ZoneId = ID<Zone>

/**
 * Zone - a traffic assignment zone in a transport model.
 *
 * Zones have:
 * - a [visumId] to reference the base visum model
 * - a [name] as description (mostly for debugging and logging)
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
) : StochasticActor, Identifiable<ZoneId> {

    abstract val visumId: Long // TODO not a general property of zone, only here because we use visum
    abstract val name: String
    abstract val regionType: RegionType // Region type and area type are the same. RegionType is the more adequate name
    abstract val classification: ZoneClassification
    abstract val parkingPlaces: Int
    abstract val isDestination: Boolean
    abstract val relief: Distance

    val centroid: Location = centroid.copy(zone = this)
    override val random: Random by lazy { Random(id.value + seed) }
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
