package domain.shared.data.household

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class HouseholdId(val value: Long) : Comparable<HouseholdId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: HouseholdId): Int = value.compareTo(other.value)

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): HouseholdId = HouseholdId(value + 1)
}
