package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.Schedule
import domain.shared.enums.ActivityType
import utils.units.AbsoluteTime
import kotlin.time.Duration

/**
 * When the schedule is modified, and a conflict occurs, then the replanning strategy should handle how the schedule is
 * modified so that the target action can be suitably inserted into the schedule.
 */
fun interface ReplanningStrategy {
    fun replan(schedule: Schedule, newStartTime: AbsoluteTime, nextAction: LinkedActivity)
}

val SHIFT = ReplanningStrategy { schedule, conflict, nextAction ->
    nextAction.shiftStartTo(conflict)
}

class SkipActivitiesTillHome(private val homeActivity: ActivityType): ReplanningStrategy
{
    override fun replan(
        schedule: Schedule,
        newStartTime: AbsoluteTime,
        nextAction: LinkedActivity
    ) {

        val relevantActivities = schedule
            .activities()
            .dropWhile { it != nextAction }
            .takeWhile { it.type != homeActivity }
        val offset: Duration = newStartTime - nextAction.startTime
        relevantActivities
            .filter { it.endTime + offset > it.latestEndTime  }
            .forEach {
                schedule.remove(it)
            }



    }
}