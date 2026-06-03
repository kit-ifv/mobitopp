package domain.shared.datastructure.schedule.action

import domain.shared.datastructure.schedule.ActionVisitor
import domain.shared.enums.ActivityType
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime
import utils.units.min
import utils.units.sinceStart
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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

    override fun <T> accept(actionVisitor: ActionVisitor<T>): T = actionVisitor.visitActivity(this)

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
        ): Activity = RawActivity(
            location = location,
            startTime = startTime,
            endTime = startTime + duration,
            earliestStartTime = earliestStartTime,
            latestEndTime = latestEndTime,
            type = type,
        )

        fun fromTimes(start: AbsoluteTime, end: AbsoluteTime, type: ActivityType): Activity {
            require(
                start <= end,
            ) { "Cannot create activity where start time is larger than end time: [start=$start , end=$end]" }

            return fromDuration(StandardLocation.LOCATIONUNKNOWN, start, end - start, type = type)
        }

        fun fromTimes(start: Duration, end: Duration, type: ActivityType): Activity = fromTimes(
            start.sinceStart,
            end.sinceStart,
            type,
        )
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
        return "[startTime=$startTime, endTime=$endTime], location = ${location.zoneId}" +
            " t= ${type.description.first()}" +
            "(${type.code}) e=$earlyStartTime l=$latestEndTime "
    }
}
