package domain.shared.datastructure.schedule.action

import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime

/**
 * A [MovingAction] is an [Action] that starts at the [startLocation] and ends at the [endLocation]. Start and end may
 * be the same (For example a leisure circular walk may be such an action) This is a read-only view and does not allow
 * modification of the properties.
 */

sealed interface MovingAction : Action {
    override val startTime: AbsoluteTime
    override val endTime: AbsoluteTime
    override val startLocation: StandardLocation
    override val endLocation: StandardLocation
    override val actionType: ActionType
        get() = ActionType.LEG

    val transportType: Mode // TODO can we rename this property to mode?
}
