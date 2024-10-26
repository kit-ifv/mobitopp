package usecases.choicemodels

import domain.data.Person
import domain.data.PrivateCar
import domain.data.SharingProvider
import domain.data.SharingStation
import domain.enums.Mode
import domain.location.Location
import domain.location.Metrics
import domain.resources.Resource
import utils.PerpetualCache
import utils.collections.filterBy
import utils.random.StochasticActor
import java.util.*

fun interface ChoiceFilter<M, P> {
    fun filter(modes: Collection<M>, params: P): Collection<M>
}

object NoFilter : ChoiceFilter<Mode, Person> {
    override fun filter(modes: Collection<Mode>, params: Person): Collection<Mode> {
        return modes
    }
}

data class TripChoiceSituation(
    val person: Person,
    val origin: Location,
    val destination: Location,
    val sharedResources: Set<Resource<Person>> = emptySet(),
) : StochasticActor by person

class ModeAvailabilityFilter(
    val modes: ChoiceModelModes,
    private val sharingStations: Set<SharingStation>,
    private val providersByMode: Map<Mode, Set<SharingProvider>>,
    private val metrics: Metrics,
) : ChoiceFilter<Mode, TripChoiceSituation> {

    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()

    override fun filter(modes: Collection<Mode>, params: TripChoiceSituation): Collection<Mode> {
        val modeList = modes.toList()
        val set = BitSet(modeList.size)
        set.flip(0, modeList.size)
        modeList.withIndex().forEach {
            set[it.index] = determineAvailability(it.value, params)
        }
        return cache.getOrPut(set.toByteArray().toList()) {
            modeList.filterBy(set).toSet()
        }
    }

    // TODO insert check for sharing stations and find missing resources
    private fun determineAvailability(mode: Mode, params: TripChoiceSituation): Boolean {
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
            it.location.matches(person.location) && it.state != PrivateCar.CarState.IN_USE &&
                (it.location.matches(person.household.location) || it.keyHolder == person)
        }
    }

    private fun checkRidepooling(person: Person, mode: Mode): Boolean {
        val sharingProviders = providersByMode[mode] ?: emptySet()
        // TODO test this
        return sharingProviders.any { it in person.memberships }
    }

    fun checkSharing(params: TripChoiceSituation, sharingMode: Mode): Pair<SharingStation, SharingStation>? {
        val person = params.person

        val modeMemberships = providersByMode[sharingMode]?.filter {
            it in person.memberships
        } ?: emptySet()

        if (modeMemberships.isEmpty()) { return null }

        val memberStations = sharingStations.filter { it.owner in modeMemberships }

        val origin = params.origin
        val destination = params.destination

        val startStation =
            memberStations.filter {
                it.zonesByFoot.any { zones -> origin.matches(zones) }
            }.filter {
                it.hasAvailableVehicles
            }.minByOrNull {
                metrics.distance(origin, it.location, sharingMode)
            }

        val endStation =
            memberStations.filter {
                it.zonesByFoot.any { zones -> destination.matches(zones) }
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

/**
 * A wrapper class containing the parameters needed to determine the mode availability of the [ModeAvailabilityFilter]
 */
data class StandardSet(
    val person: Person,
)
