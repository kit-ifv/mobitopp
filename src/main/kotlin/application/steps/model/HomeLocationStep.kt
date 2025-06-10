package application.steps.model

import core.modelsteps.Context
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.UpdateAllStep
import core.modelsteps.UpdateEachStep
import core.modelsteps.Warning
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.behavior.AssignHouseholdLocations
import domain.synthesis.behavior.GroupAssignHouseholdLocations
import domain.synthesis.behavior.TrivialGroupStrategy
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.Zone
import domain.synthesis.data.ZoneId
import units.meters

interface HomeLocationModelContext : Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
}

fun HomeLocationModelContext.householdHomeLocation(
    model: AssignHouseholdLocations<Zone, Household> = AssignAroundZoneCentroid(100.meters),
) = runStep {
    HomeLocationStep(this, model)
}

fun HomeLocationModelContext.groupedHouseholdHomeLocation(
    model: GroupAssignHouseholdLocations<Zone, MutableHousehold> =
        TrivialGroupStrategy(AssignAroundZoneCentroid(100.meters)),
) = runStep {
    GroupedHomeLocationsStep(this, model)
}

class HomeLocationStep(
    context: HomeLocationModelContext,
    val model: AssignHouseholdLocations<Zone, Household>
) : UpdateEachStep<MutableHousehold, HouseholdId>() {
    override val name = "Assign Home Location to Households"
    override val repository = context.householdRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.zoneRepository)

    override fun update(element: MutableHousehold) {
        val zone = element.location.requireZone()
        element.location = model.generateLocation(zone, element)
    }

    override fun verifyInput(): Warning? = null // TODO
}

class GroupedHomeLocationsStep(
    context: HomeLocationModelContext,
    val model: GroupAssignHouseholdLocations<Zone, MutableHousehold>
) : UpdateAllStep<MutableHousehold, HouseholdId>() {
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

    override fun verifyInput(): Warning? = null // TODO
}
