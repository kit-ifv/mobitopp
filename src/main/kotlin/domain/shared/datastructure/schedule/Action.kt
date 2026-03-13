package domain.shared.datastructure.schedule

import domain.shared.enums.ActivityType
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.shared.location.LocationOld
import utils.units.AbsoluteTime
import utils.units.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * An Action is the central aspect of mobility behaviour in this simulation mobitopp. In its simplest form an action
 * consists of a [startLocation] and a [startTime], as well as an [endLocation] and an [endTime]. In this modelling
 * approach, an action is an atomic operation - There are no smaller representations of sub-actions. It is your duty
 * when modelling future problems to break your domain down to this level.
 *
 * Note that an action only holds location and time information, your implementation must provide additional details
 * should you require them.
 */
sealed interface Action : Comparable<Action> {
    val startTime: AbsoluteTime
    val actionType: ActionType

    /**
     * [duration] is a derived property of an action by the difference of [endTime] and [startTime]. If you see
     * that your code is relying heavily on this property it might be prudent to implement your own Action with
     * a backing field rather than a derived property
     */
    val duration: Duration get() = endTime - startTime
    val endTime: AbsoluteTime

    val startLocation: StandardLocation
    val endLocation: StandardLocation

    val earliestStartTime: AbsoluteTime
    val latestEndTime: AbsoluteTime

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
        if (startTime == other.startTime && endTime == other.endTime) { return 0 }
        if (endTime <= other.startTime) return -1
        if (other.endTime <= startTime) return 1
        return 0
    }

    fun <T> accept(actionVisitor: ActionVisitor<T>): T
    operator fun compareTo(time: AbsoluteTime): Int {
        /*
            if (endTime < time) return -1
            if (startTime > time) return 1
            return 0

            in branch less
         */
        return (startTime > time).compareTo(endTime < time)
    }
}

/**
 * Strong consistency occurs when the actions are weakly consistent and all actions are well structured.
 */
fun Iterable<Action>.isConsistent(): Boolean {
    return isWeaklyConsistent() && all { it.startTime <= it.endTime }
}

/**
 * This method verifies the consistency of any iterable of actions by having continuous locations and no time interval
 * overlaps
 *
 */
fun Iterable<Action>.isWeaklyConsistent(): Boolean {
    return zipWithNext { first, second ->
        first.endLocation == second.startLocation && first.endTime <= second.startTime }.all { it }
}
fun Iterable<Action>.hasTimeBoundViolations(): Boolean {
    return any { it.startTime < it.earliestStartTime || it.endTime > it.latestEndTime }
}

operator fun Iterable<Action>.contains(action: Action): Boolean {
    return any { it.compareTo(action) == 0 }
}

/**
 * A [StationaryAction] is an [Action] that takes place at one and only one [LocationOld]. The [startLocation] and [endLocation]
 * can therefore be delegated to the central [location] property. This is a read-only view and does not allow alteration
 * of the properties. You should use this interface when you want to disallow modifications.
 */
sealed interface StationaryAction : Action {
    val location: StandardLocation
    val type: ActivityType
    override val actionType: ActionType
        get() = ActionType.ACTIVITY
    override val startLocation: StandardLocation
        get() = location
    override val endLocation: StandardLocation
        get() = location
}

/**
 * A [MovingAction] is an [Action] that starts at the [startLocation] and ends at the [endLocation]. Start and end may
 * be the same (For example a leisure circular walk may be such an action) This is a read-only view and does not allow
 * modification of the properties.
 */

sealed interface MovingAction : Action {
    override val startTime: AbsoluteTime
    override val endTime: AbsoluteTime
    override val startLocation: StandardLocation
    override val endLocation: StandardLocation
    override val actionType: ActionType
        get() = ActionType.LEG

    val transportType: Mode // TODO can we rename this property to mode?
}

/**
 * An [Activity] is a [StationaryAction]. The  properties are modifiable at this level. This is the primary
 */
interface Activity : StationaryAction {
    override var location: StandardLocation
    override var startTime: AbsoluteTime
    override var endTime: AbsoluteTime
    override var earliestStartTime: AbsoluteTime
    override var latestEndTime: AbsoluteTime
    override var type: ActivityType

    /**
     * A default implementation to spawn a leg spanning from one activity to another.
     */
    fun createLegTo(other: Activity, mode: Mode): Leg {
        val duration = min(other.startTime - endTime, 1.seconds)
        require(!duration.isNegative()) {
            "Cannot create Leg with negative duration $this -> $other"
        }
        return Leg.fromDuration(endTime, duration, endLocation, other.startLocation, mode)
    }

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun <T> accept(actionVisitor: ActionVisitor<T>): T {
        return actionVisitor.visitActivity(this)
    }

    fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedActivity {
        val act = LinkedActivity(this)
        act.previous = lower
        act.next = higher
        act.previous?.next = act
        act.next?.previous = act
        return act
    }

    companion object {
        /**
         * Generates an Activity with the provided [location], [startTime], and [duration].
         * The [duration] parameter may be more intuitive in the mobitopp of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param location The location of the activity.
         * @param startTime The start time of the activity.
         * @param duration The duration of the activity.
         * @return The generated Activity.
         */
        @Suppress("LongParameterList") // Maybe in the future split into time info and location/type info?
        fun fromDuration(
            location: StandardLocation,
            startTime: AbsoluteTime,
            duration: Duration,
            earliestStartTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY,
            latestEndTime: AbsoluteTime = AbsoluteTime.INFINITY,
            type: ActivityType = ActivityType.UNKNOWN,
        ): Activity {
            return RawActivity(
                location = location,
                startTime = startTime,
                endTime = startTime + duration,
                earliestStartTime = earliestStartTime,
                latestEndTime = latestEndTime,
                type = type
            )
        }
    }
}

/**
 * [RawActivity] provides a default implementation of the [Activity] interface.
 * It is primarily intended for debugging and testing purposes.
 *
 * @property location The location of the activity.
 * @property startTime The start time of the activity.
 * @property endTime The end time of the activity.
 */

data class RawActivity(
    override var location: StandardLocation,
    override var startTime: AbsoluteTime,
    override var endTime: AbsoluteTime,
    override var earliestStartTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY,
    override var latestEndTime: AbsoluteTime = AbsoluteTime.INFINITY,
    override var type: ActivityType = ActivityType.UNKNOWN,

    ) : Activity {

    init {
        require(duration > Duration.ZERO) {
            "Duration must be positive"
        }
    }

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
        return result
    }

    override fun toString(): String {
        val earlyStartTime =
            if (earliestStartTime == AbsoluteTime.MINUS_INFINITY) "" else "earliestStartTime=$earliestStartTime"
        val latestEndTime = if (latestEndTime == AbsoluteTime.INFINITY) "" else "latestEndTime=$latestEndTime"
        return "[startTime=$startTime, endTime=$endTime], location = ${location.zoneID}" +
            " t= ${type.description.first()}" +
            "(${type.code}) e=$earlyStartTime l=$latestEndTime "
    }
}

/**
 * A [Leg] is a [MovingAction]. The properties are mutable. This is the core anchor point for the simulation
 * representing movement.
 */
interface Leg : MovingAction {
    override var startTime: AbsoluteTime
    override var endTime: AbsoluteTime
    override var startLocation: StandardLocation
    override var endLocation: StandardLocation

    override var earliestStartTime: AbsoluteTime
    override var latestEndTime: AbsoluteTime

    override var transportType: Mode
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    override fun <T> accept(actionVisitor: ActionVisitor<T>): T {
        return actionVisitor.visitLeg(this)
    }

    fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedLeg {
        val act = LinkedLeg(this)
        act.previous = lower
        act.next = higher
        act.previous?.next = act
        act.next?.previous = act
        return act
    }

    companion object {
        /**
         * Generates a leg with the provided [startLocation], [startTime], [endLocation] and [duration].
         * The [duration] parameter may be more intuitive in the mobitopp of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param startLocation The start location of the leg.
         * @param endLocation The end location of the leg.
         * @param startTime The start time of the leg.
         * @param duration The duration of the leg.
         * @return The generated leg.
         */
        fun fromDuration(
            startTime: AbsoluteTime,
            duration: Duration,
            startLocation: StandardLocation,
            endLocation: StandardLocation,
            mode: Mode = MODEUNKOWN,
        ): Leg {
            return RawLeg(
                startTime = startTime,
                endTime = startTime + duration,
                startLocation = startLocation,
                endLocation = endLocation,
                transportType = mode
            )
        }

        /**
         * Generates a leg with the provided [startLocation], [startTime], [endLocation] and [endTime].
         * The [endTime] parameter may be more intuitive in the mobitopp of activities rather than the end time.
         * The end time is calculated in accordance.
         *
         * @param startLocation The start location of the leg.
         * @param endLocation The end location of the leg.
         * @param startTime The start time of the leg.
         * @param endTime The duration of the leg.
         * @return The generated leg.
         */
        fun fromEndTime(
            startTime: AbsoluteTime,
            endTime: AbsoluteTime,
            startLocation: StandardLocation,
            endLocation: StandardLocation,
            mode: Mode = MODEUNKOWN,
        ): Leg {
            return RawLeg(
                startTime = startTime,
                endTime = endTime,
                startLocation = startLocation,
                endLocation = endLocation,
                transportType = mode
            )
        }
    }
}

/**
 * [RawLeg] provides a default implementation of the [Leg] interface.
 * It is primarily intended for debugging and testing purposes.
 *
 * @property startLocation The start location of the leg.
 * @property endLocation The end location of the leg.
 * @property startTime The start time of the leg.
 * @property endTime The end time of the leg.
 */
data class RawLeg(
    override var startTime: AbsoluteTime,
    override var startLocation: StandardLocation,
    override var endLocation: StandardLocation,
    override var endTime: AbsoluteTime,
    override var earliestStartTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY,
    override var latestEndTime: AbsoluteTime = AbsoluteTime.INFINITY,
    override var transportType: Mode,

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
        return result
    }

    override fun toString(): String {
        return "[$startTime, $endTime] mode=$transportType, from=$startLocation to=$endLocation"
    }
}

enum class ActionType {
    ACTIVITY,
    LEG
}
