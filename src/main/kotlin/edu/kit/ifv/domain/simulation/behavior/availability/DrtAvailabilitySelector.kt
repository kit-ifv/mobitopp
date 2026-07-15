package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.DrtOffer
import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.utils.units.AbsoluteTime

interface DrtAvailabilitySelector {
    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent>

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    fun findDrtOffers(): List<DrtOffer>
}
