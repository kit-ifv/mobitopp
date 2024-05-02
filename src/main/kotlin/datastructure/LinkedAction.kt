package datastructure

import kotlin.time.Duration

interface LinkedAction : Action {
    val original: Action
    val next: () -> LinkedAction?
    val previous: () -> LinkedAction?

    override var startLocation: Location
    override var endLocation: Location
    override var startTime: Duration
    override var endTime: Duration
    override var earliestStartTime: Duration?
    override var latestEndTime: Duration?

    fun lowerBound(): Duration {
        return max(
            previous()?.endTime ?: -Duration.INFINITE,
            original.earliestStartTime ?: -Duration.INFINITE
        )
    }

    fun upperBound(): Duration {
        return min(next()?.startTime ?: Duration.INFINITE, original.latestEndTime ?: Duration.INFINITE)
    }

}


class LinkedActivity(override val original: Activity,
                     override val next: () -> LinkedAction?,
                     override val previous: () -> LinkedAction?
) : LinkedAction, StationaryAction {

    override val location: Location
        get() = original.location
    override var startLocation: Location
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = startLocation
                next()?.startLocation = value
                previous()?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = startLocation
                next()?.startLocation = value
                previous()?.endLocation = value
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
}

class LinkedLeg(override val original: Leg,
                override val next: () -> LinkedAction?,
                override val previous: () -> LinkedAction?
): LinkedAction, MovingAction {

    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = startLocation
                previous()?.endLocation = value
            }
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = startLocation
                next()?.startLocation = value
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
}


fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}