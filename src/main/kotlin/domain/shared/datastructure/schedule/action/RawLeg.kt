package domain.shared.datastructure.schedule.action

import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime
import kotlin.time.Duration

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
    override var earliestStartTime: AbsoluteTime = AbsoluteTime.Companion.MINUS_INFINITY,
    override var latestEndTime: AbsoluteTime = AbsoluteTime.Companion.INFINITY,
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

    override fun toString(): String = "[$startTime, $endTime] mode=$transportType, from=$startLocation to=$endLocation"
}
