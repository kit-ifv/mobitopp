package edu.kit.ifv.application.syntheticsim
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceAlternative
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] for [ModeChoiceAlternative]. the [overrideMode] parameter will overwrite the selection of the original
 * model if and only if it is not null.
 */
class OverridableModeChoiceModel constructor(val original: UtilityBasedChoiceModel<Mode, ModeChoiceCharacteristics>) :
    UtilityBasedChoiceModel<Mode, ModeChoiceCharacteristics> {
    private val id = counter++

    var overrideMode: Mode? = null
    override val name: String = original.name

    context(_: ModeChoiceCharacteristics, _: Random)
    override fun select(choices: Set<Mode>): Mode = overrideMode ?: original.select(choices)

    context(_: ModeChoiceCharacteristics)
    override fun utility(alternative: Mode): Double = throw UnsupportedOperationException("Not yet implemented")

    override fun probabilities(utilities: Map<Mode, Double>): Map<Mode, Double> = throw UnsupportedOperationException(
        "Not yet implemented",
    )

    context(_: ModeChoiceCharacteristics, random: Random)
    override fun selectInjected(choices: Set<Mode>, injections: Map<Mode, (Double) -> Double>): Mode =
        throw UnsupportedOperationException("Not yet implemented")

    override fun toString(): String = "Overridable ID: $id override = $overrideMode"

    companion object {
        private var counter = 0
    }
}
