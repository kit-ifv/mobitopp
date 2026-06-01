package application.syntheticsim

import domain.shared.location.StandardLocation
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter
import edu.kit.ifv.mobitopp.discretechoice.models.FilteredChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] [original] for [DestinationAlternative]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(
    val original: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
) : UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics> {
    var overrideDestination: StandardLocation? = null
    override val name: String = original.name

    context(characteristics: DestinationChoiceCharacteristics, random: Random)
    override fun select(choices: Set<StandardLocation>): StandardLocation =
        overrideDestination ?: original.select(choices)

    context(_: DestinationChoiceCharacteristics)
    override fun utility(alternative: StandardLocation): Double =
        throw UnsupportedOperationException("Not yet implemented")

    override fun probabilities(utilities: Map<StandardLocation, Double>): Map<StandardLocation, Double> =
        throw UnsupportedOperationException("Not yet implemented")

    override fun addFilter(
        filter: ChoiceFilter<StandardLocation, DestinationChoiceCharacteristics>,
    ): FilteredChoiceModel<StandardLocation, DestinationChoiceCharacteristics> = original.addFilter(filter)

    context(_: DestinationChoiceCharacteristics, random: Random)
    override fun selectInjected(
        choices: Set<StandardLocation>,
        injections: Map<StandardLocation, (Double) -> Double>,
    ): StandardLocation = throw UnsupportedOperationException("Not yet implemented")
}
