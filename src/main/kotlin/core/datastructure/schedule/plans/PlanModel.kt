package core.datastructure.schedule.plans

import core.datastructure.schedule.Action
import core.datastructure.schedule.ActionBlock
import core.datastructure.schedule.Activity
import core.datastructure.schedule.Leg
import core.datastructure.schedule.LinkedAction
import core.datastructure.schedule.LinkedActivity
import core.datastructure.schedule.LinkedLeg
import utils.units.AbsoluteTime
import kotlin.time.Duration

interface LegTracker {
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun replaceLegs(target: Set<Leg>, to: Set<Leg>)
}

interface ActivityTracker {
    fun add(activity: Activity): LinkedActivity?
    fun remove(activity: Activity)
    fun replaceActivities(target: Set<Activity>, to: Set<Activity>)
}

/**
 * A plan model should maintain the state of the Action plan by allowing legs and activities to
 */
interface PlanModel : LegTracker, ActivityTracker {

    val dispatcher: IDispatcher

    /**
     * Removes the first Action from the plan. Note that this element remains a [LinkedAction] and thus has access to
     * the [LinkedAction.previous] and [LinkedAction.next] fields. The source that called this function should also
     * perform the unlinking process if the element is not immediately discarded.
     */
    fun removeFirst(): LinkedAction?

    fun actions(): Collection<LinkedAction>

    /**
     * Return the first [Action] in the model. Or null if none is present
     */
    fun first(): LinkedAction?

    /**
     * Delete all actions from the model.
     */
    fun clear()

    fun dropUntil(activity: Activity)
}

interface SeparablePlanModel : PlanModel {

    fun activities(): Collection<LinkedActivity>

    fun lastActivity(): LinkedActivity
    fun legs(): Collection<LinkedLeg>

    fun view(): BlockModel.TripView

    fun nextBlock(): ActionBlock<*>?
}

fun PlanModel.squeeze(from: AbsoluteTime, to: AbsoluteTime, force: Boolean = false) {
    val afterAction = actions().dropWhile { it.endTime <= from }
    var counter = to
    val requiredShift = afterAction.map {
        val offset = counter - it.startTime
        counter += it.duration
        offset
    }
    val targets = afterAction.zip(requiredShift).filter { it.second > Duration.ZERO }
    val valid = targets.all { (action, shift) ->
        (action.latestEndTime) >= action.endTime + shift &&
            (action.earliestStartTime) <= action.startTime + shift
    }
    if (valid || force) {
        targets.reversed().forEach { (action, shift) ->
            action.shiftByDelta(shift)
        }
    } else {
        error(
            "Some actions in the plan cannot support the requested squeeze ${
                targets.filter { (action, shift) ->
                    (action.latestEndTime) < action.endTime + shift ||
                        (action.earliestStartTime) > action.startTime + shift
                }.map { (action, dur) ->
                    "${action.original} necessaryShift=$dur"
                }
            } \nrun with force=true IF and only IF you know what you are doing."
        )
    }
}

fun PlanModel.squeeze(action: Action, force: Boolean = false) {
    return this.squeeze(action.startTime, action.endTime, force)
}

/**
 * This function is designed to shift all activities so that the Time Interval [from, block] is freed. All Actions
 * should be shifted the same amount, so breaks in the activity plan should remain the same breaks. No squeezing.
 *
 * @param from the start of the time Interval
 * @param to the end of the time interval to be freed
 *
 * @param force Overwrites the actions regardless of their earliest start or latest end time
 */
fun PlanModel.shift(from: AbsoluteTime, block: Duration, force: Boolean = false) {
    val targets = actions().dropWhile { it.endTime <= from }
    if (targets.all {
            it.startTime + block >= (it.earliestStartTime) &&
                it.endTime + block <= (it.latestEndTime)
        } || force
    ) {
        targets.reversed().forEach {
            it.shiftByDelta(block)
        }
    } else {
        error("The schedule does not support the shift requested. ${this.actions()}")
    }
}
