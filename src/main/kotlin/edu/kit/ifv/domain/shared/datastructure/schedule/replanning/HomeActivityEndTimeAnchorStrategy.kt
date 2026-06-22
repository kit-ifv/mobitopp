package edu.kit.ifv.domain.shared.datastructure.schedule.replanning
import edu.kit.ifv.domain.shared.behavior.ChoiceModelPurposes
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import edu.kit.ifv.utils.collections.takeUntilSpliced
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * A replanning strategy that treats the *next home activity* as a strong temporal anchor.
 *
 * The strategy operates under two core principles:
 *
 * 1. **The first affected activity must happen.**
 *    The activity identified as `nextAction` is never removed and will always be shifted
 *    to start at the requested `newStartTime`.
 *
 * 2. **The end time of the next home activity is preserved as strictly as possible.**
 *    If a subsequent home activity exists in the schedule, its end time is treated as a
 *    hard (or near-hard) constraint during replanning. Intermediate activities may be
 *    shifted, shortened, or removed in order to satisfy this constraint.
 *
 * Replanning proceeds as follows:
 * - All activities from `nextAction` up to (and including) the next home activity are
 *   considered relevant.
 * - If a home activity exists, a temporal conflict is constructed spanning from the new
 *   start time of `nextAction` to the end time of that home activity.
 * - Conflict resolution is delegated to [PriorityBasedConflictResolver], which may propose
 *   start-time/duration adjustments or deletions.
 *
 * Fallback / damage control:
 * - If no valid conflict resolution can be found, all removable intermediate activities
 *   are deleted.
 * - Both the first activity and the home activity are reduced to their minimum durations.
 * - The first activity is then shifted to the requested start time, accepting that the
 *   home activity’s end time may drift, but attempting to limit the damage.
 *
 * If no subsequent home activity exists, no anchoring is possible and the schedule is
 * freely shifted starting from `nextAction`.
 *
 * This strategy is intentionally conservative: it prefers deleting or compressing
 * activities over violating the end time of the next home activity.
 */
class HomeActivityEndTimeAnchorStrategy(choiceModelPurposes: ChoiceModelPurposes) : ReplanningStrategy {
    private val homeActivity = choiceModelPurposes.home
    private val resolver: PriorityBasedConflictResolver = PriorityBasedConflictResolver(choiceModelPurposes)
    override fun replan(schedule: Schedule, newStartTime: AbsoluteTime, nextAction: LinkedActivity) {
        val (relevantActivities, potentialNextHomeActivity) = schedule
            .activities()
            .dropWhile { it != nextAction }
            .takeUntilSpliced {
                it.type == homeActivity
            }

        val suggestedChanges = potentialNextHomeActivity?.let {
            val conflict = Conflict(
                startTime = newStartTime,
                endTime = potentialNextHomeActivity.endTime,
                startLocation = schedule.lastAction()?.endLocation,
                endLocation = potentialNextHomeActivity.location,
                actions = relevantActivities + potentialNextHomeActivity,
            )

            resolver.resolveConflict(conflict)
        } ?: run {
            // If there is no conflicting home activity that should be used as an anchor you can shift to your hearts content.
            nextAction.shiftStartTo(newStartTime)
            return
        }
        if (suggestedChanges.all { it == null } ||
            suggestedChanges.first() == null ||
            suggestedChanges.last() == null
        ) {
            // The first one should not be null because that activity MUST happen.
            // Similar the potentialHome Activity also must happen (only if it is the last act of all but thats a todo)

            // Since we have not managed to find a good conflict solution, remove all removeable actrivities...
            relevantActivities.drop(1).forEach {
                schedule.remove(it)
            }
            // And set the minimum time of the first activity to its minimumm
            relevantActivities.firstOrNull()?.let {
                resolver.setMinimumDuration(it)
            }
            // And the next home activity also to its minimum
            resolver.setMinimumDuration(potentialNextHomeActivity)
            // Now we shift the first activity to the start time. At this point it is a hope and a dream that
            // the end of the last home activity will be respected, but damage control hopefully ensures that the
            // end of that home activity wont drift away too far.
            nextAction.shiftStartTo(newStartTime)
            return
        }
        // Apply the suggested changes to the activities, null represents deletion.
        suggestedChanges.zip(relevantActivities + potentialNextHomeActivity) { change, activity ->
            if (change == null) {
                schedule.remove(activity)
            } else {
                activity.startTime = change.startTime
                activity.duration = change.duration
            }
        }
    }
}
