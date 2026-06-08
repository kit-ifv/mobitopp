package edu.kit.ifv.domain.synthesis.behavior
/**
 * This household is the smallest possible representation of a household.
 */

interface MinimalistHousehold<out S, out T> {
    val members: Collection<MinimalistPerson<T>>
    val size get() = members.size
    val attributes: S
}
