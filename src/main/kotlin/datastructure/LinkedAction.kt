package datastructure

import kotlin.time.Duration

const val LINK_PREFIX = "[Linked]"

fun LinkedAction.startTimeMessage(value: Duration) =
    "Cannot change startTime to $value, not in bounds [lowerBound=${lowerBound()}, endTime=$endTime"

fun LinkedAction.endTimeMessage(value: Duration) =
    "Cannot change endTime to $value, not in bounds [startTime=$startTime, upperBound=${upperBound()}"

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

    fun lowerBound(): Duration {
        return max(
            previous?.endTime ?: -Duration.INFINITE,
            original.earliestStartTime ?: -Duration.INFINITE
        )
    }

    fun unlink() {
        next?.previous = previous
        previous?.next = next

        previous = null
        next = null
    }

    abstract fun shift(duration: Duration): Duration
    fun upperBound(): Duration {
        return min(next?.startTime ?: Duration.INFINITE, original.latestEndTime ?: Duration.INFINITE)
    }

    abstract fun forceNewEndTime(new: Duration)
}

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
            if (value in lowerBound()..endTime) {
                original.startTime = value
            } else {
                error(startTimeMessage(value))
            }

        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            if (value in startTime..upperBound()) {
                original.endTime = value
            } else {
                error(endTimeMessage(value))
            }
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

    override fun shift(duration: Duration): Duration {
        val neext = next
        original.startTime += duration
        original.endTime += duration

        return max(neext?.startTime?.let { original.endTime - it } ?: Duration.ZERO, Duration.ZERO)
    }

    override fun forceNewEndTime(new: Duration) {
        original.endTime = new
        var offset = next?.startTime?.let { new - it } ?: Duration.ZERO
        var element: LinkedAction? = next
        while (offset > Duration.ZERO && element != null) {
            offset = element.shift(offset)

            element = element.next
        }

    }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }

    override fun toString(): String {
        return LINK_PREFIX + original
    }


}

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
            if (value in lowerBound()..endTime) {
                original.startTime = value
            } else {
                error(startTimeMessage(value))
            }
        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            if (value in startTime..upperBound()) {
                original.endTime = value
            } else {

                error(endTimeMessage(value))
            }
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

    override fun shift(duration: Duration): Duration {
        val neext = next
        original.startTime += duration
        original.endTime += duration

        return max(neext?.startTime?.let { original.endTime - it } ?: Duration.ZERO, Duration.ZERO)
    }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }

    override fun toString(): String {
        return LINK_PREFIX + original
    }

    override fun forceNewEndTime(new: Duration) {
        original.endTime = new
        var offset = next?.startTime?.let { new - it } ?: Duration.ZERO
        var element: LinkedAction? = next
        while (offset > Duration.ZERO && element != null) {
            offset = element.shift(offset)

            element = element.next
        }

    }
}

fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}
