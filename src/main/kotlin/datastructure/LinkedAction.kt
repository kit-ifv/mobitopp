package datastructure

import kotlin.time.Duration

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

    fun upperBound(): Duration {
        return min(next(this)?.startTime ?: Duration.INFINITE, original.latestEndTime ?: Duration.INFINITE)
    }

}


class LinkedActivity(
    override val original: Activity,
    override val next: (LinkedAction) -> LinkedAction?,
    override val previous: (LinkedAction) -> LinkedAction?
) : LinkedAction, StationaryAction {

    override val location: Location
        get() = original.location
    override var startLocation: Location
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = startLocation
                next(this)?.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = startLocation
                next(this)?.startLocation = value
                previous(this)?.endLocation = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {
            if (lowerBound() <= value) {
                original.startTime = value
            }
        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            if (upperBound() >= value) {
                original.endTime = value
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

    override fun equals(other: Any?): Boolean {
        return original == other
    }
}

class LinkedLeg(
    override val original: Leg,
    override val next: (LinkedAction) -> LinkedAction?,
    override val previous: (LinkedAction) -> LinkedAction?
) : LinkedAction, MovingAction {

    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = startLocation
                previous(this)?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = startLocation
                next(this)?.startLocation = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {
            if (lowerBound() <= value) {
                original.startTime = value
            }
        }
    override var endTime: Duration
        get() = original.endTime
        set(value) {
            if (upperBound() >= value) {
                original.endTime = value
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

    override fun equals(other: Any?): Boolean {
        return original == other
    }
}


fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}