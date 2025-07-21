package domain.simulation.behavior

import core.events.Resource
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PrivateCarAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.agent.SharingStationAgent
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import utils.units.AbsoluteTime
import kotlin.random.Random

data class TripChoiceSituation(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
    val attractivityModel: AttractivenessModel,
    val modeAvailabilityFilter: ModeAvailabilityFilter,
)  {

    fun with(choice: Location) =
        DestinationAlternative(
            person,
            time,
            origin,
            choice,
            impedance,
            sharedResources,
            attractivityModel,
            modeAvailabilityFilter
        )

    val random: Random
        get() = person.random
}

data class DestinationAlternative(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
    val attractivityModel: AttractivenessModel,
    val modeAvailabilityFilter: ModeAvailabilityFilter,
)  {
    val choice: Location get() = destination

}

data class ModeChoiceSituation(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
)  {
    val random: Random get() = person.random
    fun with(choice: Mode) = ModeChoiceAlternative(
        person,
        time,
        origin,
        destination,
        impedance,
        sharedResources
    )
}

data class ModeChoiceAlternative(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
)  {

}

fun interface ModeAvailabilityFilter : ChoiceFilter<Mode, ModeChoiceSituation>

class SharingAvailabilityFilter(
    val modes: ChoiceModelModes,
    private val sharingStations: Set<SharingStationAgent>,
    private val providersByMode: Map<Mode, Set<SharingProviderAgent>>,
    private val metrics: Metrics,
) : ModeAvailabilityFilter {

//    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()
    context(characteristics: ModeChoiceSituation)
    override fun filter(choices: Set<Mode>): Set<Mode> {
        return choices.filter { determineAvailability(it, characteristics) }.toSet()

//        val modeList = choices.map { it.choice }
//        val set = BitSet(modeList.size)
//        set.flip(0, modeList.size)
//        choices.withIndex().forEach {
//            set[it.index] = determineAvailability(it.value.choice, it.value)
//        }
//        return cache.getOrPut(set.toByteArray().toList()) {
//            modeList.filterBy(set).toSet()
//        }
    }

    // TODO insert check for sharing stations and find missing resources
    private fun determineAvailability(mode: Mode, params: ModeChoiceSituation): Boolean {
        return when (mode) {
            modes.car -> checkCar(params.person)
            modes.bike -> checkBike(params.person)
            modes.ridePooling -> checkRidepooling(params.person, mode)
            modes.publicTransport -> checkPut(params.person)
            modes.pedestrian -> true
            modes.passenger -> true // TODO is passenger always available?
            modes.bikeSharing -> checkSharing(params, mode) != null
            else -> false
        }
    }

    private fun checkBike(person: PersonAgent): Boolean {
        return person.hasBike
    }

    private fun checkPut(person: PersonAgent): Boolean {
        return person.hasCommuterTicket
    }

    private fun checkCar(person: PersonAgent): Boolean {
        return person.hasLicense && person.household.cars.any {
            (it.location == person.location) && it.state != PrivateCarAgent.CarState.IN_USE &&
                ((it.location == person.household.location) || it.keyHolder == person)
        }
    }

    private fun checkRidepooling(person: PersonAgent, mode: Mode): Boolean {
        val sharingProviders = providersByMode[mode] ?: emptySet()
        // TODO test this
        return sharingProviders.any { it in person.memberships }
    }

    fun checkSharing(
        params: ModeChoiceSituation,
        sharingMode: Mode
    ): Pair<SharingStationAgent, SharingStationAgent>? {
        val person = params.person

        val modeMemberships = providersByMode[sharingMode]?.filter {
            it in person.memberships
        } ?: emptySet()

        if (modeMemberships.isEmpty()) {
            return null
        }

        val memberStations = sharingStations.filter { it.owner in modeMemberships }

        val origin = params.origin
        val destination = params.destination

        val startStation =
            memberStations.filter {
                it.zonesByFoot.any { zone -> origin in zone }
            }.filter {
                it.hasAvailableVehicles
            }.minByOrNull {
                metrics.distance(origin, it.location, sharingMode)
            }

        val endStation =
            memberStations.filter {
                it.zonesByFoot.any { zone -> destination in zone }
            }.filter {
                it != startStation
            }.minByOrNull {
                metrics.distance(it.location, destination, sharingMode)
            }

        return startStation?.let { start ->
            endStation?.let { end ->
                start to end
            }
        }
    }
}
