package datastructure

import kotlin.time.Duration

sealed interface Action : Comparable<Action> {
    val startTime: Duration
    val duration: Duration get() = endTime - startTime
    val endTime: Duration

    val startLocation: Location
    val endLocation: Location

    /*TODO Intervals do not form a well defined order, we require a more idiomatic way of representing this fact
    maybe use a separate comparator?
    */
    // As in x <= y && x >= y => x == y is violated by intervals. We only have a partial order but Comparable induces
    // total ordering
    override fun compareTo(other: Action): Int {
        if (endTime <= other.startTime) return -1
        if (other.endTime <= startTime) return 1
        return 0
    }

}

fun Iterable<Action>.isConsistent(): Boolean {
    val t =
        zipWithNext { first, second -> first.endLocation == second.startLocation && first.endTime <= second.startTime }
    return t.all { it }
}

sealed interface StationaryAction : Action {
    val location: Location
    override val startLocation: Location
        get() = location
    override val endLocation: Location
        get() = location
}

sealed interface MovingAction : Action {
    override val startTime: Duration
    override val endTime: Duration
    override val startLocation: Location
    override val endLocation: Location
}

interface Activity : StationaryAction {
    override var location: Location
    override var startTime: Duration
    override var endTime: Duration


    /**
     * A default implementation to spawn a leg spanning from one activity to another.
     */
    fun createLegTo(other: Activity): Leg {
        return Leg.fromDuration(endTime, other.startTime - endTime, endLocation, other.startLocation)
    }

    override fun equals(other: Any?): Boolean

    companion object {
        fun fromDuration(location: Location, startTime: Duration, duration: Duration): Activity {
            return RawActivity(location = location, startTime = startTime, endTime = startTime + duration)
        }
    }
}

data class RawActivity(
    override var location: Location,
    override var startTime: Duration,
    override var endTime: Duration
) : Activity {
    override val duration get() = endTime - startTime
}

interface Leg : MovingAction {
    override var startTime: Duration
    override var endTime: Duration
    override var startLocation: Location
    override var endLocation: Location
    override fun equals(other: Any?): Boolean

    companion object {
        fun fromDuration(startTime: Duration, duration: Duration, startLocation: Location, endLocation: Location): Leg {
            return RawLeg(
                startTime = startTime,
                endTime = startTime + duration,
                startLocation = startLocation,
                endLocation = endLocation
            )
        }
    }
}

data class RawLeg(
    override var startTime: Duration,
    override var startLocation: Location,
    override var endLocation: Location,
    override var endTime: Duration

) : Leg {
    override val duration: Duration get() = endTime - startTime
}