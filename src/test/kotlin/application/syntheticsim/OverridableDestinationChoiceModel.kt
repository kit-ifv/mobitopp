package application.syntheticsim

import domain.shared.location.Location
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.TripChoiceSituation
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter

import edu.kit.ifv.mobitopp.discretechoice.models.FilteredChoiceModel


import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import java.awt.Choice
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] [original] for [DestinationAlternative]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(
    val original: UtilityBasedChoiceModel<Location, TripChoiceSituation>,
) : UtilityBasedChoiceModel<Location, TripChoiceSituation>{
    var overrideDestination: Location? = null
    override val name: String = original.name


    context(characteristics: TripChoiceSituation, random: Random)
    override fun select(choices: Set<Location>): Location {
        return overrideDestination ?: original.select(choices)
    }

    context(_: TripChoiceSituation)
    override fun utility(alternative: Location): Double {
        TODO("Not yet implemented")
    }

    override fun probabilities(utilities: Map<Location, Double>): Map<Location, Double> {
        TODO("Not yet implemented")
    }

    override fun addFilter(filter: ChoiceFilter<Location, TripChoiceSituation>): FilteredChoiceModel<Location, TripChoiceSituation> {
        return original.addFilter(filter)
    }

    context(_: TripChoiceSituation, random: Random)
    override fun selectInjected(
        choices: Set<Location>,
        injections: Map<Location, (Double) -> Double>,
    ): Location {
        TODO("Not yet implemented")
    }
}
