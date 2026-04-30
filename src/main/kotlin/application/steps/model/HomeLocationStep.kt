package application.steps.model

import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.UpdateAllStep
import core.modelsteps.UpdateEachStep
import core.modelsteps.Warning
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.config.DemandSimContext
import domain.synthesis.behavior.householdlocation.AssignAroundZoneCentroid
import domain.synthesis.behavior.householdlocation.AssignHouseholdLocations
import domain.synthesis.behavior.householdlocation.GroupAssignHouseholdLocations
import domain.synthesis.behavior.householdlocation.TrivialGroupStrategy
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import edu.kit.ifv.units.meters

interface HomeLocationModelContext : DemandSimContext {
    val zoneRepository: Repository<Zone, ZoneId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
}

fun HomeLocationModelContext.householdHomeLocation(
    model: AssignHouseholdLocations<Zone, Household> = AssignAroundZoneCentroid(100.meters),
) = runStep {
    HomeLocationStep(this, model)
}

/**
 * Return the step, but do not execute it immediately.
 */
fun HomeLocationModelContext.assignHouseholdLocation(
    model: AssignHouseholdLocations<Zone, Household> = AssignAroundZoneCentroid(100.meters)
): HomeLocationStep {
    return HomeLocationStep(this, model)
}

fun HomeLocationModelContext.groupedHouseholdHomeLocation(
    model: GroupAssignHouseholdLocations<Zone, MutableHousehold> =
        TrivialGroupStrategy(AssignAroundZoneCentroid(100.meters)),
) = runStep {
    GroupedHomeLocationsStep(this, model)
}

class HomeLocationStep(
    private val context: HomeLocationModelContext,
    val model: AssignHouseholdLocations<Zone, Household>,
) : UpdateEachStep<MutableHousehold, HouseholdId>() {

    override val name = "Assign Home Location to Households"
    override val repository = context.householdRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.zoneRepository)

    override fun update(element: MutableHousehold) {
        val zoneID = element.location.zoneID
        val zone = context.zoneRepository.getValue(zoneID)
        element.location = model.generateLocation(zone, element)
    }

    override fun verifyInput(): Warning? = null // TODO
}

class GroupedHomeLocationsStep(
    private val context: HomeLocationModelContext,
    val model: GroupAssignHouseholdLocations<Zone, MutableHousehold>,
) : UpdateAllStep<MutableHousehold, HouseholdId>() {
    override val name = "Assign Home Location to Households grouped by zone"
    override val repository = context.householdRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.zoneRepository)

    override fun updateAll(elements: Collection<MutableHousehold>) {
        val householdsByZone = elements.groupBy { it.location.zoneID }
        householdsByZone.entries.forEach { (zoneID, households) ->
            val zone = context.zoneRepository.getValue(zoneID)

            model.generateLocations(zone, households).forEach { (hh, loc) ->
                hh.location = loc
            }
        }
    }

    override fun verifyInput(): Warning? = null // TODO
}
