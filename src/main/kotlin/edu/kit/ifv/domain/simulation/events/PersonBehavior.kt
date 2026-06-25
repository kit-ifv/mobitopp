package edu.kit.ifv.domain.simulation.events
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.IndexAddressableImpedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.BikeSharingConnectionSelector
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.DrtAvailabilitySelector
import edu.kit.ifv.domain.simulation.behavior.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.utils.units.Time

fun interface GenerateDestinationCharacteristics<out T> {
    operator fun invoke(person: PersonAgent, time: Time, behavior: PersonBehavior, legs: LinkTrip): T
}

fun interface GenerateModeCharacteristics<out T> {
    @Suppress("LongParameterList")
    operator fun invoke(
        person: PersonAgent,
        time: Time,
        behavior: PersonBehavior,
        origin: StandardLocation,
        destination: StandardLocation,
        currentChoices: Collection<Mode>,
        custom: Any?,
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
            behavior.availabilityModel.asProviderAvailabilityFilter(),
        )
    }

val StandardModeImplementation =
    GenerateModeCharacteristics<ModeChoiceCharacteristics> {
            person,
            time,
            behavior,
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
            behavior.impedance,
            currentChoices,
            custom,
        )
    }

data class PersonBehavior constructor(
    val destinationChoice: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    val modes: ChoiceModelModes,
    val impedance: IndexAddressableImpedance,
    val attractivityModel: AttractivenessModel,
    val availabilityModel: ModeAvailabilityModel,
    val bikeSharingConnectionSelector: BikeSharingConnectionSelector,
    val drtAvailabilitySelector: DrtAvailabilitySelector,
    val spawnDestinationCharacteristics: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>,
    val spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics>,
    val replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
) {
    companion object {
        @Suppress("LongParameterList")
        fun from(
            impedance: IndexAddressableImpedance,
            destinationChoice: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
            modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
            choiceModelModes: ChoiceModelModes,
            attractivenessModel: AttractivenessModel,
            modeAvailability: ModeAvailabilityModel,
            bikeSharingConnectionSelector: BikeSharingConnectionSelector,
            drtAvailabilitySelector: DrtAvailabilitySelector,
            replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
        ): PersonBehavior = PersonBehavior(
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
            replanningStrategy,
        )
    }
}
