package usecases.models

import domain.data.Person
import domain.data.PrivateCar
import domain.data.SharingProvider
import domain.data.SharingStation
import domain.enums.Mode
import domain.location.Location
import domain.location.Metrics
import domain.resources.Resource
import modeling.models.ChoiceAlternative
import modeling.models.ChoiceFilter
import modeling.models.ChoiceSituation
import usecases.AttractivenessModel
import utils.units.AbsoluteTime
import kotlin.random.Random

data class TripChoiceSituation(
    val person: Person,
    val time: AbsoluteTime,
    val origin: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<Person>> = emptySet(),
    val attractivityModel: AttractivenessModel,
    val modeAvailabilityFilter: ModeAvailabilityFilter,
) : ChoiceSituation<DestinationAlternative, Location> {

    override fun with(choice: Location) =
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

    override val random: Random
        get() = person.random
}

data class DestinationAlternative(
    val person: Person,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<Person>> = emptySet(),
    val attractivityModel: AttractivenessModel,
    val modeAvailabilityFilter: ModeAvailabilityFilter,
) : ChoiceAlternative<Location>() {
    override val choice: Location get() = destination
}

data class ModeChoiceSituation(
    val person: Person,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val sharedResources: Set<Resource<Person>> = emptySet(),
) : ChoiceSituation<ModeChoiceAlternative, Mode> {
    override val random: Random get() = person.random
    override fun with(choice: Mode) = ModeChoiceAlternative(
        person,
        time,
        origin,
        destination,
        choice,
        impedance,
        sharedResources
    )
}

data class ModeChoiceAlternative(
    val person: Person,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val mode: Mode,
    val impedance: Metrics,
    val sharedResources: Set<Resource<Person>> = emptySet(),
) : ChoiceAlternative<Mode>() {
    override val choice: Mode get() = mode
}

fun interface ModeAvailabilityFilter : ChoiceFilter<ModeChoiceAlternative>

class SharingAvailabilityFilter(
    val modes: ChoiceModelModes,
    private val sharingStations: Set<SharingStation>,
    private val providersByMode: Map<Mode, Set<SharingProvider>>,
    private val metrics: Metrics,
) : ModeAvailabilityFilter {

//    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()

    override fun filter(choices: Set<ModeChoiceAlternative>): Set<ModeChoiceAlternative> {
        return choices.filter { determineAvailability(it.choice, it) }.toSet()

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
    private fun determineAvailability(mode: Mode, params: ModeChoiceAlternative): Boolean {
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

    private fun checkBike(person: Person): Boolean {
        return person.hasBike
    }

    private fun checkPut(person: Person): Boolean {
        return person.hasCommuterTicket
    }

    private fun checkCar(person: Person): Boolean {
        return person.hasLicense && person.household.cars.any {
            (it.location == person.location) && it.state != PrivateCar.CarState.IN_USE &&
                ((it.location == person.household.location) || it.keyHolder == person)
        }
    }

    private fun checkRidepooling(person: Person, mode: Mode): Boolean {
        val sharingProviders = providersByMode[mode] ?: emptySet()
        // TODO test this
        return sharingProviders.any { it in person.memberships }
    }

    fun checkSharing(params: ModeChoiceAlternative, sharingMode: Mode): Pair<SharingStation, SharingStation>? {
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
