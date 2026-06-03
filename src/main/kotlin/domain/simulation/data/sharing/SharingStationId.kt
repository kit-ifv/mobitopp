package domain.simulation.data.sharing

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class SharingStationId(val value: Long) : Comparable<SharingStationId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: SharingStationId): Int = value.compareTo(other.value)

    /**
     * @return the next higher id.
     */
    fun next(): SharingStationId = SharingStationId(value + 1)
}