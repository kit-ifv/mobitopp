package domain.shared.datastructure.schedule

import domain.shared.enums.ActivityType
import domain.shared.location.LocationOld
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

    abstract override var startLocation: LocationOld
    abstract override var endLocation: LocationOld
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

    init {
        require(original !is LinkedActivity)
    }

    override var location: LocationOld
        get() = original.location
        set(value) {
            if (value != location) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var startLocation: LocationOld
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: LocationOld
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var startTime: AbsoluteTime
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
    override var endTime: AbsoluteTime
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

    override var duration: Duration
        get() = original.duration
        set(value) {
            // delegate to change of endTime, TODO validate in bounds, make sure endtime never < start time
            this.endTime = this.startTime + value
        }

    override var earliestStartTime: AbsoluteTime
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: AbsoluteTime
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

    override fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedActivity {
        return this
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
    override var startLocation: LocationOld
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: LocationOld
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = value
                next?.startLocation = value
            }
        }
    override var startTime: AbsoluteTime
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
    override var endTime: AbsoluteTime
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
    override var earliestStartTime: AbsoluteTime
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: AbsoluteTime
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

    override fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedLeg {
        return this
    }
}
