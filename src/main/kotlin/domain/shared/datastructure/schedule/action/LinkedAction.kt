package domain.shared.datastructure.schedule.action

import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime
import utils.units.max
import kotlin.time.Duration

/**
 * Represents an action that is inserted into an action plan with a known position. Modifying attributes of a linked
 * action can propagate changes to neighboring actions. Note that altering the order within the plan should be handled
 * at a higher level of abstraction.
 */
abstract class LinkedAction : Action {
    abstract val original: Action
    abstract var previous: LinkedAction?
        internal set
    abstract var next: LinkedAction?
        internal set

    abstract override var startLocation: StandardLocation
    abstract override var endLocation: StandardLocation
    abstract override var startTime: AbsoluteTime
    abstract override var endTime: AbsoluteTime

    abstract override var earliestStartTime: AbsoluteTime
    abstract override var latestEndTime: AbsoluteTime

    fun unlink(): Action {
        next?.previous = previous
        previous?.next = next

        previous = null
        next = null
        return original
    }

    fun shiftByDelta(duration: Duration): Duration {
        startTime += duration
        endTime += duration
        return max(next?.startTime?.let { original.endTime - it } ?: Duration.ZERO, Duration.ZERO)
    }

    fun shiftStartTo(timePoint: AbsoluteTime) {
        // Keeping the original duration since changing the start or end would influence the value calculation
        val duration = duration
        startTime = timePoint
        endTime = startTime + duration
    }

    fun requiresPushback(target: AbsoluteTime) = endTime > target

    fun requiresPullForward(target: AbsoluteTime) = startTime < target

    override fun toString(): String = "[Linked] $original"
}
