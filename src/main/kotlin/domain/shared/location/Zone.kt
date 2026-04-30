package domain.shared.location

import Mutable
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegionType
import domain.shared.location.attributes.HasZoneID
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.WGS84Coordinate
import kotlinx.serialization.Serializable
import utils.Identifiable
import utils.random.StochasticActor
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.random.Random

@Serializable
@JvmInline
value class ZoneId(val value: Long) : Comparable<ZoneId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ZoneId): Int {
        return value.compareTo(other.value)
    }

    /**
     * @return the next higher id.
     */
    fun next(): ZoneId {
        return ZoneId(value + 1)
    }
}

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

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract val visumId: Long // TODO not a general property of zone, only here because we use visum
    abstract val name: String
    abstract val regionType: RegionType
    abstract val classification: ZoneClassification
    abstract val parkingPlaces: Int
    abstract val isDestination: Boolean
    abstract val relief: Distance

    val centroid: StandardLocation by lazy {
        StandardLocation(
            position = centroid.position,
            zone = this,
            roadAccess = RoadAccess.INVALID
        )
    }

    operator fun contains(location: HasZoneID): Boolean = location.zoneID == this.id

}

fun Zone.point(wgsCoord: WGS84Coordinate) = StandardLocation(
    wgsCoord.toPoint(),
    zone = this,
    roadAccess = RoadAccess.INVALID
)
