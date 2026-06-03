package domain.shared.datastructure.schedule.action

import domain.shared.datastructure.schedule.ActionVisitor
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime
import kotlin.time.Duration

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

    override fun <T> accept(actionVisitor: ActionVisitor<T>): T = actionVisitor.visitLeg(this)

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
        ): Leg = RawLeg(
            startTime = startTime,
            endTime = startTime + duration,
            startLocation = startLocation,
            endLocation = endLocation,
            transportType = mode,
        )

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
        ): Leg = RawLeg(
            startTime = startTime,
            endTime = endTime,
            startLocation = startLocation,
            endLocation = endLocation,
            transportType = mode,
        )
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

    override fun toString(): String = "[$startTime, $endTime] mode=$transportType, from=$startLocation to=$endLocation"
}
