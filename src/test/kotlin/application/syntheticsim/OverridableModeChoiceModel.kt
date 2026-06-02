package application.syntheticsim

import domain.shared.enums.Mode
import domain.simulation.behavior.ModeChoiceAlternative
import domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import edu.kit.ifv.mobitopp.discretechoice.models.FilteredChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] for [ModeChoiceAlternative]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel(val original: UtilityBasedChoiceModel<Mode, ModeChoiceCharacteristics>) :
    UtilityBasedChoiceModel<Mode, ModeChoiceCharacteristics> {
    var overrideMode: Mode? = null
    override val name: String = original.name

    context(_: ModeChoiceCharacteristics, _: Random)
    override fun select(choices: Set<Mode>): Mode = overrideMode ?: original.select(choices)

    context(_: ModeChoiceCharacteristics)
    override fun utility(alternative: Mode): Double = throw UnsupportedOperationException("Not yet implemented")

    override fun probabilities(utilities: Map<Mode, Double>): Map<Mode, Double> = throw UnsupportedOperationException(
        "Not yet implemented",
    )

    override fun addFilter(
        filter: ChoiceFilter<Mode, ModeChoiceCharacteristics>,
    ): FilteredChoiceModel<Mode, ModeChoiceCharacteristics> = original.addFilter(
        filter,
    )

    context(_: ModeChoiceCharacteristics, random: Random)
    override fun selectInjected(choices: Set<Mode>, injections: Map<Mode, (Double) -> Double>): Mode =
        throw UnsupportedOperationException("Not yet implemented")
}
