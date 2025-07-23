package application.syntheticsim

import domain.shared.location.Location
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
    val original: UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>,
) : UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>{
    var overrideDestination: Location? = null
    override val name: String = original.name


    context(characteristics: DestinationChoiceCharacteristics, random: Random)
    override fun select(choices: Set<Location>): Location {
        return overrideDestination ?: original.select(choices)
    }

    context(_: DestinationChoiceCharacteristics)
    override fun utility(alternative: Location): Double {
        TODO("Not yet implemented")
    }

    override fun probabilities(utilities: Map<Location, Double>): Map<Location, Double> {
        TODO("Not yet implemented")
    }

    override fun addFilter(filter: ChoiceFilter<Location, DestinationChoiceCharacteristics>): FilteredChoiceModel<Location, DestinationChoiceCharacteristics> {
        return original.addFilter(filter)
    }

    context(_: DestinationChoiceCharacteristics, random: Random)
    override fun selectInjected(
        choices: Set<Location>,
        injections: Map<Location, (Double) -> Double>,
    ): Location {
        TODO("Not yet implemented")
    }
}
