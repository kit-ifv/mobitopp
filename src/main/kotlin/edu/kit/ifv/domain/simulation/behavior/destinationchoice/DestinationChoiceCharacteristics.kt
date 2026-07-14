package edu.kit.ifv.domain.simulation.behavior.destinationchoice

import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.random.Random

interface DestinationChoiceCharacteristics {
    val person: PersonAgent
    val time: AbsoluteTime
    val origin: StandardLocation

    companion object {
        @Suppress("LongParameterList")
        operator fun invoke(
            person: PersonAgent,
            time: AbsoluteTime,
            origin: StandardLocation,
        ): DestinationChoiceCharacteristics = DestinationChoiceCharacteristicsImpl(
            person,
            time,
            origin,
        )
    }
}

fun DestinationChoiceCharacteristics.with(choice: StandardLocation) = DestinationAlternative(this, choice)

data class DestinationChoiceCharacteristicsImpl(
    override val person: PersonAgent,
    override val time: AbsoluteTime,
    override val origin: StandardLocation,
) : DestinationChoiceCharacteristics {

    val random: Random
        get() = person.random
}

data class DestinationAlternative(val original: DestinationChoiceCharacteristics, val choice: StandardLocation) :
    DestinationChoiceCharacteristics by original