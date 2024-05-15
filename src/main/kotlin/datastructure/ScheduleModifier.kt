package datastructure

import utils.collections.iterate
import utils.units.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

// This is future work for rescheduling
fun interface ScheduleModifier {
    fun applyTo(schedule: Schedule, currentTime: Duration)
}

fun Schedule.applyAt(currentTime: Duration, functor: ScheduleModifier) {
    functor.applyTo(this, currentTime)
}
object SkipToNextHomeActivity : ScheduleModifier {
    override fun applyTo(schedule: Schedule, currentTime: Duration) {
        if (schedule.present.iterate(schedule.future).hasTimeBoundViolations()) {
            val nextHomeActivity =
                schedule.activities().dropWhile { it.endTime < currentTime }.first { it.type == ActivityType.HOME }
            schedule.dropUntil(nextHomeActivity)

            // If the current action is a leg it might be reasonable to redirect the leg, rather than to end it and
            // insert a new leg
            schedule.present?.let {
                if (it.actionType == ActionType.LEG) {
                    it.endLocation = nextHomeActivity.location
                    it.endTime = complexRecalculationPlsImplement(currentTime)

                    nextHomeActivity.shiftStartTo(it.endTime)
                    return
                }
            }
            // If the current action is either an activity or simply not present we may want to add a leg, if

            nextHomeActivity.apply {
                startTime = max(schedule.lastAction().endTime + 10.minutes, latestEndTime - duration)
                endTime = latestEndTime
            }
            if (schedule.lastAction().endLocation != nextHomeActivity.location) {
                // Add a leg from the last location to the home activity, should the need arise
                schedule.add(
                    Leg.fromEndTime(
                        max(schedule.lastAction().endTime, currentTime),
                        nextHomeActivity.startTime,
                        schedule.lastAction().endLocation,
                        nextHomeActivity.location
                    )
                )
            }
        }
    }

    // I currently have no info on how to calculate the duration from an unknown location to the destination, also I
    // have no information about the mode.
    private fun complexRecalculationPlsImplement(currentTime: Duration): Duration {
        return currentTime + 10.minutes
    }
}
