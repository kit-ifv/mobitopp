package application.steps.model

import application.steps.HasZoneRepo
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.updateBulkStep
import core.modelsteps.scopes.updateEachStep
import domain.shared.location.zone.Zone
import domain.shared.location.zone.attributes.HasCentroid
import domain.shared.location.zone.attributes.HasRegionType
import domain.synthesis.behavior.householdlocation.AssignAroundPoint
import domain.synthesis.behavior.householdlocation.AssignHouseholdLocations
import domain.synthesis.behavior.householdlocation.GroupAssignHouseholdLocations
import domain.synthesis.behavior.householdlocation.TrivialGroupStrategy
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.household.Household
import domain.synthesis.data.household.HouseholdId
import edu.kit.ifv.units.meters

// TODO generalize MutableHousehold to HasMutableStandardLocation etc.
// TODO add model step to select location in polygon

/**
 * Assigns home locations to each household individually.
 *
 * This step iterates over each household in the [repository] and uses the provided [model]
 * to generate and assign a specific location (e.g., coordinates) within its assigned zone.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param repository The mutable repository of households to update. Provided via context.
 * @param model The model used to generate locations. Defaults to [AssignAroundPoint].
 */
context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C, Z> C.assignHouseholdLocation(
    model: AssignHouseholdLocations<Zone<Z>, Household> = AssignAroundPoint(100.meters),
) where C : HasZoneRepo<*, Zone<Z>>, Z : HasRegionType, Z : HasCentroid = updateEachStep(
    name = "Assign Home Location to each Household",
    dependentRepositories = setOf(zoneRepository),
) { household ->
    val zoneID = household.location.attributes.zoneId
    val zone = getZone(zoneID)
    household.location = model.generateLocation(zone, household)
}

/**
 * Assigns home locations to households in bulk, grouped by zone.
 *
 * This step groups households by their zone and then uses the provided [model] to generate
 * and assign locations for all households in each zone at once. This can be more efficient
 * for certain location assignment strategies.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param repository The mutable repository of households to update. Provided via context.
 * @param model The model used to generate locations in bulk. Defaults to [TrivialGroupStrategy].
 */
context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C, Z> C.assignHouseholdLocationsInBulk(
    model: GroupAssignHouseholdLocations<Zone<Z>, MutableHousehold> =
        TrivialGroupStrategy(AssignAroundPoint(100.meters)),
) where C : HasZoneRepo<*, Zone<Z>>, Z : HasRegionType, Z : HasCentroid = updateBulkStep(
    name = "Assign Home Location to Households grouped by zone",
    dependentRepositories = setOf(zoneRepository),
) { households ->
    val householdsByZone = households.groupBy { it.location.attributes.zoneId }
    householdsByZone.entries.forEach { (zoneID, households) ->
        val zone = getZone(zoneID)

        model.generateLocations(zone, households).forEach { (hh, loc) ->
            hh.location = loc
        }
    }
}
