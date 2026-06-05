package edu.kit.ifv.domain.shared.datastructure.schedule.action
import edu.kit.ifv.domain.shared.datastructure.schedule.ActionVisitor
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.AbsoluteTime
import edu.kit.ifv.utils.units.min
import edu.kit.ifv.utils.units.sinceStart
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
        return Leg.Companion.fromDuration(endTime, duration, endLocation, other.startLocation, mode)
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
