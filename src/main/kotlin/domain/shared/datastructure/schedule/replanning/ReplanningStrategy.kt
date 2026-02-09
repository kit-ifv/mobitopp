package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.Schedule
import utils.units.AbsoluteTime
import kotlin.time.Duration

/**
 * When the schedule is modified, and a conflict occurs, then the replanning strategy should handle how the schedule is
 * modified so that the target action can be suitably inserted into the schedule.
 */
fun interface
ReplanningStrategy {
    fun replan(schedule: Schedule, newStartTime: AbsoluteTime, nextAction: LinkedActivity)
    fun replan(schedule: Schedule?, newStartTime: AbsoluteTime, nextAction: LinkedActivity) =
        schedule?.let { replan(it, newStartTime, nextAction) }

    companion object {
        val SHIFT = ReplanningStrategy { schedule, conflict, nextAction ->
            nextAction.shiftStartTo(conflict)
        }
    }
}

