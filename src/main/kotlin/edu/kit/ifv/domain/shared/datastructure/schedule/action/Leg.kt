package edu.kit.ifv.domain.shared.datastructure.schedule.action
import edu.kit.ifv.domain.shared.datastructure.schedule.ActionVisitor
import edu.kit.ifv.domain.shared.enums.MODEUNKOWN
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.AbsoluteTime
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
