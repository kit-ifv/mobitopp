package datastructure

import utils.units.max
import kotlin.time.Duration

/**
 * Represents an action that is inserted into an action plan with a known position. Modifying attributes of a linked
 * action can propagate changes to neighboring actions. Note that altering the order within the plan should be handled
 * at a higher level of abstraction.
 */
abstract class LinkedAction : Action {
    abstract val original: Action
    internal abstract var previous: LinkedAction?
    internal abstract var next: LinkedAction?

    abstract override var startLocation: Location
    abstract override var endLocation: Location
    abstract override var startTime: Duration
    abstract override var endTime: Duration

    abstract override var earliestStartTime: Duration
    abstract override var latestEndTime: Duration

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

    fun shiftStartTo(timePoint: Duration) {
        // Keeping the original duration since changing the start or end would influence the value calculation
        val duration = duration
        startTime = timePoint
        endTime = startTime + duration
    }

    fun requiresPushback(target: Duration) = endTime > target

    fun requiresPullForward(target: Duration) = startTime < target

    override fun toString(): String {
        return "[Linked] $original"
    }
}

/**
 * Represents a linked activity, which is a linked action associated with an activity.
 * @param original The original activity.
 * @param previous The previous linked action in the sequence.
 * @param next The next linked action in the sequence.
 */
class LinkedActivity(
    override val original: Activity,
    override var previous: LinkedAction? = null,
    override var next: LinkedAction? = null
) : LinkedAction(), Activity by original {
    override var location: Location
        get() = original.location
        set(value) {
            if (value != location) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var startLocation: Location
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {
            previous?.let {
                if (it.requiresPushback(value)) {
                    val difference = it.endTime - value
                    it.startTime -= difference
                    it.endTime -= difference
                }
            }
            original.startTime = value
        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            next?.let {
                if (it.requiresPullForward(value)) {
                    val difference = value - it.startTime
                    it.startTime += difference
                    it.endTime += difference
                }
            }
            original.endTime = value
        }
    override var earliestStartTime: Duration
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: Duration
        get() = original.latestEndTime
        set(value) {
            original.latestEndTime = value
        }
    override var type: ActivityType
        get() = original.type
        set(value) {
            original.type = value
        }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }
}

/**
 * Represents a linked leg, which is a linked action associated with a leg.
 * @param original The original leg.
 * @param previous The previous linked action in the sequence.
 * @param next The next linked action in the sequence.
 */
class LinkedLeg(
    override val original: Leg,
    override var previous: LinkedAction? = null,

    override var next: LinkedAction? = null
) : LinkedAction(), Leg by original {
    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = value
                next?.startLocation = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {
            previous?.let {
                if (it.requiresPushback(value)) {
                    val difference = it.endTime - value
                    it.startTime -= difference
                    it.endTime -= difference
                }
            }
            original.startTime = value
        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            next?.let {
                if (it.requiresPullForward(value)) {
                    val difference = value - it.startTime
                    it.startTime += difference
                    it.endTime += difference
                }
            }
            original.endTime = value
        }
    override var earliestStartTime: Duration
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: Duration
        get() = original.latestEndTime
        set(value) {
            original.latestEndTime = value
        }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }
}
