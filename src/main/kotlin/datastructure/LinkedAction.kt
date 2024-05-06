package datastructure

import kotlin.time.Duration

const val LINK_PREFIX = "[Linked]"

fun LinkedAction.startTimeMessage(value: Duration) =
    "Cannot change startTime to $value, not in bounds [lowerBound=${lowerBound()}, endTime=$endTime"

fun LinkedAction.endTimeMessage(value: Duration) =
    "Cannot change endTime to $value, not in bounds [startTime=$startTime, upperBound=${upperBound()}"

interface LinkedAction : Action {
    val original: Action
    val next: (LinkedAction) -> LinkedAction?
    val previous: (LinkedAction) -> LinkedAction?

    override var startLocation: Location
    override var endLocation: Location
    override var startTime: Duration
    override var endTime: Duration

    override var earliestStartTime: Duration?
    override var latestEndTime: Duration?

    fun lowerBound(): Duration {
        return max(
            previous(this)?.endTime ?: -Duration.INFINITE,
            original.earliestStartTime ?: -Duration.INFINITE
        )
    }

    fun shift(duration: Duration)
    fun upperBound(): Duration {
        return min(next(this)?.startTime ?: Duration.INFINITE, original.latestEndTime ?: Duration.INFINITE)
    }
}

class LinkedActivity(
    override val original: Activity,
    override val previous: (LinkedAction) -> LinkedAction?,
    override val next: (LinkedAction) -> LinkedAction?,
) : LinkedAction, Activity {

    override var location: Location
        get() = original.location
        set(value) {
            if (value != location) {
                original.location = value
                next(this)?.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var startLocation: Location
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = value
                next(this)?.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = value
                next(this)?.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {
            if (value in lowerBound()..endTime) {
                original.startTime = value
            }
            error(startTimeMessage(value))
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
    override var earliestStartTime: Duration?
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: Duration?
        get() = original.latestEndTime
        set(value) {
            original.latestEndTime = value
        }
    override var type: ActivityType
        get() = original.type
        set(value) {
            original.type = value
        }
    override fun shift(duration: Duration) {
        original.startTime += duration
        original.endTime += duration
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
    override val previous: (LinkedAction) -> LinkedAction?,
    override val next: (LinkedAction) -> LinkedAction?,
) : LinkedAction, Leg {

    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = value
                next(this)?.startLocation = value
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
    override var earliestStartTime: Duration?
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: Duration?
        get() = original.latestEndTime
        set(value) {
            original.latestEndTime = value
        }

    override fun shift(duration: Duration) {
        original.startTime += duration
        original.endTime += duration
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

fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}
