package edu.kit.ifv.domain.shared.datastructure.schedule.action
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.time.Duration

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
    override var earliestStartTime: AbsoluteTime = AbsoluteTime.Companion.MINUS_INFINITY,
    override var latestEndTime: AbsoluteTime = AbsoluteTime.Companion.INFINITY,
    override var type: ActivityType = ActivityType.Companion.UNKNOWN,

) : Activity {

    init {
        require(duration > Duration.Companion.ZERO) {
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
            if (earliestStartTime ==
                AbsoluteTime.Companion.MINUS_INFINITY
            ) {
                ""
            } else {
                "earliestStartTime=$earliestStartTime"
            }
        val latestEndTime = if (latestEndTime == AbsoluteTime.Companion.INFINITY) "" else "latestEndTime=$latestEndTime"
        return "[startTime=$startTime, endTime=$endTime], location = ${location.zoneId}" +
            " t= ${type.description.first()}" +
            "(${type.code}) e=$earlyStartTime l=$latestEndTime "
    }
}
