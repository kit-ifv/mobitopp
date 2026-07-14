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

class StaticRuleScope(val mode: Mode, val person: IPerson)

interface AgentRuleScope {
    val mode: Mode
    val time: AbsoluteTime
    val agent: PersonAgent
    val destination: StandardLocation

    val sharingProviders get() = agent.sharingMemberships.filter { it.mode == mode }
    val drtProviders get() = agent.drtMemberships.filter { it.mode == mode }

    fun List<SharingProviderAgent>.checkOperatingHours() = filter { time.hour in it.operatingHours }
    fun List<SharingProviderAgent>.stations() = flatMap { it.stations }
    fun List<SharingStationAgent>.checkAgentInFootZones() = filter { it.zonesByFoot.any { zone -> agent.location in zone } }
    fun List<SharingStationAgent>.checkAgentInRadius(radius: Distance, pedestrian: Mode, impedance: Impedance) =
        filter { impedance.distance(it.location, agent.location, pedestrian) <= radius }

    fun List<SharingStationAgent>.checkDestinationInFootZones() =
        filter { start ->
            start.owner.stations
                .filter { it != start }
                .any { s -> s.zonesByFoot.any { z -> destination in z } }
        }

    fun List<SharingStationAgent>.checkDestinationInRadius(radius: Distance, pedestrian: Mode, impedance: Impedance) =
        filter { start ->
            start.owner.stations
                .filter { it != start }
                .any { s -> impedance.distance(s.location, destination, pedestrian) <= radius }
        }

    fun List<DrtProviderAgent>.checkOperatingHoursAndArea() =
        filter { it.operatesAt(time, agent.location, destination) }



}

class ProviderRuleScope(
    override val mode: Mode,
    override val agent: PersonAgent,
    override val time: AbsoluteTime,
    override val destination: StandardLocation
): AgentRuleScope {
    fun available(vararg resources: Any): ProviderAvailability = mode.available(resources)
    fun notAvailable(): ProviderAvailability = mode.notAvailable
    fun modeAlreadyInUse(): Boolean = agent.modeResource?.let { it.mode == mode } ?: false

    fun homeBasedVehicleRule() =
        if (agent.isHome()) {
            available(agent.household)
        } else if (modeAlreadyInUse()) {
            available()
        } else {
            notAvailable()
        }

    fun List<Any>.checkAnyAvailable() = takeIf { it.isNotEmpty() }
        ?.let { available(it) }
        ?: notAvailable()
}

class ResourceRuleScope(
    override val mode: Mode,
    val characteristics: ModeChoiceCharacteristics
): AgentRuleScope {
    override val agent: PersonAgent get() = characteristics.person
    override val time: AbsoluteTime get() = characteristics.time
    override val destination: StandardLocation get() = characteristics.destination

    fun availableWithoutResource(): ModeResource = NoResourceMode(mode)
    fun resourceUnavailable(): ModeResource? = null

    fun List<SharingStationAgent>.checkVehiclesAvailable() = filter { it.hasAvailableVehicles }

    fun List<SharingStationAgent>.selectStationByMinDistance(pedestrian: Mode, impedance: Impedance) =
        minByOrNull { impedance.distance(agent.location, it.location, pedestrian) }
            ?.let { SharingStationResource(mode, it) }

    fun List<SharingStationAgent>.selectStationByMinTravelTime(pedestrian: Mode, impedance: Impedance) =
        minByOrNull { impedance.duration(agent.location, it.location, pedestrian, time) }
            ?.let { SharingStationResource(mode, it) }

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
                .minByOrNull { it.second }        // best end station for this start
                ?.let { start to it }
        }
        .minByOrNull { (start, end) ->            // best overall incl. walk to start
            end.second + impedance.distance(agent.location, start.location, pedestrian)
        }
        ?.let { (start, minEnd) -> SharingFreeResource(mode, start, minEnd.first) }

    fun List<DrtProviderAgent>.selectRideOfferByMinDuration() =
        mapNotNull {
            it.requestRide(DrtRequest(it, agent, time, time, agent.location, destination))
        }.run {
            val best = minByOrNull { it.totalDuration }
            filter { it != best }.forEach { it.providerAgent.algorithm.revokeOffer(it) }
            best
        }?.let {
            PoolingResource(mode, it)
        }


}