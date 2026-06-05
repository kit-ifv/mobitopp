package edu.kit.ifv.domain.synthesis.behavior.householdlocation
import edu.kit.ifv.domain.shared.location.StandardLocation

/**
 * Assign a list of locations, because sometimes it makes sense to handle the group as a whole (To avoid location
 * collisions, for example)
 */
fun interface GroupAssignHouseholdLocations<AREA, H> {
    fun generateLocations(zone: AREA, householdsToLocate: List<H>): List<Pair<H, StandardLocation>>
}
