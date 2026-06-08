package edu.kit.ifv.domain.synthesis.behavior.householdlocation
import edu.kit.ifv.domain.shared.location.StandardLocation

class TrivialGroupStrategy<AREA, H>(val singularStrategy: AssignHouseholdLocations<in AREA, H>) :
    GroupAssignHouseholdLocations<AREA, H> {
    override fun generateLocations(zone: AREA, householdsToLocate: List<H>): List<Pair<H, StandardLocation>> =
        householdsToLocate.map { it to singularStrategy.generateLocation(zone, it) }
}
