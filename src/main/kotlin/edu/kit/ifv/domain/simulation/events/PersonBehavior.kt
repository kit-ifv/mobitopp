package edu.kit.ifv.domain.simulation.events
import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
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
            legs.elements.last().startLocation
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

@Deprecated("Replaced with context")
data class PersonBehavior constructor(
      val temp: Unit,
//    val destinationChoice: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
//    val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
//    val modes: ChoiceModelModes,
//    val impedance: Impedance,
//    val attractivityModel: AttractivenessModel,
//    val availabilityModel: ModeAvailabilityModel,
//    val bikeSharingConnectionSelector: BikeSharingConnectionSelector,
//    val drtAvailabilitySelector: DrtAvailabilitySelector,
//    val spawnDestinationCharacteristics: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>,
//    val spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics>,
//    val replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
) {
//    companion object {
//        @Suppress("LongParameterList")
//        fun from(
//            impedance: Impedance,
//            destinationChoice: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
//            modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
//            choiceModelModes: ChoiceModelModes,
//            attractivenessModel: AttractivenessModel,
//            modeAvailability: ModeAvailabilityModel,
//            bikeSharingConnectionSelector: BikeSharingConnectionSelector,
//            drtAvailabilitySelector: DrtAvailabilitySelector,
//            replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
//        ): PersonBehavior = PersonBehavior(
//            destinationChoice,
//            modeChoice,
//            choiceModelModes,
//            impedance,
//            attractivenessModel,
//            modeAvailability,
//            bikeSharingConnectionSelector,
//            drtAvailabilitySelector,
//            StandardDestinationImplementation,
//            StandardModeImplementation,
//            replanningStrategy,
//        )
//    }
}
