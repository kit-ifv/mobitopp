package domain.simulation.behavior

import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingStationAgent
import domain.simulation.agent.getBestCarOrNull
import domain.simulation.agent.lastTransportMode
import domain.synthesis.data.IPerson
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.sharingMembershipIds
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import utils.units.AbsoluteTime
import kotlin.random.Random

interface DestinationChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: Location
    val impedance: Metrics
    val attractivityModel: AttractivenessModel
    val modeAvailabilityFilter: ChoiceFilter<Mode, ModeChoiceCharacteristics>

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: Location,
            impedance: Metrics,
            attractivityModel: AttractivenessModel,
            modeAvailabilityFilter: ChoiceFilter<Mode, ModeChoiceCharacteristics>,
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

    companion object {
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: Location,
            destination: Location,
            impedance: Metrics,
        ): ModeChoiceCharacteristics = ModeChoiceCharacteristicsImpl(
            person,
            time,
            origin,
            destination,
            impedance,
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
    val origin: Location,
    val destination: Location,
    val choice: Mode,
    val impedance: Metrics,
)

data class ResourceAvailability(val mode: Mode?, val resources: Collection<Any> = emptyList()) {
    val isAvailable: Boolean = (mode != null)
}
val notAvailable = ResourceAvailability(null, emptyList())
fun available(mode: Mode, resources: Collection<Any> = emptyList()) = ResourceAvailability(mode, resources)

fun Collection<ResourceAvailability>.flatten() = filter {
    it.isAvailable
}.run {
    val modes = mapNotNull { it.mode }
    val resources = flatMap { it.resources }
    modes to resources
}

context(agent: PersonAgent)
fun ModeAvailabilityFilter.currentlyAffectedResources(modes: Collection<Mode>): List<Any> =
    modes.flatMap { currentAvailability(it).resources }

/**
 * ModeAvailabilityFilter is a [ChoiceFilter] for [ModeChoiceAlternative]s
 * that additionally breaks down availability into three parts:
 *  - static availability refers to the modes generally available to a person
 *    based on their attributes like age, license, etc.
 *  - current availability refers to the modes available for a person in their current situation
 *    before a choice of destination or mode is performed e.g. including dynamic availability information
 *    on shared vehicles. This also determines which mode related (shared) resources might be affected
 *  - choice availability refers to modes available in a choice situation defined by [ModeChoiceCharacteristics]
 *    with a selected destination e.g., filtering out bike sharing if there is no station at the destination
 *
 * This distinction allows to reuse the model at different stages of decision-making.
 * The three stages should be implemented building on top of each other: choice > uses > current > uses > static.
 * Also, static availability could be precomputed per person and current availability per choice
 * situation/characteristics to reduce computation time.
 *
 */
interface ModeAvailabilityFilter : ChoiceFilter<Mode, ModeChoiceCharacteristics> {

    context(characteristics: ModeChoiceCharacteristics)
    override fun filter(alternative: Mode): Boolean =
        choiceAvailability(alternative)


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
     * Computes the current availability (availability and possibly affected shared resources)
     * of the given mode for the person agent in their current situation (given in the context).
     *
     * @param mode the mode for which to check availability and affected resources
     * @receiver agent the person to compute the situative mode availability for
     * @return availability and possibly affected resources
     */
    context(agent: PersonAgent)
    fun currentAvailability(mode: Mode): ResourceAvailability

    /**
     * Checks the availability of a given mode choice alternative
     * in the context of a choice situation defined by [ModeChoiceCharacteristics].
     *
     * @param mode the mode to be checked for availability
     * @receiver characteristics the characteristics of the mode choice situation
     * @return whether the given mode is available
     */
    context(characteristics: ModeChoiceCharacteristics)
    fun choiceAvailability(mode: Mode): Boolean

} // TODO implementation using composite of rules, caching of reduced choice sets in person data and choice situation

fun interface BikeSharingConnectionSelector {
    fun findConnection(characteristics: ModeChoiceCharacteristics): Pair<SharingStationAgent, SharingStationAgent>?
}

class AvailabilityModelWithSharing(
    val modes: ChoiceModelModes,
    private val providersByMode: Map<Mode, Set<SharingProviderId>>,
    private val metrics: Metrics,
) : ModeAvailabilityFilter, BikeSharingConnectionSelector {

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

    context(agent: PersonAgent)
    override fun currentAvailability(mode: Mode): ResourceAvailability =
        takeIf {
            staticAvailability(mode) //TODO use cached static availability of agent
        }?.let {
            when (mode) {
                modes.car -> isCarCurrentlyAvailable(agent)
                modes.bikeSharing -> isBikeSharingCurrentlyAvailable(agent)
                else -> when {
                    mode.requiresVehicleTakeAlong -> mode.isFixedModeCurrentlyAvailable(agent)
                    else -> mode.isFlexModeCurrentlyAvailableI(agent)
                }
            }
        } ?: notAvailable

    context(characteristics: ModeChoiceCharacteristics)
    override fun choiceAvailability(mode: Mode): Boolean =
        context(characteristics.person) {
            currentAvailability(mode).isAvailable //TODO use cached situative availability of agent
        } && when (mode) {
            modes.bikeSharing -> isBikesharingAvailableForChoice(characteristics)
            else -> true
        }

    // Static availability
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

    // Current availability
    private fun isCarCurrentlyAvailable(person: PersonAgent): ResourceAvailability =
        person.getBestCarOrNull()?.let { available(modes.car, setOf(it)) } ?: notAvailable
    //availability definition in other file :(

    private fun isBikeSharingCurrentlyAvailable(person: PersonAgent): ResourceAvailability =
        takeIf { isHome(person) || prevModeIsFlexible(person) }?.let {
            person.sharingMemberships.filter {
                it.id in (providersByMode[modes.bikeSharing] ?: emptySet())
            }.flatMap {
                it.stations
            }.filter {
                it.zonesByFoot.any { zone -> person.location in zone }
            }
        }?.let { available(modes.bikeSharing, it) } ?: notAvailable

    private fun Mode.isFlexModeCurrentlyAvailableI(person: PersonAgent) =
        takeIf { isHome(person) || prevModeIsFlexible(person) }?.let { available(this) } ?: notAvailable

    private fun Mode.isFixedModeCurrentlyAvailable(person: PersonAgent) =
        takeIf { isHome(person) || (person.lastTransportMode() == this) }?.let { available(this) } ?: notAvailable

    private fun prevModeIsFlexible(person: PersonAgent): Boolean =
        (person.lastTransportMode()?.requiresVehicleTakeAlong?.not() ?: true)

    private fun isHome(person: PersonAgent): Boolean = person.location == person.household.location


    //Choice availability
    private fun isBikesharingAvailableForChoice(characteristics: ModeChoiceCharacteristics) =
        findConnection(characteristics) != null

    override fun findConnection(characteristics: ModeChoiceCharacteristics): Pair<SharingStationAgent, SharingStationAgent>? =
        characteristics.findStartEndStation()

    private fun ModeChoiceCharacteristics.findStartEndStation(): Pair<SharingStationAgent, SharingStationAgent>? {
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

}

