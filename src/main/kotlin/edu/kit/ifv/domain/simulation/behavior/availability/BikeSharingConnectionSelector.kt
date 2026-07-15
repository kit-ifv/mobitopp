package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.SharingStationAgent

fun interface BikeSharingConnectionSelector {
    context(impedance: Impedance)
    fun findConnection(
        person: PersonAgent,
        destination: StandardLocation,
    ): Pair<SharingStationAgent, SharingStationAgent>?
}
