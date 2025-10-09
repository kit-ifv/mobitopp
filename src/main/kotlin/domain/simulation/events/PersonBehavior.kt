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
import domain.simulation.behavior.DrtAvailabilitySelector
import domain.simulation.behavior.ModeAvailabilityModel
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
    @Suppress("LongParameterList")
    operator fun invoke(
        person: PersonAgent,
        time: Time,
        behavior: PersonBehavior,
        origin: Location,
        destination: Location,
        currentChoices: Collection<Mode>,
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
            behavior.availabilityModel.asProviderAvailabilityFilter()
        )
    }

val StandardModeImplementation =
    GenerateModeCharacteristics<ModeChoiceCharacteristics> {
            person, time, behavior, origin, destination, currentChoices ->
        ModeChoiceCharacteristics(
            person,
            time,
            origin,
            destination,
            behavior.impedance,
            currentChoices,
        )
    }

data class PersonBehavior(
    val destinationChoice: FixedChoiceModel<Location, DestinationChoiceCharacteristics>,
    val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    val modes: ChoiceModelModes,
    val impedance: Metrics,
    val attractivityModel: AttractivenessModel,
    val availabilityModel: ModeAvailabilityModel,
    val bikeSharingConnectionSelector: BikeSharingConnectionSelector,
    val drtAvailabilitySelector: DrtAvailabilitySelector,
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
            modeAvailability: ModeAvailabilityModel,
            bikeSharingConnectionSelector: BikeSharingConnectionSelector,
            drtAvailabilitySelector: DrtAvailabilitySelector,
        ): PersonBehavior {
            return PersonBehavior(
                destinationChoice,
                modeChoice,
                choiceModelModes,
                impedance,
                attractivenessModel,
                modeAvailability,
                bikeSharingConnectionSelector,
                drtAvailabilitySelector,
                StandardDestinationImplementation,
                StandardModeImplementation,
            )
        }
    }
}
