package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.action.LinkedActivity
import utils.units.AbsoluteTime

/**
 * A minimal replanning strategy that performs no conflict resolution.
 *
 * This strategy simply shifts the start time of the next activity to the
 * requested `newStartTime`, the remainder of the schedule will be forced to shift accordingly.
 *
 * This is primarily useful as:
 * - a baseline or control strategy,
 * - a fallback when no structured replanning is desired,
 * - or for testing and debugging purposes.
 *
 */
object BananaReplanning : ReplanningStrategy {
    override fun replan(schedule: Schedule, newStartTime: AbsoluteTime, nextAction: LinkedActivity) {
        nextAction.shiftStartTo(newStartTime)
    }
}
