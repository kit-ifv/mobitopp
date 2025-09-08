package domain.simulation.events

import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.schedule.LinkTrip
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.simulation.behavior.BikeSharingConnectionSelector
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeAvailabilityFilter
import domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import utils.units.Time

fun interface GenerateDestinationCharacteristics<out T> {
    operator fun invoke(
        person: PersonAgent,
        time: Time,
        behavior: PersonBehavior,
        legs: LinkTrip,
    ): T
}

fun interface GenerateModeCharacteristics<out T> {
    operator fun invoke(
        person: PersonAgent,
        time: Time,
        behavior: PersonBehavior,
        origin: Location,
        destination: Location,
    ): T
}

val StandardDestinationImplementation =
    GenerateDestinationCharacteristics<DestinationChoiceCharacteristics> { person, time, behavior, legs ->
        DestinationChoiceCharacteristics(
            person,
            time,
            legs.elements.last().startLocation,
            behavior.impedance,
            behavior.attractivityModel,
            behavior.availabilityModel
        )
    }

val StandardModeImplementation =
    GenerateModeCharacteristics<ModeChoiceCharacteristics> { person, time, behavior, origin, destination ->
        ModeChoiceCharacteristics(
            person,
            time,
            origin,
            destination,
            behavior.impedance,
        )
    }

data class PersonBehavior(
    val destinationChoice: FixedChoiceModel<Location, DestinationChoiceCharacteristics>,
    val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    val modes: ChoiceModelModes,
    val impedance: Metrics,
    val attractivityModel: AttractivenessModel,
    val availabilityModel: ModeAvailabilityFilter,
    val bikeSharingConnectionSelector: BikeSharingConnectionSelector,
    val spawnDestinationCharacteristics: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>,
    val spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics>,
) {
    companion object {
        @Suppress("LongParameterList")
        fun from(
            impedance: Metrics,
            destinationChoice: FixedChoiceModel<Location, DestinationChoiceCharacteristics>,
            modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
            choiceModelModes: ChoiceModelModes,
            attractivenessModel: AttractivenessModel,
            modeAvailability: ModeAvailabilityFilter,
            bikeSharingConnectionSelector: BikeSharingConnectionSelector,
        ): PersonBehavior {
            return PersonBehavior(
                destinationChoice,
                modeChoice,
                choiceModelModes,
                impedance,
                attractivenessModel,
                modeAvailability,
                bikeSharingConnectionSelector,
                StandardDestinationImplementation,
                StandardModeImplementation,
            )
        }
    }
}
