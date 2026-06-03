package domain.shared.datastructure.schedule.modifier

import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.action.ActionType
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.enums.ActivityType
import utils.units.AbsoluteTime
import kotlin.time.Duration.Companion.minutes

class SkipToNextHomeActivity(
    private val home: ActivityType,
    private val legEndTimeRecalculator: (AbsoluteTime) -> AbsoluteTime = { it + 10.minutes },
) : ScheduleModifier {

    override fun applyTo(schedule: Schedule, currentTime: AbsoluteTime) {
        val nextHomeActivity =
            schedule
                .activities()
                .dropWhile { it.endTime < currentTime }
                .first { it.type == home }
        schedule.dropUntil(nextHomeActivity)

        // If the current action is a leg it might be reasonable to redirect the leg, rather than to end it and
        // insert a new leg
        schedule.present?.let {
            if (it.actionType == ActionType.LEG) {
                it.endLocation = nextHomeActivity.location
                it.endTime = legEndTimeRecalculator(currentTime)

                nextHomeActivity.shiftStartTo(it.endTime)
                return
            }
        }
        // If the current action is either an activity or simply not present we may want to add a leg, if

        nextHomeActivity.apply {
            startTime = AbsoluteTime.max(
                schedule.lastStartedAction().endTime + 10.minutes,
                latestEndTime - duration,
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
                    nextHomeActivity.location,
                ),
            )
        }
    }
}
