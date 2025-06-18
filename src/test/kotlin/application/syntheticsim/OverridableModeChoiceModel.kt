package application.syntheticsim

import domain.shared.enums.Mode
import domain.simulation.behavior.ModeChoiceAlternative
import modeling.models.ChoiceFilter
import modeling.models.ChoiceModel
import modeling.models.noFilter
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] for [ModeChoiceAlternative]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel(
    val original: ChoiceModel<ModeChoiceAlternative, Mode>,
    override var choiceFilter: ChoiceFilter<ModeChoiceAlternative> = noFilter()
) : ChoiceModel<ModeChoiceAlternative, Mode> {
    var overrideMode: Mode? = null

    override val name: String
        get() = original.name

    override fun select(choices: Set<ModeChoiceAlternative>, random: Random): Mode {
        return overrideMode ?: original.select(choices, random)
    }
}
