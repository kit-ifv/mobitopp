package domain.simulation.events

import discreteChoice.models.FixedChoicesModel
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.BikeSharingConnectionSelector
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.ModeAvailabilityFilter
import domain.simulation.behavior.ModeChoiceAlternative

data class PersonBehavior(
    val destinationChoice: FixedChoicesModel<DestinationAlternative, Location>,
    val modeChoice: FixedChoicesModel<ModeChoiceAlternative, Mode>,
    val impedance: Metrics,
    val attractivityModel: AttractivenessModel,
    val availabilityModel: ModeAvailabilityFilter,
    val bikeSharingConnectionSelector: BikeSharingConnectionSelector,
    val choiceModelModes: ChoiceModelModes,
) {

    companion object {
        @Suppress("LongParameterList")
        fun from(
            impedance: Metrics,
            destinationChoice: FixedChoicesModel<DestinationAlternative, Location>,
            modeChoice: FixedChoicesModel<ModeChoiceAlternative, Mode>,
            attractivenessModel: AttractivenessModel,
            modeAvailability: AvailabilityModelWithSharing,
            modes: ChoiceModelModes
        ): PersonBehavior {
            return PersonBehavior(
                destinationChoice,
                modeChoice,
                impedance,
                attractivenessModel,
                modeAvailability,
                modeAvailability,
                modes,
            )
        }
    }
}
