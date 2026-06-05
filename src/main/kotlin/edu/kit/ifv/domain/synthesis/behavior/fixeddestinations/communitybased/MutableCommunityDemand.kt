package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased
import edu.kit.ifv.domain.shared.location.StandardLocation

/**
 * The Mutable Community Demand allows the alteration of demand, and provides the decreaseDemand method as a convenience
 * method to decrease the demand once an agent has been assigned.
 */
class MutableCommunityDemand(
    converter: (StandardLocation) -> CommunityNumber,
    demands: MutableMap<CommunityNumber, Double> = mutableMapOf(),
    communityID: CommunityNumber,
) : CommunityDemand(converter, demands, communityID) {

    operator fun set(j: CommunityNumber, value: Double) {
        demands[j] = value
    }

    operator fun set(j: Number, value: Double) = set(j.toCommunity(), value)

    fun copy(): MutableCommunityDemand = MutableCommunityDemand(converter, demands.toMutableMap(), communityID)

    /**
     * Decrease the demand towards the target community number by 1. If the demand is not present, add it and set it to
     * 0
     */
    fun decreaseDemandFor(j: CommunityNumber) {
        demands[j] = (demands[j] ?: 1.0) - 1.0
    }

    fun decreaseDemandFor(location: StandardLocation) = decreaseDemandFor(converter(location))
}
