package domain.shared.datastructure.schedule

import domain.shared.enums.ActivityType
import utils.collections.iterate
import utils.units.AbsoluteTime
import kotlin.time.Duration.Companion.minutes

// This is future work for rescheduling
fun interface ScheduleModifier {
    fun applyTo(schedule: Schedule, currentTime: AbsoluteTime)
}

fun Schedule.applyAt(currentTime: AbsoluteTime, functor: ScheduleModifier) {
    functor.applyTo(this, currentTime)
}

class SkipToNextHomeActivity(private val home: ActivityType) : ScheduleModifier {

    override fun applyTo(schedule: Schedule, currentTime: AbsoluteTime) {
        if (schedule.present.iterate(schedule.future).hasTimeBoundViolations()) {
            val nextHomeActivity =
                schedule.activities().dropWhile { it.endTime < currentTime }
                    .first { it.type == home }
            schedule.dropUntil(nextHomeActivity)

            // If the current action is a leg it might be reasonable to redirect the leg, rather than to end it and
            // insert a new leg
            schedule.present?.let {
                if (it.actionType == ActionType.LEG) {
                    it.endLocation = nextHomeActivity.location
                    it.endTime = complexCalPlsImplement(currentTime)

                    nextHomeActivity.shiftStartTo(it.endTime)
                    return
                }
            }
            // If the current action is either an activity or simply not present we may want to add a leg, if

            nextHomeActivity.apply {
                startTime = AbsoluteTime.max(
                    schedule.lastStartedAction().endTime + 10.minutes,
                    latestEndTime - duration
                )
                endTime = latestEndTime
            }
            if (schedule.lastStartedAction().endLocation != nextHomeActivity.location) {
                // Add a leg from the last location to the home activity, should the need arise
                schedule.add(
                    Leg.fromEndTime(
                        AbsoluteTime.max(schedule.lastStartedAction().endTime, currentTime),
                        nextHomeActivity.startTime,
                        schedule.lastStartedAction().endLocation,
                        nextHomeActivity.location
                    )
                )
            }
        }
    }

    // I currently have no info on how to calculate the duration from an unknown location to the destination, also I
    // have no information about the mode.
    private fun complexCalPlsImplement(currentTime: AbsoluteTime): AbsoluteTime {
        return currentTime + 10.minutes
    }
}
