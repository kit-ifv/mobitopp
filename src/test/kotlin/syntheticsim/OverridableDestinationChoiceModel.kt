package syntheticsim

import domain.data.Person
import domain.enums.Mode
import domain.location.ZoneLocation
import modeling.models.ChoiceModel
import usecases.choicemodels.BasicModesModel
import usecases.choicemodels.LegacyDestinationChoice
import utils.CodePlan
import utils.units.Time

/**
 * Wraps around a LegacyDestinationChoice [original]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(val original: LegacyDestinationChoice) :
    ChoiceModel<Person, ZoneLocation>, BasicModesModel {
    var overrideDestination: ZoneLocation? = null

    override fun select(agent: Person, choices: Set<ZoneLocation>, time: Time): ZoneLocation {
        return overrideDestination ?: original.select(agent, choices, time)
    }

    override val name: String
        get() = original.name

    override fun choices(agent: Person, time: Time): Set<ZoneLocation> {
        return original.choices(agent, time)
    }

    override val modes: CodePlan<Mode>
        get() = original.modes
}
