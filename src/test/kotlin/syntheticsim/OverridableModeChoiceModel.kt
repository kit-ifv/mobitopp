package syntheticsim

import domain.enums.Mode
import modeling.models.ChoiceModel
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.TripChoiceSituation
import utils.units.Time

/**
 * Wraps around a [LegacyModeChoiceModel]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel(val original: LegacyModeChoiceModel) :
    ChoiceModel<TripChoiceSituation, Mode> {
    var overrideMode: Mode? = null

    override fun select(agent: TripChoiceSituation, choices: Set<Mode>, time: Time): Mode {
        return overrideMode ?: original.select(
            agent,
            choices,
            time
        )
    }

    override val name: String
        get() = original.name

    override fun choices(agent: TripChoiceSituation, time: Time): Set<Mode> {
        return original.choices(agent, time)
    }

    override fun filter(agent: TripChoiceSituation, time: Time): Collection<Mode> {
        return original.filter(agent, time)
    }
}
