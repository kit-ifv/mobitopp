package domain.synthesis.behavior.fixeddestinations.communitybased

import domain.shared.location.StandardLocation

/**
 * A read only view of the commute demand for an unknown input community number. This class provides all methods that
 * do not alter the state of the underlying demand.
 */
open class CommunityDemand(
    protected val converter: (StandardLocation) -> CommunityNumber,
    protected val demands: MutableMap<CommunityNumber, Double> = mutableMapOf(),
    val communityID: CommunityNumber,
) {
    val total get() = demands.values.sum()
    val keys get() = demands.keys
    fun isEmpty() = demands.isEmpty()
    fun isNotEmpty() = demands.isNotEmpty()
    operator fun get(j: CommunityNumber): Double = demands[j] ?: 0.0

    operator fun get(j: Number): Double = get(j.toCommunity())

    operator fun contains(j: CommunityNumber): Boolean = j in demands.keys

    operator fun contains(j: Number): Boolean = contains(j.toCommunity())

    /**
     * A demand to a certain community is saturated once the demand has dropped below a positive number
     */
    fun isSaturated(j: CommunityNumber): Boolean = get(j) <= 0.0

    fun isSaturated(location: StandardLocation): Boolean = isSaturated(converter(location))
    fun isSaturated(j: Number): Boolean = isSaturated(j.toCommunity())

    override fun toString(): String = demands.toString()

    protected fun Number.toCommunity(): CommunityNumber = CommunityNumber(this.toInt())
}