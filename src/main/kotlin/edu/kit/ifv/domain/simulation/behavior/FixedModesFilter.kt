package edu.kit.ifv.domain.simulation.behavior
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.mobitopp.discretechoice.models.ChoiceFilter

// TODO should filters have names for debugging?
object FixedModesFilter : ChoiceFilter<Mode, ModeChoiceCharacteristics> {
    context(characteristics: ModeChoiceCharacteristics)
    override fun filter(alternative: Mode): Boolean {
        val person = characteristics.person
        // TODO accessing first to get parameters is not elegant,
        // and assumes that alternatives only differ in Mode/choice
        val lastActivity = person.schedule.pastActivities().lastOrNull()

        return lastActivity?.takeIf { isNotAtHome(person, it) }?.let { _ ->
            val lastMode = person.schedule.pastLegs().lastOrNull()?.transportType

            lastMode?.let {
                if (it.requiresVehicleTakeAlong) {
                    alternative == lastMode
                } else {
                    !alternative.requiresVehicleTakeAlong
                }
            }
        } ?: true
    }

    private fun isNotAtHome(person: PersonAgent, activity: StationaryAction): Boolean =
        person.household.location != activity.location
}
