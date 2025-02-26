package usecases.steps.legacyData

import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.Zone
import domain.data.ZoneId
import modeling.steps.Context
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.UpdateAllStep
import modeling.steps.UpdateEachStep
import modeling.validation.Warning
import synthesis.AssignAroundZoneCentroid
import synthesis.AssignHouseholdLocations
import synthesis.GroupAssignHouseholdLocations
import synthesis.TrivialGroupStrategy
import units.meters
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

interface HomeLocationModelContext: Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
}

fun HomeLocationModelContext.householdHomeLocation(
    model: AssignHouseholdLocations<Household> = AssignAroundZoneCentroid(100.meters),
) = runStep {
    HomeLocationStep(this, model)
}

fun HomeLocationModelContext.groupedHouseholdHomeLocation(
    model: GroupAssignHouseholdLocations<MutableHousehold> = TrivialGroupStrategy(AssignAroundZoneCentroid(100.meters)),
) = runStep {
    GroupedHomeLocationsStep(this, model)
}

class HomeLocationStep(
    context: HomeLocationModelContext,
    val model: AssignHouseholdLocations<Household>
): UpdateEachStep<MutableHousehold, HouseholdId>() {
    override val name = "Assign Home Location to Households"
    override val repository = context.householdRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.zoneRepository)

    override fun update(element: MutableHousehold) {
        val zone = element.location.requireZone()
        element.location = model.generateLocation(zone, element)
    }

    override fun verifyInput(): Warning? = null //TODO

}

class GroupedHomeLocationsStep(
    context: HomeLocationModelContext,
    val model: GroupAssignHouseholdLocations<MutableHousehold>
): UpdateAllStep<MutableHousehold, HouseholdId>() {
    override val name = "Assign Home Location to Households grouped by zone"
    override val repository = context.householdRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.zoneRepository)

    override fun updateAll(elements: Collection<MutableHousehold>) {
        val householdsByZone = elements.groupBy { it.location.requireZone() }
        householdsByZone.entries.forEach { (zone, households) ->
            model.generateLocations(zone, households).forEach { (hh, loc) ->
                hh.location = loc
            }
        }
    }

    override fun verifyInput(): Warning? = null //TODO

}