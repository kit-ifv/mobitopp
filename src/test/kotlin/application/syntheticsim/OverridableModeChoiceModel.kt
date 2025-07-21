package application.syntheticsim

import domain.shared.enums.Mode
import domain.simulation.behavior.ModeChoiceAlternative
import domain.simulation.behavior.ModeChoiceSituation
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import edu.kit.ifv.mobitopp.discretechoice.models.FilteredChoiceModel


import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] for [ModeChoiceAlternative]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel(
    val original: UtilityBasedChoiceModel< Mode, ModeChoiceSituation>,
) : UtilityBasedChoiceModel<Mode, ModeChoiceSituation, >  {
    var overrideMode: Mode? = null
    override val name: String = original.name


    context(_: ModeChoiceSituation, _: Random)
    override fun select(choices: Set<Mode>): Mode {
        return overrideMode ?: original.select(choices)
    }

    context(_: ModeChoiceSituation)
    override fun utility(alternative: Mode): Double {
        TODO("Not yet implemented")
    }

    override fun probabilities(utilities: Map<Mode, Double>): Map<Mode, Double> {
        TODO("Not yet implemented")
    }

    override fun addFilter(filter: ChoiceFilter<Mode, ModeChoiceSituation>): FilteredChoiceModel<Mode, ModeChoiceSituation> {
        return original.addFilter(filter)
    }

    context(_: ModeChoiceSituation, random: Random)
    override fun selectInjected(
        choices: Set<Mode>,
        injections: Map<Mode, (Double) -> Double>,
    ): Mode {
        TODO("Not yet implemented")
    }

}
