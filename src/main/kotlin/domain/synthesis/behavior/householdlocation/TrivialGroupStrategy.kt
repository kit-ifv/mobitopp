package domain.synthesis.behavior.householdlocation

import domain.shared.location.StandardLocation

class TrivialGroupStrategy<AREA, H>(val singularStrategy: AssignHouseholdLocations<AREA, H>) :
    GroupAssignHouseholdLocations<AREA, H> {
    override fun generateLocations(zone: AREA, householdsToLocate: List<H>): List<Pair<H, StandardLocation>> =
        householdsToLocate.map { it to singularStrategy.generateLocation(zone, it) }
}
