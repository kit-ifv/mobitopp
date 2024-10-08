package usecases.models

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import domain.location.ZoneLocation
import modeling.models.ChoiceModel
import utils.units.Time

class VehicleTakeAlongModeChoice(
    val modeChoice: ChoiceModel<Person, Mode>,
) : ChoiceModel<Person, Mode> by modeChoice {

    override val name: String
        get() = "${modeChoice.name} considering take along vehicles"

    override fun select(agent: Person, choices: Set<Mode>, time: Time): Mode {
        val lastActivity = agent.schedule.pastActivities().lastOrNull()

        return lastActivity?.takeIf { isNotAtHome(agent, it) }?.let { _ ->
            val lastMode = agent.schedule.pastLegs().lastOrNull()?.transportType

            lastMode?.let {
                if (it.requiresVehicleTakeAlong) { it } else {
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
        return person.household.location.zone.id != (activity.location as ZoneLocation).zone.id
    }
}
