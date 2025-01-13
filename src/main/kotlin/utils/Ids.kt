package utils

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ID<out E>(val value: Long) : Comparable<ID<*>> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ID<*>): Int {
        return value.compareTo(other.value)
    }
}

interface Identifiable<I> {
    val id: I
}
