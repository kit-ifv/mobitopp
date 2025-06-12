package domain.simulation.agent

import domain.shared.datastructure.schedule.plans.SingularDispatcher
import domain.simulation.agent.location
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.NoDurationRandomizer
import domain.simulation.behavior.toSchedule
import domain.synthesis.data.CarId
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.SharingStation
import domain.synthesis.data.SharingStationId

class BuildAgents(
    val seed: Long, // TODO discuss if original seed is needed (same as data entity?) or could be different/derived
    val durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer
) {

    val personsById: MutableMap<PersonId, MutablePersonAgent> = mutableMapOf()
    val householdsById: MutableMap<HouseholdId, MutableHouseholdAgent> = mutableMapOf()
    val carsById: MutableMap<CarId, MutablePrivateCarAgent> = mutableMapOf()

    val sharingProvidersById: MutableMap<SharingProviderId, MutableSharingProviderAgent> = mutableMapOf()
    val sharingStationsById: MutableMap<SharingStationId, MutableSharingStationAgent> = mutableMapOf()

    fun buildPersonAgents(
        households: List<Household>
    ): Set<PersonAgent> {
        households.map { it.toAgent(this) }

        return personsById.values.toSet()
    }

    fun buildProviderAgents(
        providers: List<SharingProvider>
    ): Set<SharingProviderAgent> {
        providers.map { it.toAgent(this) }

        return sharingProvidersById.values.toSet()
    }

    fun clear() {
        personsById.clear()
        householdsById.clear()
        carsById.clear()
        sharingProvidersById.clear()
        sharingStationsById.clear()
    }
}

//To prevent recursion cycle, never call toAgent inside scope of getOrPut! Use initAfterPut scope instead
private fun <K, V> MutableMap<K, V>.getOrInitAfterPut(
    key: K,
    defaultValue: () -> V,
    initAfterPut: (V) -> Unit,
): V {
    var init = false
    val result = this.getOrPut(key) {
        init = true
        defaultValue()
    }

    if (init) {
        initAfterPut(result)
    }

    return result
}


fun Household.toAgent(context: BuildAgents) = context.householdsById.getOrInitAfterPut(
    key=this.id,
    defaultValue = { MutableHouseholdAgent(id, context.seed) }
) { agent ->
    agent.householdNumber = this.householdNumber
    agent.surveyYear = this.surveyYear
    agent.location = this.location
    agent.domCode = this.domCode
    agent.type = this.type
    agent.incomePerMonth = this.incomePerMonth
    agent.economicStatus = this.economicStatus
    agent.members.addAll(
        this.members.map { it.toAgent(context, agent) }
    )
    agent.cars.addAll(
        this.cars.map { it.toAgent(context, agent) }
    )
}


fun Person.toAgent(context: BuildAgents, householdAgent: HouseholdAgent = household.toAgent(context)) =
    context.personsById.getOrInitAfterPut(
        key=this.id,
        defaultValue =  { MutablePersonAgent(id, householdAgent, context.seed) }
    ) { agent ->

        agent.age = this.age
        agent.employment = this.employment
        agent.sex = this.sex
        agent.graduation = this.graduation
        agent.income = this.income
        agent.hasBike = this.hasBike
        agent.hasCommuterTicket = this.hasCommuterTicket
        agent.hasLicense = this.hasLicense
        agent.eMobilityAcceptance = this.eMobilityAcceptance
        agent.chargingInfluence = this.chargingInfluence

        agent.sharingMemberships.addAll(
            this.sharingMemberships.map { it.toAgent(context) }
        )
        agent.memberships.addAll(agent.sharingMemberships)
        agent.memberships.add(agent.household)


        agent.schedule = this.plannedActivities.toSchedule(SingularDispatcher())
        this.clearPlannedActivities() // clear to save memory
        context.durationRandomizer.randomizeAll(agent)
    }


fun PrivateCar.toAgent(context: BuildAgents, ownerAgent: HouseholdAgent = owner.toAgent(context)) =
    context.carsById.getOrInitAfterPut(
        key=this.id,
        defaultValue = { MutablePrivateCarAgent(id, ownerAgent) }
    ) { agent ->

        agent.segment = this.segment
        agent.seats = this.seats
        agent.engine = this.engine
        agent.mainUser = this.mainUser?.toAgent(context)
    //TODO idea to reduce copy: in Agent definition:
    // pass long term entity for delegation of interface implementation,
    // only overwrite parts where other agent types are now referenced!
    }

fun SharingProvider.toAgent(context: BuildAgents) = context.sharingProvidersById.getOrInitAfterPut(
    key=this.id,
    defaultValue = { MutableSharingProviderAgent(id, name, mode) }
) { agent ->
    agent.stations.addAll(
        this.stations.map { it.toAgent(context, agent) }
    )
}

private var vehicleIdCounter = 0L

fun SharingStation.toAgent(context: BuildAgents, ownerAgent: MutableSharingProviderAgent) =
    context.sharingStationsById.getOrPut(this.id) {
        val data = this

        val vehicles = (0 until initialVehicleCount).map {
            SharingVehicleAgent(
                SharingVehicleId(vehicleIdCounter++),
                ownerAgent.mode,
                ownerAgent
            )
        }

        ownerAgent.ownedVehicles.addAll(vehicles)

        MutableSharingStationAgent(id, ownerAgent) {
            this.uid = data.uid
            this.name = data.name
            this.location = data.location
            this.zonesByFoot.addAll(data.zonesByFoot)
            addVehicles(vehicles)
        }
    }
