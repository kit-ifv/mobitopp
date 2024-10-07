package syntheticsim

import domain.data.Person
import domain.enums.Mode
import modeling.models.ChoiceModel
import usecases.choicemodels.BasicModesModel
import usecases.choicemodels.LegacyModeChoiceModel
import utils.CodePlan
import utils.units.Time

/**
 * Wraps around a [LegacyModeChoiceModel]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel(val original: LegacyModeChoiceModel) :
    ChoiceModel<Person, Mode>,
    BasicModesModel {
    var overrideMode: Mode? = null

    override fun select(agent: Person, choices: Set<Mode>, time: Time): Mode {
        return overrideMode ?: original.select(agent, choices, time)
    }

    override val name: String
        get() = original.name

    override fun choices(agent: Person, time: Time): Set<Mode> {
        return original.choices(agent, time)
    }

    override fun filter(agent: Person, time: Time): Collection<Mode> {
        return original.filter(agent, time)
    }

    override val modes: CodePlan<Mode>
        get() = original.modes
}
