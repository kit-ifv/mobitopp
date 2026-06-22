package edu.kit.ifv.domain.shared.location.zone
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ZoneId(val value: Long) : Comparable<ZoneId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ZoneId): Int = value.compareTo(other.value)

    /**
     * @return the next higher id.
     */
    fun next(): ZoneId = ZoneId(value + 1)
}

fun Number.toZoneId() = ZoneId(this.toLong())
