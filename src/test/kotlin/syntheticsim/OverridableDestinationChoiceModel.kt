package syntheticsim

import domain.data.Person
import domain.location.Location
import modeling.models.ChoiceModel
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.LegacyDestinationChoice
import utils.units.Time

/**
 * Wraps around a LegacyDestinationChoice [original]. The [overrideDestination] parameter will replace the selection
 * of the original model, if not null. This allows controlling the choice function output.
 */
class OverridableDestinationChoiceModel(val original: LegacyDestinationChoice) :
    ChoiceModel<Person, Location> {
    var overrideDestination: Location? = null

    override fun select(agent: Person, choices: Set<Location>, time: Time): Location {
        return overrideDestination ?: original.select(agent, choices, time)
    }

    override val name: String
        get() = original.name

    override fun choices(agent: Person, time: Time): Set<Location> {
        return original.choices(agent, time)
    }

    val modes: ChoiceModelModes
        get() = original.modes
}
