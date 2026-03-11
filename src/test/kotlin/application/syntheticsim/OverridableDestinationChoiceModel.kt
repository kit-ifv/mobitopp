package application.syntheticsim

import domain.shared.location.LocationOld
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter

import edu.kit.ifv.mobitopp.discretechoice.models.FilteredChoiceModel


import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel

/**
 * Wraps around a [ChoiceModel] [original] for [DestinationAlternative]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(
    val original: UtilityBasedChoiceModel<LocationOld, DestinationChoiceCharacteristics>,
) : UtilityBasedChoiceModel<LocationOld, DestinationChoiceCharacteristics>{
    var overrideDestination: LocationOld? = null
    override val name: String = original.name


    context(characteristics: DestinationChoiceCharacteristics, random: Random)
    override fun select(choices: Set<LocationOld>): LocationOld {
        return overrideDestination ?: original.select(choices)
    }

    context(_: DestinationChoiceCharacteristics)
    override fun utility(alternative: LocationOld): Double {
        TODO("Not yet implemented")
    }

    override fun probabilities(utilities: Map<LocationOld, Double>): Map<LocationOld, Double> {
        TODO("Not yet implemented")
    }

    override fun addFilter(filter: ChoiceFilter<LocationOld, DestinationChoiceCharacteristics>): FilteredChoiceModel<LocationOld, DestinationChoiceCharacteristics> {
        return original.addFilter(filter)
    }

    context(_: DestinationChoiceCharacteristics, random: Random)
    override fun selectInjected(
        choices: Set<LocationOld>,
        injections: Map<LocationOld, (Double) -> Double>,
    ): LocationOld {
        TODO("Not yet implemented")
    }
}
