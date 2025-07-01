package application.syntheticsim

import discreteChoice.models.ChoiceFilter
import discreteChoice.models.ChoiceModel
import discreteChoice.models.noFilter
import domain.shared.location.Location
import domain.simulation.behavior.DestinationAlternative
import kotlin.random.Random

/**
 * Wraps around a [ChoiceModel] [original] for [DestinationAlternative]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(
    val original: ChoiceModel<DestinationAlternative, Location>,
    override var choiceFilter: ChoiceFilter<DestinationAlternative> = noFilter()
) : ChoiceModel<DestinationAlternative, Location> {
    var overrideDestination: Location? = null

    override fun select(choices: Set<DestinationAlternative>, random: Random): Location {
        return overrideDestination ?: original.select(choices, random)
    }

    override val name: String
        get() = original.name
}
