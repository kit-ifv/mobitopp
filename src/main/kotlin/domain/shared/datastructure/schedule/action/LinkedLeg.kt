package domain.shared.datastructure.schedule.action

import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime

/**
 * Represents a linked leg, which is a linked action associated with a leg.
 * @param original The original leg.
 * @param previous The previous linked action in the sequence.
 * @param next The next linked action in the sequence.
 */
class LinkedLeg(
    override val original: Leg,
    override var previous: LinkedAction? = null,
    override var next: LinkedAction? = null,
) : LinkedAction(),
    Leg by original {
    override var startLocation: StandardLocation
        get() = original.startLocation
        set(value) {
            if (value != startLocation) {
                original.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: StandardLocation
        get() = original.endLocation
        set(value) {
            if (value != endLocation) {
                original.endLocation = value
                next?.startLocation = value
            }
        }
    override var startTime: AbsoluteTime
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
    override var endTime: AbsoluteTime
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
    override var earliestStartTime: AbsoluteTime
        get() = original.earliestStartTime
        set(value) {
            original.earliestStartTime = value
        }
    override var latestEndTime: AbsoluteTime
        get() = original.latestEndTime
        set(value) {
            original.latestEndTime = value
        }

    override fun equals(other: Any?): Boolean = original == other

    override fun hashCode(): Int = original.hashCode()

    override fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedLeg = this
}