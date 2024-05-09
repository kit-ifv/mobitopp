package datastructure

import kotlin.time.Duration

const val LINK_PREFIX = "[Linked]"

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

    fun unlink() {
        next?.previous = previous
        previous?.next = next

        previous = null
        next = null
    }

    abstract fun shift(duration: Duration): Duration

    fun requiresPushback(target: Duration) = endTime > target

    fun requiresPullForward(target: Duration) = startTime < target

    override fun toString(): String {
        return LINK_PREFIX + original.toString()
    }
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

    override fun shift(duration: Duration): Duration {
        val nectAction = next
        original.startTime += duration
        original.endTime += duration

        return max(nectAction?.startTime?.let { original.endTime - it } ?: Duration.ZERO, Duration.ZERO)
    }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
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
}

fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}
