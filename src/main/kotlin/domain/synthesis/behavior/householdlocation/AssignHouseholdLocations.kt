package domain.synthesis.behavior.householdlocation

import domain.shared.location.StandardLocation

/**
 * Assign a Location to a household with no information other than the household and the zone
 */
fun interface AssignHouseholdLocations<AREA, H> {
    fun generateLocation(zone: AREA, household: H): StandardLocation
}
