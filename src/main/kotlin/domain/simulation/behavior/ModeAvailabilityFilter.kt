package domain.simulation.behavior

import discreteChoice.models.ChoiceAlternative
import discreteChoice.models.ChoiceFilter
import discreteChoice.models.ChoiceSituation
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PrivateCarAgent
import domain.simulation.agent.SharingStationAgent
import domain.simulation.agent.getBestCarOrNull
import domain.simulation.agent.lastTransportMode
import domain.synthesis.data.IPerson
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.sharingMembershipIds
import utils.units.AbsoluteTime
import kotlin.random.Random

data class TripChoiceSituation(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val impedance: Metrics,
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
            attractivityModel,
            modeAvailabilityFilter
        )

    override val random: Random
        get() = person.random
}

data class DestinationAlternative(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
    val attractivityModel: AttractivenessModel,
    val modeAvailabilityFilter: ModeAvailabilityFilter,
) : ChoiceAlternative<Location>() {
    override val choice: Location get() = destination
}

data class ModeChoiceSituation(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val impedance: Metrics,
) : ChoiceSituation<ModeChoiceAlternative, Mode> {
    override val random: Random get() = person.random
    override fun with(choice: Mode) = ModeChoiceAlternative(
        person,
        time,
        origin,
        destination,
        choice,
        impedance,
    )
}

data class ModeChoiceAlternative(
    val person: PersonAgent,
    val time: AbsoluteTime,
    val origin: Location,
    val destination: Location,
    val mode: Mode,
    val impedance: Metrics,
) : ChoiceAlternative<Mode>() {
    override val choice: Mode get() = mode
}

data class SituativeAvailability(val modes: Set<Mode>, val resources: Set<Any>)

/**
 * ModeAvailabilityFilter is a [ChoiceFilter] for [ModeChoiceAlternative]s
 * that additionally breaks down availability into three parts:
 *  - static availability refers to the modes generally available to a person
 *    based on their attributes like age, license, etc.
 *  - situative availability refers to the modes available for a person in their current situation
 *    before a choice of destination or mode is performed e.g. including dynamic availability information
 *    on shared vehicles. This also determines which mode related (shared) resources might be affected
 *  - alternative-dependent availability refers to modes available in a given [ModeChoiceAlternative]
 *    with a given destination e.g., filtering out bike sharing if there is no station at the destination
 *
 * This distinction allows to reuse the model at different stages of decision-making.
 * The three stages should be implemented building on top of each other: alt. dep. > uses > situative > uses > static.
 * Also, static availability could be precomputed per person and situative availability per choice situation
 * to reduce computation time.
 *
 *
 * @constructor Create empty Mode availability filter
 */
interface ModeAvailabilityFilter : ChoiceFilter<ModeChoiceAlternative> {

    /**
     * Filter the given set of [ModeChoiceAlternative]s by applying the alternative dependent availability rules.
     *
     * @param choices the full choice set to be filtered
     * @return a filtered set of ModeChoiceAlternatives
     */
    override fun filter(choices: Set<ModeChoiceAlternative>): Set<ModeChoiceAlternative> {
        return choices.filter { alternativeDependentAvailability(it) }.toSet()
    }

    /**
     * Computes static mode availability for the given person:
     * i.e., the modes generally available to a person based on their attributes like age, license, etc.
     *
     * @param person the person to compute the static mode availability for
     * @return a reduced choice set of mode
     */
    fun staticAvailability(person: IPerson): Set<Mode>

    /**
     * Computes the situative availability (available modes and possibly affected shared resources)
     * of the given person agent in their current situation.
     *
     * @param person the person to compute the situative mode availability for
     * @return a reduced choice set of mode
     */
    fun situativeAvailability(person: PersonAgent): SituativeAvailability

    /**
     * Checks the availability of a given mode choice alternative.
     *
     * @param alternative the mode choice alternative to be checked for availability
     * @return whether the given mode choice alternative is available
     */
    fun alternativeDependentAvailability(alternative: ModeChoiceAlternative): Boolean
    //TODO refactor when discrete-choice lib refactored alternatives:  choiceSituationBasedAvail(sit): Set<Mode>


} // TODO implementation using composite of rules, caching of reduced choice sets in person data and choice situation

fun interface BikeSharingConnectionSelector {
    fun findConnection(alternative: ModeChoiceAlternative): Pair<SharingStationAgent, SharingStationAgent>?
}

class AvailabilityModelWithSharing(
    val modes: ChoiceModelModes,
    private val providersByMode: Map<Mode, Set<SharingProviderId>>,
    private val metrics: Metrics,
) : ModeAvailabilityFilter, BikeSharingConnectionSelector {

    override fun staticAvailability(person: IPerson): Set<Mode> {
        val choiceSet = mutableSetOf<Mode>()
        choiceSet.addAll(modes.options)

        context(person) {
            choiceSet.removeIfNot(::hasCarStatic, modes.car)
            choiceSet.removeIfNot(::hasBikeStatic, modes.bike)
            choiceSet.removeIfNot(::hasCssbStatic, modes.carSharingStation)
            choiceSet.removeIfNot(::hasCsffStatic, modes.carSharingFree)
            choiceSet.removeIfNot(::hasPoolingStatic, modes.ridePooling)
            choiceSet.removeIfNot(::hasBikeSharingStatic, modes.bikeSharing)
        }

        return choiceSet
    }

    override fun situativeAvailability(person: PersonAgent): SituativeAvailability {
        val choiceSet = mutableSetOf<Mode>()
        val resources = mutableSetOf<Any>()

        context(person, resources) {

            val staticAvailability = staticAvailability(person)
            staticAvailability.forEach { //Apply cached long-term choice set here
                when(it) {
                    modes.car -> choiceSet.addIf(::isCarAvailable, modes.car)
                    modes.bikeSharing -> choiceSet.addCollectionIf(::isBikeSharingAvailable, modes.bikeSharing)
                    else -> when {
                        it.requiresVehicleTakeAlong -> choiceSet.addIf(it::isFixedModeAvailable, it)
                        else -> choiceSet.addIf(::isFlexModeAvailable, it)
                    }
                }
            }

        }

        return SituativeAvailability(choiceSet, resources)
    }

    override fun alternativeDependentAvailability(alternative: ModeChoiceAlternative): Boolean {
        val situativeAvailability = situativeAvailability(alternative.person) //TODO use cached situative avail
        if (alternative.mode !in situativeAvailability.modes) {
            return false
        }

        return when(alternative.mode) {
            modes.bikeSharing -> isBikesharingAvailableForAlternative(alternative)
            else -> true
        }
    }



    private fun hasCarStatic(person: IPerson) =
        person.hasLicense && person.household.cars.isNotEmpty()

    private fun hasBikeStatic(person: IPerson) =
        person.hasBike

    private fun hasCssbStatic(person: IPerson) =
        person.hasLicense &&
            providersByMode[modes.carSharingStation]?.any { it in person.sharingMembershipIds } ?: false

    private fun hasCsffStatic(person: IPerson) =
        person.hasLicense &&
            providersByMode[modes.carSharingFree]?.any { it in person.sharingMembershipIds } ?: false

    private fun hasPoolingStatic(person: IPerson) =
        providersByMode[modes.ridePooling]?.any { it in person.sharingMembershipIds } ?: false
    // TODO person should have pooling memberships, this is not sharing or general service memberships

    private fun hasBikeSharingStatic(person: IPerson) =
        providersByMode[modes.bikeSharing]?.any { it in person.sharingMembershipIds } ?: false


    private fun isCarAvailable(person: PersonAgent): PrivateCarAgent? =
        person.getBestCarOrNull() //availability definition in other file :(

    private fun isBikeSharingAvailable(person: PersonAgent): Collection<SharingStationAgent>? =
        takeIf { isFlexModeAvailable(person) }?.let {
            person.sharingMemberships.filter {
                it.id in (providersByMode[modes.bikeSharing] ?: emptySet())
            }.flatMap {
                it.stations
            }.filter {
                it.zonesByFoot.any { zone -> person.location in zone }
            }
        }

    private fun isBikesharingAvailableForAlternative(alternative: ModeChoiceAlternative) =
        findConnection(alternative) != null

    //TODO can be based on ChoiceSituation instead of alternative after refactoring in discrete-choice lib
    override fun findConnection(alternative: ModeChoiceAlternative): Pair<SharingStationAgent, SharingStationAgent>? =
        alternative.findStartEndStation()

    private fun ModeChoiceAlternative.findStartEndStation(): Pair<SharingStationAgent, SharingStationAgent>? {
        val memberStations = person.sharingMemberships.filter {
            it.id in (providersByMode[modes.bikeSharing] ?: emptySet())
        }.flatMap {
            it.stations
        }

        val startStation =
            memberStations.filter {
                it.zonesByFoot.any { zone -> origin in zone }
            }.filter {
                it.hasAvailableVehicles
            }.minByOrNull {
                metrics.distance(it.location, destination, modes.bikeSharing)
            }

        val endStation =
            memberStations.filter {
                it.zonesByFoot.any { zone -> destination in zone }
            }.filter {
                it != startStation
            }.minByOrNull {
                metrics.distance(it.location, destination, modes.bikeSharing)
            }

        return startStation?.let { start ->
            endStation?.let { end ->
                start to end
            }
        }
    }

    context(person: IPerson)
    private fun MutableSet<Mode>.removeIfNot(condition: (IPerson) -> Boolean, mode: Mode) {
        if (!condition(person)) {
            this.remove(mode)
        }
    }

    private context(element: X)
    fun <X> MutableSet<Mode>.addIf(condition: (X) -> Boolean, mode: Mode) {
        if (condition(element)) {
            this.add(mode)
        }
    }

    private context(person: PersonAgent, resources: MutableSet<Any>)
    fun <X: Any> MutableSet<Mode>.addIf(condition: (PersonAgent) -> X?, mode: Mode, adder: (MutableSet<Any>, X) -> Unit = MutableSet<Any>::add) {

        condition(person)?.let {
            this.add(mode)
            adder(resources, it)
        }

    }

    private context(person: PersonAgent, resources: MutableSet<Any>)
    fun MutableSet<Mode>.addCollectionIf(condition: (PersonAgent) -> Collection<Any>?, mode: Mode) =
        this.addIf(condition, mode, MutableSet<Any>::addAll)

}

private fun isFlexModeAvailable(person: PersonAgent) =
    isHome(person) || prevModeIsFlexible(person)

private fun prevModeIsFlexible(person: PersonAgent): Boolean =
    (person.lastTransportMode()?.requiresVehicleTakeAlong?.not() ?: true)

private fun Mode.isFixedModeAvailable(person: PersonAgent) =
    isHome(person) || (person.lastTransportMode() == this)

private fun isHome(person: PersonAgent): Boolean = person.location == person.household.location
