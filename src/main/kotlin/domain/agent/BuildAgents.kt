package domain.agent

import datastructure.plans.SingularDispatcher
import domain.data.CarId
import domain.data.Household
import domain.data.HouseholdId
import domain.data.Person
import domain.data.PersonId
import domain.data.PrivateCar
import domain.data.SharingProvider
import domain.data.SharingProviderId
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.data.toSchedule
import usecases.models.ActivityDurationRandomizer
import usecases.models.NoDurationRandomizer

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

fun Household.toAgent(context: BuildAgents) = context.householdsById.getOrPut(this.id) {
    val data = this
    MutableHouseholdAgent(id, context.seed) {
        this.householdNumber = data.householdNumber
        this.surveyYear = data.surveyYear
        this.location = data.location
        this.domCode = data.domCode
        this.type = data.type
        this.incomePerMonth = data.incomePerMonth
        this.economicStatus = data.economicStatus
        this.members.addAll(
            data.members.map { it.toAgent(context, this) }
        )
        this.cars.addAll(
            data.cars.map { it.toAgent(context, this) }
        )
    }
}

fun Person.toAgent(context: BuildAgents, householdAgent: HouseholdAgent = household.toAgent(context)) =
    context.personsById.getOrPut(this.id) {
        val data = this
        MutablePersonAgent(id, householdAgent, context.seed) {
            this.age = data.age
            this.employment = data.employment
            this.sex = data.sex
            this.graduation = data.graduation
            this.income = data.income
            this.hasBike = data.hasBike
            this.hasCommuterTicket = data.hasCommuterTicket
            this.hasLicense = data.hasLicense
            this.eMobilityAcceptance = data.eMobilityAcceptance
            this.chargingInfluence = data.chargingInfluence
            this.sharingMemberships.addAll(
                data.sharingMemberships.map { it.toAgent(context) }
            )

            this.schedule = data.plannedActivities.toSchedule(SingularDispatcher())
            data.clearPlannedActivities() // clear to save memory
            context.durationRandomizer.randomizeAll(this)

            this.memberships.addAll(this.sharingMemberships)
            this.memberships.add(this.household)
        }
    }

fun PrivateCar.toAgent(context: BuildAgents, ownerAgent: HouseholdAgent = owner.toAgent(context)) =
    context.carsById.getOrPut(this.id) {
        val data = this
        MutablePrivateCarAgent(id, ownerAgent) {
            this.segment = data.segment
            this.seats = data.seats
            this.engine = data.engine
            this.mainUser = data.mainUser?.toAgent(context)
        }
    }

fun SharingProvider.toAgent(context: BuildAgents) = context.sharingProvidersById.getOrPut(this.id) {
    val data = this
    MutableSharingProviderAgent(id, name, mode) {
        this.stations.addAll(
            data.stations.map { it.toAgent(context, this) }
        )
    }
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
