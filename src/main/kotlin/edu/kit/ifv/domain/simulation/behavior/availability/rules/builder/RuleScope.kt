package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
import edu.kit.ifv.domain.simulation.agent.DrtRequest
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.SharingProviderAgent
import edu.kit.ifv.domain.simulation.agent.SharingStationAgent
import edu.kit.ifv.domain.simulation.agent.isHome
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.NoResourceMode
import edu.kit.ifv.domain.simulation.behavior.availability.PoolingResource
import edu.kit.ifv.domain.simulation.behavior.availability.ProviderAvailability
import edu.kit.ifv.domain.simulation.behavior.availability.SharingFreeResource
import edu.kit.ifv.domain.simulation.behavior.availability.SharingStationResource
import edu.kit.ifv.domain.simulation.behavior.availability.available
import edu.kit.ifv.domain.simulation.behavior.availability.notAvailable
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.units.Distance
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * Scope for defining static availability rules.
 * @property mode the transport mode
 * @property person the person for whom to check availability
 */
class StaticRuleScope(val mode: Mode, val person: IPerson)

/**
 * Common interface for scopes that have access to the agent and the choice situation.
 */
interface AgentRuleScope {
    /** The transport mode. */
    val mode: Mode
    /** The current simulation time. */
    val time: AbsoluteTime
    /** The person agent making the choice. */
    val agent: PersonAgent
    /** The potential destination. */
    val destination: StandardLocation

    /** Sharing providers the agent is a member of for the current [mode]. */
    val sharingProviders get() = agent.sharingMemberships.filter { it.mode == mode }
    /** DRT providers the agent is a member of for the current [mode]. */
    val drtProviders get() = agent.drtMemberships.filter { it.mode == mode }

    /** Filters providers that are currently operating. */
    fun List<SharingProviderAgent>.checkOperatingHours() = filter { time.hour in it.operatingHours }
    /** Returns all stations of the given providers. */
    fun List<SharingProviderAgent>.stations() = flatMap { it.stations }
    /** Filters stations where the agent is currently in a foot zone. */
    fun List<SharingStationAgent>.checkAgentInFootZones() = filter {
        it.zonesByFoot.any { zone -> agent.location in zone }
    }
    /** Filters stations within a certain [radius] from the agent's location. */
    fun List<SharingStationAgent>.checkAgentInRadius(radius: Distance, pedestrian: Mode, impedance: Impedance) =
        filter { impedance.distance(it.location, agent.location, pedestrian) <= radius }

    /** Filters starting stations where at least one other station of the same provider is near the [destination]. */
    fun List<SharingStationAgent>.checkDestinationInFootZones() = filter { start ->
        start.owner.stations
            .filter { it != start }
            .any { s -> s.zonesByFoot.any { z -> destination in z } }
    }

    /** Filters starting stations where at least one other station of the same provider is within [radius] of the [destination]. */
    fun List<SharingStationAgent>.checkDestinationInRadius(radius: Distance, pedestrian: Mode, impedance: Impedance) =
        filter { start ->
            start.owner.stations
                .filter { it != start }
                .any { s -> impedance.distance(s.location, destination, pedestrian) <= radius }
        }

    /** Filters DRT providers that operate at the current [time] between [agent]'s location and [destination]. */
    fun List<DrtProviderAgent>.checkOperatingHoursAndArea() =
        filter { it.operatesAt(time, agent.location, destination) }
}

/**
 * Scope for defining provider availability rules.
 */
class ProviderRuleScope(
    override val mode: Mode,
    override val agent: PersonAgent,
    override val time: AbsoluteTime,
    override val destination: StandardLocation,
) : AgentRuleScope {
    /** Creates a [ProviderAvailability] indicating availability from the given [resources]. */
    fun available(resources: Collection<Any>): ProviderAvailability = mode.available(resources)
    /** Creates a [ProviderAvailability] indicating availability from the given [resources]. */
    fun available(vararg resources: Any): ProviderAvailability = mode.available(resources)
    /** Creates a [ProviderAvailability] indicating that the mode is not available. */
    fun notAvailable(): ProviderAvailability = mode.notAvailable
    /** Returns true if the agent is already using a resource for this [mode]. */
    fun modeAlreadyInUse(): Boolean = agent.modeResource?.let { it.mode == mode } ?: false

    /**
     * Default rule for home-based vehicles: available if at home, or if the vehicle is already in use.
     */
    fun homeBasedVehicleRule(useProvider: Boolean) = if (agent.isHome()) {
        if (useProvider) {
            available(agent.household)
        } else {
            available()
        }
    } else if (modeAlreadyInUse()) {
        available()
    } else {
        notAvailable()
    }

    /** Returns [available] if the list is not empty, otherwise [notAvailable]. */
    fun List<Any>.checkAnyAvailable() = takeIf { it.isNotEmpty() }
        ?.let { available(it) }
        ?: notAvailable()
}

/**
 * Scope for defining resource availability rules.
 */
class ResourceRuleScope(override val mode: Mode, val characteristics: ModeChoiceCharacteristics) : AgentRuleScope {
    override val agent: PersonAgent get() = characteristics.person
    override val time: AbsoluteTime get() = characteristics.time
    override val destination: StandardLocation get() = characteristics.destination

    /** Returns a [NoResourceMode] indicating that the mode is available without a specific resource. */
    fun availableWithoutResource(): ModeResource = NoResourceMode(mode)
    /** Indicates that the resource is unavailable. */
    val resourceUnavailable: ModeResource? = null

    /** Filters stations that have at least one vehicle available. */
    fun List<SharingStationAgent>.checkVehiclesAvailable() = filter { it.hasAvailableVehicles }

    /** Selects the station with the minimum distance from the agent. */
    fun List<SharingStationAgent>.selectStationByMinDistance(pedestrian: Mode, impedance: Impedance) =
        minByOrNull { impedance.distance(agent.location, it.location, pedestrian) }
            ?.let { SharingStationResource(mode, it) }

    /** Selects the station with the minimum travel time from the agent. */
    fun List<SharingStationAgent>.selectStationByMinTravelTime(pedestrian: Mode, impedance: Impedance) =
        minByOrNull { impedance.duration(agent.location, it.location, pedestrian, time) }
            ?.let { SharingStationResource(mode, it) }

    /** Selects a start and end station for one-way sharing based on minimum total distance. */
    fun List<SharingStationAgent>.selectMinDistOneWaySharing(pedestrian: Mode, impedance: Impedance) =
        mapNotNull { start ->
            start.owner.stations
                .filter { it != start }
                .filter { s -> s.zonesByFoot.any { z -> destination in z } }
                .map {
                    it to (
                        impedance.distance(start.location, it.location, mode) +
                            impedance.distance(it.location, destination, pedestrian)
                        )
                }
                .minByOrNull { it.second } // best end station for this start
                ?.let { start to it }
        }
            .minByOrNull { (start, end) ->
                // best overall incl. walk to start
                end.second + impedance.distance(agent.location, start.location, pedestrian)
            }
            ?.let { (start, minEnd) -> SharingFreeResource(mode, start, minEnd.first) }

    /** Requests and selects the best ride offer from DRT providers based on minimum duration. */
    fun List<DrtProviderAgent>.selectRideOfferByMinDuration() = mapNotNull {
        it.requestRide(DrtRequest(it, agent, time, time, agent.location, destination))
    }.run {
        val best = minByOrNull { it.totalDuration }
        filter { it != best }.forEach { it.providerAgent.algorithm.revokeOffer(it) }
        best
    }?.let {
        PoolingResource(mode, it)
    }
}
