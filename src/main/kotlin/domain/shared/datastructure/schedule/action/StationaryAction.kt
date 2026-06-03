package domain.shared.datastructure.schedule.action

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation

/**
 * A [StationaryAction] is an [Action] that takes place at one and only one Location. The [startLocation] and [endLocation]
 * can therefore be delegated to the central [location] property. This is a read-only view and does not allow alteration
 * of the properties. You should use this interface when you want to disallow modifications.
 */
sealed interface StationaryAction : Action {
    val location: StandardLocation
    val type: ActivityType
    override val actionType: ActionType
        get() = ActionType.ACTIVITY
    override val startLocation: StandardLocation
        get() = location
    override val endLocation: StandardLocation
        get() = location
}