package usecases.models

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import modeling.models.ChoiceModel
import usecases.choicemodels.TripChoiceSituation
import utils.units.Time

class VehicleTakeAlongModeChoice(
    val modeChoice: ChoiceModel<TripChoiceSituation, Mode>,
) : ChoiceModel<TripChoiceSituation, Mode> by modeChoice {

    override val name: String
        get() = "${modeChoice.name} considering take along vehicles"

    override fun select(agent: TripChoiceSituation, choices: Set<Mode>, time: Time): Mode {
        val lastActivity = agent.person.schedule.pastActivities().lastOrNull()

        return lastActivity?.takeIf { isNotAtHome(agent.person, it) }?.let { _ ->
            val lastMode = agent.person.schedule.pastLegs().lastOrNull()?.transportType

            lastMode?.let {
                if (it.requiresVehicleTakeAlong) {
                    it
                } else {
                    modeChoice.select(
                        agent,
                        choices.filter { m -> !m.requiresVehicleTakeAlong }.toSet(),
                        time
                    )
                }
            }
        } ?: modeChoice.select(agent, choices, time)
    }

    private fun isNotAtHome(person: Person, activity: StationaryAction): Boolean {
        return person.household.location != activity.location
    }
}
