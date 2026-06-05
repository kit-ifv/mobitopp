package domain.shared.data.activity

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ActivityId(val value: Long) : Comparable<ActivityId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ActivityId): Int = value.compareTo(other.value)

    /**
     * @return the next higher id.
     */
    fun next(): ActivityId = ActivityId(value + 1)
}
