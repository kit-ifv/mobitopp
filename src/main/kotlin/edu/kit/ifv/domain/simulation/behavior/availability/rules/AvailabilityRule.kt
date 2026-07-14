package edu.kit.ifv.domain.simulation.behavior.availability.rules

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.ProviderAvailability
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.utils.units.AbsoluteTime

interface AvailabilityRule {
    val mode: Mode

    fun staticAvailability(person: IPerson): Boolean

    fun providerAvailability(
        agent: PersonAgent,
        time: AbsoluteTime,
        destination: StandardLocation
    ): ProviderAvailability

    fun resourceAvailability(
        characteristics: ModeChoiceCharacteristics,
    ): ModeResource?

}
