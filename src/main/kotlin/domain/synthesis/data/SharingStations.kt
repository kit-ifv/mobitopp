package domain.synthesis.data

import Mutable
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import kotlinx.serialization.Serializable
import utils.Identifiable

@Serializable
@JvmInline
value class SharingProviderId(val value: Long) : Comparable<SharingProviderId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: SharingProviderId): Int {
        return value.compareTo(other.value)
    }

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): SharingProviderId {
        return SharingProviderId(value + 1)
    }
}

interface ISharingProvider : Identifiable<SharingProviderId> {
    val name: String
    val mode: Mode // TODO assign proper mode
    val stations: Set<ISharingStation>

//    val ownedVehicles: Set<ISharingVehicle>
    val numberOfVehicles: Int
}

@Mutable
abstract class SharingProvider(
    final override val id: SharingProviderId
    // todo should name and mode also be immutable?
) : ISharingProvider {
    abstract override val stations: Set<SharingStation>

    override val numberOfVehicles: Int
        get() = stations.sumOf { it.initialVehicleCount }
}

@Serializable
@JvmInline
value class SharingStationId(val value: Long) : Comparable<SharingStationId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: SharingStationId): Int {
        return value.compareTo(other.value)
    }

    /**
     * @return the next higher id.
     */
    fun next(): SharingStationId {
        return SharingStationId(value + 1)
    }
}

interface ISharingStation : Identifiable<SharingStationId> {
    val uid: String
    val name: String
    val location: StandardLocation
    val zonesByFoot: Set<Zone>
    val owner: ISharingProvider

    fun isReachableFrom(origin: StandardLocation) = zonesByFoot.any { origin in it }
    fun isReachableFrom(zone: Zone) = zone in zonesByFoot
}

@Mutable
abstract class SharingStation(
    final override val id: SharingStationId,
    final override val owner: MutableSharingProvider,
) : ISharingStation {

    abstract val initialVehicleCount: Int

    init {
        registerOwner()
    }

    private fun registerOwner() {
        owner.stations.add(this)
    }
}
