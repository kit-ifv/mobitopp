package edu.kit.ifv.domain.simulation.events
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.utils.units.Time

fun interface GenerateDestinationCharacteristics<out T> {
    operator fun invoke(person: PersonAgent, time: Time, legs: LinkTrip): T
}

fun interface GenerateModeCharacteristics<out T> {
    @Suppress("LongParameterList")
    operator fun invoke(
        person: PersonAgent,
        time: Time,
        origin: StandardLocation,
        destination: StandardLocation,
        currentChoices: Collection<Mode>,
        custom: Any?,
    ): T
}

val StandardDestinationImplementation: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>
    get() = GenerateDestinationCharacteristics { person, time, legs ->
        DestinationChoiceCharacteristics(
            person,
            time,
            legs.elements.last().startLocation,
        )
    }

val StandardModeImplementation: GenerateModeCharacteristics<ModeChoiceCharacteristics>
    get() = GenerateModeCharacteristics {
            person,
            time,
            origin,
            destination,
            currentChoices,
            custom,
        ->
        ModeChoiceCharacteristics(
            person,
            time,
            origin,
            destination,
            currentChoices,
            custom,
        )
    }
