package edu.kit.ifv.domain.shared.datastructure.schedule.action
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.time.Duration

/**
 * Represents a linked activity, which is a linked action associated with an activity.
 * @param original The original activity.
 * @param previous The previous linked action in the sequence.
 * @param next The next linked action in the sequence.
 */
class LinkedActivity(
    override val original: Activity,
    override var previous: LinkedAction? = null,
    override var next: LinkedAction? = null,
) : LinkedAction(),
    Activity by original {

    init {
        require(original !is LinkedActivity)
    }

    override var location: StandardLocation
        get() = original.location
        set(value) {
            if (value != location) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var startLocation: StandardLocation
        get() = original.location
        set(value) {
            if (value != startLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
            }
        }
    override var endLocation: StandardLocation
        get() = original.location
        set(value) {
            if (value != endLocation) {
                original.location = value
                next?.startLocation = value
                previous?.endLocation = value
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

    override var duration: Duration
        get() = original.duration
        set(value) {
            // delegate to change of endTime, TODO validate in bounds, make sure endtime never < start time
            this.endTime = this.startTime + value
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
    override var type: ActivityType
        get() = original.type
        set(value) {
            original.type = value
        }

    override fun equals(other: Any?): Boolean = original == other

    override fun hashCode(): Int = original.hashCode()

    override fun link(lower: LinkedAction?, higher: LinkedAction?): LinkedActivity = this
}
