@file:Suppress("TooManyFunctions")

package domain.simulation.agent

import core.statemachine.StateMachineFactory
import domain.shared.datastructure.schedule.plans.SingularDispatcher
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.NoDurationRandomizer
import domain.simulation.behavior.toSchedule
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.CarId
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.SharingStation
import domain.synthesis.data.SharingStationId
import utils.collections.addProgressBar

class BuildAgents(
    val seed: Long, // TODO discuss if original seed is needed (same as data entity?) or could be different/derived
    val personStateMachine: StateMachineFactory<PersonAgent>,
    val personBehavior: PersonBehavior,
    val drtStateMachine: StateMachineFactory<DrtProviderAgent>? = null,
    val drtAlgorithm: ((DrtProvider) -> DrtAlgorithm)? = null,
    val durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
) {

    val personsById: MutableMap<PersonId, MutablePersonAgent> = mutableMapOf()
    val householdsById: MutableMap<HouseholdId, MutableHouseholdAgent> = mutableMapOf()
    val carsById: MutableMap<CarId, MutablePrivateCarAgent> = mutableMapOf()

    val sharingProvidersById: MutableMap<SharingProviderId, MutableSharingProviderAgent> = mutableMapOf()
    val sharingStationsById: MutableMap<SharingStationId, MutableSharingStationAgent> = mutableMapOf()

    val drtProvidersById: MutableMap<DrtProviderId, DrtProviderAgent> = mutableMapOf()

    fun buildPersonAgents(households: List<Household>): Set<PersonAgent> {
        val count = households.sumOf { it.members.size }
        households.filter {
            it.members.isNotEmpty()
        }.addProgressBar("create person agent", expectedCount = count).map {
            it.toAgent(this)
        }

        return personsById.values.toSet()
    }

    fun buildSharingProviderAgents(sharingProviders: List<SharingProvider>): Set<SharingProviderAgent> {
        sharingProviders.map { it.toAgent(this) }

        return sharingProvidersById.values.toSet()
    }

    fun buildDrtProviderAgents(drtProviders: List<DrtProvider>): Set<DrtProviderAgent> {
        drtProviders.map { it.toAgent(this) }

        return drtProvidersById.values.toSet()
    }

    fun clear() {
        personsById.clear()
        householdsById.clear()
        carsById.clear()
        sharingProvidersById.clear()
        sharingStationsById.clear()
    }
}

// To prevent recursion cycle, never call toAgent inside scope of getOrPut! Use initAfterPut scope instead
private fun <K, V> MutableMap<K, V>.getOrInitAfterPut(key: K, defaultValue: () -> V, initAfterPut: (V) -> Unit): V {
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
    key = this.id,
    defaultValue = { MutableHouseholdAgent(id, context.seed) },
) { agent ->
    agent.loadAttributes(this)
    agent.members.addAll(
        this.members.map { it.toAgent(context, agent) },
    )
    agent.cars.addAll(
        this.cars.map { it.toAgent(context, agent) },
    )
}

context(seed: Long)
fun Household.toAgent(): HouseholdAgent {
    val agent = MutableHouseholdAgent(id, seed)
    agent.loadAttributes(this)
    return agent
}

fun MutableHouseholdAgent.loadAttributes(attributes: Household) {
    householdNumber = attributes.householdNumber
    surveyYear = attributes.surveyYear
    location = attributes.location
    domCode = attributes.domCode
    type = attributes.type
    incomePerMonth = attributes.incomePerMonth
    economicStatus = attributes.economicStatus
}

context(household: Household, seed: Long)
fun Person.toAgent(context: BuildAgents): PersonAgent {
    val hhAgent = household.toAgent()
    val personAgent = MutablePersonAgent(id, hhAgent, context.personStateMachine, seed)
    personAgent.loadAttributes(this)
    return personAgent
}

fun MutablePersonAgent.loadAttributes(attributes: Person) {
    age = attributes.age
    employment = attributes.employment
    sex = attributes.sex
    graduation = attributes.graduation
    income = attributes.income
    hasBike = attributes.hasBike
    hasCommuterTicket = attributes.hasCommuterTicket
    hasLicense = attributes.hasLicense
    eMobilityAcceptance = attributes.eMobilityAcceptance
    chargingInfluence = attributes.chargingInfluence
}

fun Person.toAgent(
    context: BuildAgents,
    householdAgent: HouseholdAgent = household.toAgent(
        context,
    ),
) = context.personsById.getOrInitAfterPut(
    key = this.id,
    defaultValue = { MutablePersonAgent(id, householdAgent, context.personStateMachine, context.seed) },
) { agent ->

    agent.loadAttributes(this)
    agent.sharingMemberships.addAll(
        this.sharingMemberships.map { it.toAgent(context) },
    )

    agent.drtMemberships.addAll(
        this.drtMemberships.map { it.toAgent(context) },
    )

    agent.behavior = context.personBehavior

    agent.schedule = this.plannedActivities.toSchedule(SingularDispatcher())
    this.clearPlannedActivities() // clear to save memory
    context.durationRandomizer.randomizeAll(agent)
}

fun PrivateCar.toAgent(
    context: BuildAgents,
    ownerAgent: HouseholdAgent = owner.toAgent(
        context,
    ),
) = context.carsById.getOrInitAfterPut(
    key = this.id,
    defaultValue = { MutablePrivateCarAgent(id, ownerAgent) },
) { agent ->

    agent.segment = this.segment
    agent.seats = this.seats
    agent.engine = this.engine
    agent.mainUser = this.mainUser?.toAgent(context)
    // TODO idea to reduce copy: in Agent definition:
    // pass long term entity for delegation of interface implementation,
    // only overwrite parts where other agent types are now referenced!
}

fun SharingProvider.toAgent(context: BuildAgents) = context.sharingProvidersById.getOrInitAfterPut(
    key = this.id,
    defaultValue = { MutableSharingProviderAgent(id, name, mode) },
) { agent ->
    agent.stations.addAll(
        this.stations.map { it.toAgent(context, agent) },
    )
}

private var vehicleIdCounter = 0L

fun SharingStation.toAgent(context: BuildAgents, ownerAgent: MutableSharingProviderAgent) =
    context.sharingStationsById.getOrPut(
        this.id,
    ) {
        val data = this

        val vehicles = (0 until initialVehicleCount).map {
            SharingVehicleAgent(
                SharingVehicleId(vehicleIdCounter++),
                ownerAgent.mode,
                ownerAgent,
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

fun DrtProvider.toAgent(context: BuildAgents) = context.drtProvidersById.getOrPut(
    key = this.id,
) {
    val stateMachine = requireNotNull(context.drtStateMachine) {
        "Cannot convert DrtProviderData to Agent since drtStateMachine is null. Specify it in BuildAgents context object."
    }

    val algorithm = requireNotNull(context.drtAlgorithm) {
        "Cannot convert DrtProviderData to Agent since drtAlgorithm is null. Specify it in BuildAgents context object."
    }

    DrtProviderAgent(this, algorithm(this), stateMachine)
}
