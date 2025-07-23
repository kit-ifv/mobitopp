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

interface DestinationChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: Location
    val impedance: Metrics
    val sharedResources: Set<Resource<PersonAgent>>
    val attractivityModel: AttractivenessModel
    val modeAvailabilityFilter: ChoiceFilter<Mode, ModeChoiceCharacteristics>

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: Location,
            impedance: Metrics,
            sharedResources: Set<Resource<PersonAgent>>,
            attractivityModel: AttractivenessModel,
            modeAvailabilityFilter: ChoiceFilter<Mode, ModeChoiceCharacteristics>,
        ): DestinationChoiceCharacteristics {
            return DestinationChoiceCharacteristicsImpl(
                person,
                time,
                origin,
                impedance,
                sharedResources,
                attractivityModel,
                modeAvailabilityFilter,
            )
        }
    }
}

fun DestinationChoiceCharacteristics.with(choice: Location): DestinationAlternative {

    return DestinationAlternative(
        this,
        choice
    )

}

data class DestinationChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: Location,
    override val impedance: Metrics,
    override val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
    override val attractivityModel: AttractivenessModel,
    override val modeAvailabilityFilter: ChoiceFilter<Mode, ModeChoiceCharacteristics>,
) : DestinationChoiceCharacteristics {


    val random: Random
        get() = person.random
}

data class DestinationAlternative(
    val original: DestinationChoiceCharacteristics,
    val choice: Location,
) : DestinationChoiceCharacteristics by original {


}

/**
 * Provide an interface, that way projects can actually implement additional conditions onto the characteristics.
 * These should be the minimum available characteristics during mode choice, so maybe impedance and sharedResources
 * need to be dropped for a more generic implementation.
 */
interface ModeChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: Location
    val destination: Location
    val impedance: Metrics
    val sharedResources: Set<Resource<PersonAgent>>

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: Location,
            destination: Location,
            impedance: Metrics,
            sharedResources: Set<Resource<PersonAgent>>,
        ): ModeChoiceCharacteristics = ModeChoiceCharacteristicsImpl(
            person,
            time,
            origin,
            destination,
            impedance,
            sharedResources,
        )
    }
}

/**
 * Characteristics are invariant within a discrete choice situation.
 */
data class ModeChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: Location,
    override val destination: Location,
    override val impedance: Metrics,
    override val sharedResources: Set<Resource<PersonAgent>> = emptySet(),
) : ModeChoiceCharacteristics {
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
) {

}

fun interface ModeAvailabilityFilter : ChoiceFilter<Mode, ModeChoiceCharacteristics>

class SharingAvailabilityFilter(
    val modes: ChoiceModelModes,
    private val sharingStations: Set<SharingStationAgent>,
    private val providersByMode: Map<Mode, Set<SharingProviderAgent>>,
    private val metrics: Metrics,
) : ModeAvailabilityFilter {

    //    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()
    context(characteristics: ModeChoiceCharacteristics)
    override fun filter(alternative: Mode): Boolean {

        return determineAvailability(alternative, characteristics)

    }

    // TODO insert check for sharing stations and find missing resources
    private fun determineAvailability(mode: Mode, params: ModeChoiceCharacteristics): Boolean {
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
        // TODO this is bugged, commuterticket has nothing to do with availability
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
        params: ModeChoiceCharacteristics,
        sharingMode: Mode,
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
