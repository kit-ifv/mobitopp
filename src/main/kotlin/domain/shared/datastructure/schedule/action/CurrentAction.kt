package domain.shared.datastructure.schedule.action

import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime

/**
 * Current Action is a wrapper class that only allows modification of [LinkedAction] attributes which are in the future:
 * The [endLocation], [endTime] and [latestEndTime], while protecting alteration for all other attributes
 */
class CurrentAction(private val linkedAction: LinkedAction) : Action by linkedAction {

    val original: Action = linkedAction.original

    val type: ActionType = linkedAction.actionType
    override var endTime: AbsoluteTime
        get() = linkedAction.endTime
        set(value) {
            linkedAction.endTime = value
        }
    override var endLocation: StandardLocation
        get() = linkedAction.endLocation
        set(value) {
            linkedAction.endLocation = value
        }
    override var latestEndTime: AbsoluteTime
        get() = linkedAction.latestEndTime
        set(value) {
            linkedAction.latestEndTime = value
        }
}
