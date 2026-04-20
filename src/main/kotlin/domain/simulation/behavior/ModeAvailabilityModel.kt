package domain.simulation.behavior

import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Impedance
import domain.shared.location.StandardLocation
import domain.simulation.agent.DrtOffer
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.DrtRequest
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingStationAgent
import domain.simulation.agent.getBestCarOrNull
import domain.simulation.agent.lastTransportMode
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.IPerson
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.drtMembershipIds
import domain.synthesis.data.sharingMembershipIds
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import utils.units.AbsoluteTime
import kotlin.random.Random

interface DestinationChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: StandardLocation
    val impedance: Impedance
    val attractivityModel: AttractivenessModel
    val modeAvailabilityFilter: ChoiceFilter<Mode, DestinationAlternative>

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: StandardLocation,
            impedance: Impedance,
            attractivityModel: AttractivenessModel,
            modeAvailabilityFilter: ChoiceFilter<Mode, DestinationAlternative>,
        ): DestinationChoiceCharacteristics {
            return DestinationChoiceCharacteristicsImpl(
                person,
                time,
                origin,
                impedance,
                attractivityModel,
                modeAvailabilityFilter,
            )
        }
    }
}

fun DestinationChoiceCharacteristics.with(choice: StandardLocation) = DestinationAlternative(this, choice)


data class DestinationChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: StandardLocation,
    override val impedance: Impedance,
    override val attractivityModel: AttractivenessModel,
    override val modeAvailabilityFilter: ChoiceFilter<Mode, DestinationAlternative>,
) : DestinationChoiceCharacteristics {


    val random: Random
        get() = person.random
}

data class DestinationAlternative(
    val original: DestinationChoiceCharacteristics,
    val choice: StandardLocation,
) : DestinationChoiceCharacteristics by original

/**
 * Provide an interface, that way projects can actually implement additional conditions onto the characteristics.
 * These should be the minimum available characteristics during mode choice, so maybe impedance and currentChoices
 * need to be dropped for a more generic implementation.
 * Jelle: impedance holds distance/duration/cost information which should always be used in mode choice!
 */
interface ModeChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: StandardLocation
    val destination: StandardLocation
    val impedance: Impedance
    val currentChoices: Collection<Mode> //cache of filtered modes before mode choice
    val custom: Any?

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: StandardLocation,
            destination: StandardLocation,
            impedance: Impedance,
            currentChoices: Collection<Mode>,
            custom: Any?
        ): ModeChoiceCharacteristics = ModeChoiceCharacteristicsImpl(
            person,
            time,
            origin,
            destination,
            impedance,
            currentChoices,
            custom
        )
    }
}

/**
 * Characteristics are invariant within a discrete choice situation.
 */
data class ModeChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: StandardLocation,
    override val destination: StandardLocation,
    override val impedance: Impedance,
    override val currentChoices: Collection<Mode>,
    override val custom: Any? = null
) : ModeChoiceCharacteristics {
    val random: Random get() = person.random
    fun with(choice: Mode) = ModeChoiceAlternative(
        person,
        time,
        origin,
        destination,
        choice,
        impedance,
    )
}

data class ModeChoiceAlternative( //TODO check if this can be deleted?
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: StandardLocation,
    val destination: StandardLocation,
    val choice: Mode,
    val impedance: Impedance,
)

data class ProviderAvailability(val mode: Mode, val providers: Collection<Any>? = null) { //TODO nullable mode necessary? maybe just nullable list?
    val isAvailable: Boolean = (providers != null)
    val isNotAvailable: Boolean = (providers == null)
}
val Mode.notAvailable get() = ProviderAvailability(this, null)
fun Mode.available(resources: Collection<Any> = emptyList()) = ProviderAvailability(this, resources)

fun Collection<ProviderAvailability>.flatten() = mapNotNull {
    it.providers?.let { provider -> it.mode to provider}
}.run {
    val modes = map { it.first }
    val resources = flatMap { it.second }
    modes to resources
}

context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
fun ModeAvailabilityModel.currentlyAffectedProviders(modes: Collection<Mode>): List<Any> =
    modes.mapNotNull { providerAvailability(it).providers }.flatten()

/**
 * ModeAvailabilityFilter is a [ChoiceFilter] for [ModeChoiceAlternative]s
 * that additionally breaks down availability into three parts:
 *  - static availability refers to the modes generally available to a person
 *    based on their attributes like age, license, etc.
 *  - provider availability refers to the modes available for a person in their current situation
 *    including abstract availability information of resource provides (s.a. sharing stations, drt provider)
 *    but not yet considering availability of single resources.
 *    This also determines which mode related (shared) resource providers might be affected that need to be locked
 *    by mutex before checking the availability of individual resources.
 *  - resource availability refers to modes available in a choice situation defined by [ModeChoiceCharacteristics]
 *    with a selected destination considering the current availability of resources
 *    e.g., filtering out bike sharing if there are no vehicles available.
 *    This should only be called if mutex-lock for resource provider was acquired!
 *
 * This distinction allows to reuse the model at different stages of decision-making.
 * The three stages should be implemented building on top of each other: resource > uses > provider > uses > static.
 * Also, static availability could be precomputed per person and provider availability per choice
 * situation/characteristics to reduce computation time.
 *
 */
interface ModeAvailabilityModel {

    fun asStaticAvailabilityFilter() = ChoiceFilter<Mode, IPerson> { mode ->
        staticAvailability(mode)
    }

    fun asProviderAvailabilityFilter() = object: ChoiceFilter<Mode, DestinationAlternative> {
        context(situation: DestinationAlternative)
        override fun filter(alternative: Mode) =
            context(situation.person, situation.time, situation.choice) {
                providerAvailability(alternative).isAvailable
            }
    }

    fun asResourceAvailabilityFilter() = ChoiceFilter<Mode, ModeChoiceCharacteristics> { mode ->
        resourceAvailability(mode)
    }


    /**
     * Computes static availability of the given mode for the person provided as context:
     * i.e., whether the mode is generally available to a person based on their attributes like age, license, etc.
     *
     * @receiver person the person to compute the static mode availability for
     * @param mode the mode to be checked for static availability
     * @return whether the [mode] is statically available to the person
     */
    context(person: IPerson)
    fun staticAvailability(mode: Mode): Boolean

    /**
     * Computes the provider availability (availability and possibly affected shared resource providers)
     * of the given [mode] in the current choice situation (given in the context).
     *
     * @param mode the mode for which to check availability and affected resources
     * @receiver situation = a [DestinationAlternative] characterizing the choice situation and potential destination
     * @return availability and possibly affected resource providers
     */
    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    fun providerAvailability(mode: Mode): ProviderAvailability

    /**
     * Checks the availability of a given mode choice alternative
     * in the context of a choice situation defined by [ModeChoiceCharacteristics].
     *
     * @param mode the mode to be checked for availability
     * @receiver characteristics = the characteristics of the mode choice situation
     * @return whether the given mode is available
     */
    context(characteristics: ModeChoiceCharacteristics)
    fun resourceAvailability(mode: Mode): Boolean

} // TODO implementation using composite of rules, caching of reduced choice sets in person data and choice situation

fun interface BikeSharingConnectionSelector {
    fun findConnection(person: PersonAgent, destination: StandardLocation): Pair<SharingStationAgent, SharingStationAgent>?
}

interface DrtAvailabilitySelector {
    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent>

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    fun findDrtOffers(): List<DrtOffer>

}

class AvailabilityModelWithSharing(
    val modes: ChoiceModelModes,
    private val sharingProvidersByMode: Map<Mode, Set<SharingProviderId>>,
    private val drtProvidersByMode: Map<Mode, Set<DrtProviderId>>,
    private val impedance: Impedance,
) : ModeAvailabilityModel, BikeSharingConnectionSelector, DrtAvailabilitySelector {

    context(person: IPerson)
    override fun staticAvailability(mode: Mode): Boolean = when (mode) {
        modes.car -> hasCarStatic(person)
        modes.bike -> hasBikeStatic(person)
        modes.carSharingStation -> hasCssbStatic(person)
        modes.carSharingFree -> hasCsffStatic(person)
        modes.ridePooling -> hasPoolingStatic(person)
        modes.bikeSharing -> hasBikeSharingStatic(person)
        else -> true
    }

    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun providerAvailability(mode: Mode): ProviderAvailability =
        takeIf {
            staticAvailability(mode) //TODO use cached static availability of agent
        }?.let {
            when (mode) {
                modes.car -> isCarCurrentlyAvailable()
                modes.bikeSharing -> isBikeSharingCurrentlyAvailable()
                modes.ridePooling -> isPoolingCurrentlyAvailable()
                else -> when {
                    mode.requiresVehicleTakeAlong -> mode.isFixedModeCurrentlyAvailable(person)
                    else -> mode.isFlexModeCurrentlyAvailableI(person)
                }
            }
        } ?: mode.notAvailable

    context(characteristics: ModeChoiceCharacteristics)
    override fun resourceAvailability(mode: Mode): Boolean =
        context(characteristics.person, characteristics.time, characteristics.destination) {
            mode in characteristics.currentChoices //cached choice set before mode choice / lock
        } && when (mode) {
            modes.car -> isPrivateCarAvailableForChoice(characteristics)
            modes.bikeSharing -> isBikesharingAvailableForChoice(characteristics)
            modes.ridePooling -> isPoolingAvailableForChoice(characteristics)
            else -> true
        }

    // Static availability
    private fun hasCarStatic(person: IPerson) =
        person.hasLicense && person.household.cars.isNotEmpty()

    private fun hasBikeStatic(person: IPerson) =
        person.hasBike

    private fun hasCssbStatic(person: IPerson) =
        person.hasLicense &&
                sharingProvidersByMode[modes.carSharingStation]?.any { it in person.sharingMembershipIds } ?: false

    private fun hasCsffStatic(person: IPerson) =
        person.hasLicense &&
                sharingProvidersByMode[modes.carSharingFree]?.any { it in person.sharingMembershipIds } ?: false

    private fun hasPoolingStatic(person: IPerson) =
        drtProvidersByMode[modes.ridePooling]?.any { it in person.drtMembershipIds } ?: false

    private fun hasBikeSharingStatic(person: IPerson) =
        sharingProvidersByMode[modes.bikeSharing]?.any { it in person.sharingMembershipIds } ?: false



    // provider availability

    context(person: PersonAgent)
    private fun isCarCurrentlyAvailable(): ProviderAvailability =
        takeIf {
            person.household.cars.isNotEmpty() &&
                (isHome(person) || (person.lastTransportMode() == modes.car))
        }?.let {
            modes.car.available( setOf(person.household))
        } ?: modes.car.notAvailable


    context(person: PersonAgent, destination: StandardLocation)
    private fun isBikeSharingCurrentlyAvailable(): ProviderAvailability =
        takeIf { isHome(person) || prevModeIsFlexible(person) }?.let {
            person.sharingMemberships.filter {
                it.id in (sharingProvidersByMode[modes.bikeSharing] ?: emptySet())
            }.filter {
                val starts = it.stations.filter { s -> s.zonesByFoot.any { z -> person.location in z } }.toSet()
                val ends = it.stations.filter { s -> s.zonesByFoot.any { z -> destination in z } }.toSet()

                starts != ends && starts.isNotEmpty() && ends.isNotEmpty() //TODO

            }.flatMap {
                it.stations
            }.filter {
                it.zonesByFoot.any { zone -> person.location in zone }
            }.takeIf {
                it.isNotEmpty()
            }

        }?.let {
            modes.bikeSharing.available(it)
        } ?: modes.bikeSharing.notAvailable


    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    private fun isPoolingCurrentlyAvailable(): ProviderAvailability =
        getDrtProvidersCurrentlyOperating().takeIf {
            it.isNotEmpty()
        }?.let {
            modes.ridePooling.available(it)
        } ?: modes.ridePooling.notAvailable

    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent> =
        takeIf { isHome(person) || prevModeIsFlexible(person) }?.let {
            person.drtMemberships.filter {
                it.id in (drtProvidersByMode[modes.ridePooling] ?: emptySet())
            }.filter {
                it.operatesAt(time, person.location, destination)
            }
        } ?: emptyList()


    private fun Mode.isFlexModeCurrentlyAvailableI(person: PersonAgent) =
        takeIf { isHome(person) || prevModeIsFlexible(person) }?.let { available() } ?: notAvailable

    private fun Mode.isFixedModeCurrentlyAvailable(person: PersonAgent) =
        takeIf { isHome(person) || (person.lastTransportMode() == this) }?.let { available() } ?: notAvailable

    private fun prevModeIsFlexible(person: PersonAgent): Boolean =
        (person.lastTransportMode()?.requiresVehicleTakeAlong?.not() ?: true)

    private fun isHome(person: PersonAgent): Boolean = person.location == person.household.location


    //Choice availability
    private fun isPrivateCarAvailableForChoice(characteristics: ModeChoiceCharacteristics) =
        characteristics.person.getBestCarOrNull() != null


    private fun isBikesharingAvailableForChoice(characteristics: ModeChoiceCharacteristics) =
        findConnection(characteristics.person, characteristics.destination) != null

    override fun findConnection(person: PersonAgent, destination: StandardLocation): Pair<SharingStationAgent, SharingStationAgent>? =
        context(person, destination) {
            findStartEndStation()
        }

    context(person: PersonAgent, destination: StandardLocation)
    private fun findStartEndStation(): Pair<SharingStationAgent, SharingStationAgent>? {
        val origin = person.location

        val memberships = person.sharingMemberships.filter {
            it.id in (sharingProvidersByMode[modes.bikeSharing] ?: emptySet())
        }

        val connections = memberships.mapNotNull { provider ->

            provider.stations.filter {
                it.isReachableFrom(origin)
            }.filter {
                it.hasAvailableVehicles
            }.sortedBy {
                impedance.distance(origin, it.location, modes.pedestrian)
            }.firstNotNullOfOrNull { start ->

                provider.stations.filter {
                    it.isReachableFrom(destination)
                }.filter {
                    it != start
                }.minByOrNull {
                    impedance.distance(it.location, destination, modes.pedestrian)
                }?.let {
                    start to it
                }

            }

        }

        return connections.minByOrNull { (start, end) ->
            impedance.distance(origin, start.location, modes.pedestrian) +
            impedance.distance(start.location, end.location, modes.bikeSharing) +
            impedance.distance(end.location, destination, modes.pedestrian)
        }
    }

    private fun isPoolingAvailableForChoice(characteristics: ModeChoiceCharacteristics) =
        characteristics.run {
            context(person, time, destination) {
                findDrtOffers().isNotEmpty()
            }
        }

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun findDrtOffers(): List<DrtOffer> {
        val memberProviders = agent.drtMemberships.filter {
            it.id in (drtProvidersByMode[modes.ridePooling] ?: emptySet())
        }

        return memberProviders.mapNotNull {
            it.requestRide(
                DrtRequest(it,agent, time, time, agent.location, destination)
            )
        }

    }

}

