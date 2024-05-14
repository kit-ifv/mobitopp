package datastructure

import kotlin.time.Duration

/**
 * An Action is the central aspect of mobility behaviour in this simulation context. In its simplest form an action
 * consists of a [startLocation] and a [startTime], as well as an [endLocation] and an [endTime]. In this modelling
 * approach an action is an atomic operation - There are no smaller representations of sub-actions. It is your duty
 * when modelling future problems to break your domain down to this level.
 *
 * Note that an action only holds location and time information, your implementation must provide additional details
 * should you require them. The [onBegin] and [onEnd] methods are designed to integrate operational logic in the
 * event system.
 */
sealed interface Action : Comparable<Action> {
    val startTime: Duration
    val actionType: ActionType

    /**
     * [duration] is a derived property of an action by the difference of [endTime] and [startTime]. If you see
     * that your code is relying heavily on this property it might be prudent to implement your own Action with
     * a backing field rather than a derived property
     */
    val duration: Duration get() = endTime - startTime
    val endTime: Duration

    val startLocation: Location
    val endLocation: Location

    val earliestStartTime: Duration
    val latestEndTime: Duration

    /*Intervals do not form a well-defined order, we require a more idiomatic way of representing this fact

    maybe use a separate comparator?

     As in x <= y && x >= y => x == y is violated by intervals. We only have a partial order but Comparable induces
     total ordering */
    @Suppress("ReturnCount")
    /* The speed benefit of early returns clearly outweighs the fact that there are
     3 (the horror) return statements rather than 2. I think that this method may be simple enough that a foreign
     reader will understand what exactly is happening
     */
    override fun compareTo(other: Action): Int {
        if (endTime <= other.startTime) return -1
        if (other.endTime <= startTime) return 1
        return 0
    }

    /**
     * The [onBegin] function provides implementing classes with the ability to define their individual behaviour
     * for the event system. This method will trigger when an Action is supposed to start.
     */
    fun onBegin() {
    }

    /**
     * The [onEnd] function provides implementing classes with the ability to define their individual behaviour
     * for the event system. This method will trigger when an Action is supposed to end.
     */
    fun onEnd() {
    }
}

/**
 * This method verifies the consistency of any iterable of actions by having continuous locations and no time interval
 * overlaps
 *
 */
fun Iterable<Action>.isConsistent(): Boolean {
    val t =
        zipWithNext { first, second -> first.endLocation == second.startLocation && first.endTime <= second.startTime }
    return t.all { it }
}

fun Iterable<Action>.hasTimeBoundViolations(): Boolean {
    return any { it.startTime < it.earliestStartTime || it.endTime > it.latestEndTime }
}

enum class ActivityType {
    HOME,
    UNKNOWN
}

/**
 * A [StationaryAction] is an [Action] that takes place at one and only one [Location]. The [startLocation] and [endLocation]
 * can therefore be delegated to the central [location] property. This is a read-only view and does not allow alteration
 * of the properties. You should use this interface when you want to disallow modifications.
 */
sealed interface StationaryAction : Action {
    val location: Location
    val type: ActivityType
    override val actionType: ActionType
        get() = ActionType.ACTIVITY
    override val startLocation: Location
        get() = location
    override val endLocation: Location
        get() = location
}

/**
 * A [MovingAction] is an [Action] that starts at the [startLocation] and ends at the [endLocation]. Start and end may
 * be the same (For example a leisure circular walk may be such an action) This is a read-only view and does not allow
 * modification of the properties.
 */

sealed interface MovingAction : Action {
    override val startTime: Duration
    override val endTime: Duration
    override val startLocation: Location
    override val endLocation: Location
    override val actionType: ActionType
        get() = ActionType.LEG
    // TODO insert Transport mode here
}

/**
 * An [Activity] is a [StationaryAction]. The  properties are modifiable at this level. This is the primary
 */
interface Activity : StationaryAction {
    override var location: Location
    override var startTime: Duration
    override var endTime: Duration
    override var earliestStartTime: Duration
    override var latestEndTime: Duration
    override var type: ActivityType

    /**
     * A default implementation to spawn a leg spanning from one activity to another.
     */
    fun createLegTo(other: Activity): Leg {
        return Leg.fromDuration(endTime, other.startTime - endTime, endLocation, other.startLocation)
    }

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    companion object {
        /**
         * Generates an Activity with the provided [location], [startTime], and [duration].
         * The [duration] parameter may be more intuitive in the context of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param location The location of the activity.
         * @param startTime The start time of the activity.
         * @param duration The duration of the activity.
         * @return The generated Activity.
         */
        fun fromDuration(
            location: Location,
            startTime: Duration,
            duration: Duration,
            earliestStartTime: Duration,
            latestEndTime: Duration
        ): Activity {
            return RawActivity(
                location = location,
                startTime = startTime,
                endTime = startTime + duration,
                earliestStartTime = earliestStartTime,
                latestEndTime = latestEndTime
            )
        }
        fun fromDuration(
            location: Location,
            startTime: Duration,
            duration: Duration,
        ): Activity {
            return fromDuration(location, startTime, duration, -Duration.INFINITE, Duration.INFINITE)
        }
    }
}

/**
 * [RawActivity] provides a default implementation of the [Activity] interface.
 * It is primarily intended for debugging and testing purposes, with no additional logic, such as [onBegin] or [onEnd]
 *
 * @property location The location of the activity.
 * @property startTime The start time of the activity.
 * @property endTime The end time of the activity.
 */
data class RawActivity(
    override var location: Location,
    override var startTime: Duration,
    override var endTime: Duration,
    override var earliestStartTime: Duration = -Duration.INFINITE,
    override var latestEndTime: Duration = Duration.INFINITE,
    override var type: ActivityType = ActivityType.UNKNOWN

) : Activity {
    override val duration get() = endTime - startTime
    override fun equals(other: Any?): Boolean {
        if (other !is StationaryAction) return false
        return startTime == other.startTime &&
            location == other.location &&
            endTime == other.endTime
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + earliestStartTime.hashCode()
        result = 31 * result + latestEndTime.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

/**
 * A [Leg] is a [MovingAction]. The properties are mutable. This is the core anchor point for the simulation
 * representing movement.
 */
interface Leg : MovingAction {
    override var startTime: Duration
    override var endTime: Duration
    override var startLocation: Location
    override var endLocation: Location

    override var earliestStartTime: Duration
    override var latestEndTime: Duration

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    companion object {
        /**
         * Generates a leg with the provided [startLocation], [startTime], [endLocation] and [duration].
         * The [duration] parameter may be more intuitive in the context of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param startLocation The start location of the leg.
         * @param endLocation The end location of the leg.
         * @param startTime The start time of the leg.
         * @param duration The duration of the leg.
         * @return The generated leg.
         */
        fun fromDuration(startTime: Duration, duration: Duration, startLocation: Location, endLocation: Location): Leg {
            return RawLeg(
                startTime = startTime,
                endTime = startTime + duration,
                startLocation = startLocation,
                endLocation = endLocation
            )
        }

        /**
         * Generates a leg with the provided [startLocation], [startTime], [endLocation] and [endTime].
         * The [endTime] parameter may be more intuitive in the context of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param startLocation The start location of the leg.
         * @param endLocation The end location of the leg.
         * @param startTime The start time of the leg.
         * @param endTime The duration of the leg.
         * @return The generated leg.
         */
        fun fromEndTime(startTime: Duration, endTime: Duration, startLocation: Location, endLocation: Location): Leg {
            return RawLeg(
                startTime = startTime,
                endTime = endTime,
                startLocation = startLocation,
                endLocation = endLocation
            )
        }
    }
}

/**
 * [RawLeg] provides a default implementation of the [Leg] interface.
 * It is primarily intended for debugging and testing purposes, with no additional logic, such as [onBegin] or [onEnd]
 *
 * @property startLocation The start location of the leg.
 * @property endLocation The end location of the leg.
 * @property startTime The start time of the leg.
 * @property endTime The end time of the leg.
 */
data class RawLeg(
    override var startTime: Duration,
    override var startLocation: Location,
    override var endLocation: Location,
    override var endTime: Duration,
    override var earliestStartTime: Duration = -Duration.INFINITE,
    override var latestEndTime: Duration = Duration.INFINITE

) : Leg {
    override val duration: Duration get() = endTime - startTime

    override fun equals(other: Any?): Boolean {
        if (other !is MovingAction) return false
        return startTime == other.startTime &&
            startLocation == other.startLocation &&
            endLocation == other.endLocation &&
            endTime == other.endTime
    }

    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + startLocation.hashCode()
        result = 31 * result + endLocation.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + earliestStartTime.hashCode()
        result = 31 * result + latestEndTime.hashCode()
        return result
    }
}

/**
 * Default location interface, TODO should be refactored at some point, right now it could also be [Any]
 */
interface Location

enum class ActionType {
    ACTIVITY,
    LEG
}
