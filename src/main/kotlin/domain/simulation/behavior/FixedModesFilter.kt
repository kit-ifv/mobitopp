package domain.simulation.behavior

import domain.shared.datastructure.schedule.StationaryAction
import domain.simulation.agent.PersonAgent
import discreteChoice.models.ChoiceFilter

object FixedModesFilter : ChoiceFilter<ModeChoiceAlternative> { // TODO should filters have names for debugging?

    override fun filter(choices: Set<ModeChoiceAlternative>): Set<ModeChoiceAlternative> {
        val person = choices.first().person
        // TODO accessing first to get parameters is not elegant,
        // and assumes that alternatives only differ in Mode/choice
        val lastActivity = person.schedule.pastActivities().lastOrNull()

        return lastActivity?.takeIf { isNotAtHome(person, it) }?.let { _ ->
            val lastMode = person.schedule.pastLegs().lastOrNull()?.transportType

            lastMode?.let {
                if (it.requiresVehicleTakeAlong) {
                    choices.filter { it.choice == lastMode }.toSet()
                } else {
                    choices.filter { !it.choice.requiresVehicleTakeAlong }.toSet()
                }
            }
        } ?: choices
    }

    private fun isNotAtHome(person: PersonAgent, activity: StationaryAction): Boolean {
        return person.household.location != activity.location
    }
}
