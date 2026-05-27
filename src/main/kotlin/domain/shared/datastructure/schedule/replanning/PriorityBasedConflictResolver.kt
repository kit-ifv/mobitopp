package domain.shared.datastructure.schedule.replanning

import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.StationaryAction
import edu.kit.ifv.units.max
import utils.units.AbsoluteTime
import utils.units.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Resolves a temporal conflict by greedily making a set of activities fit into a fixed time window.
 *
 * Given a [Conflict] with a time interval `[conflict.startTime, conflict.endTime]` and an ordered list
 * of stationary activities, this resolver proposes a list of [Change?] aligned with the conflict's
 * activity order:
 *
 * - A non-null [Change] updates the corresponding activity's start time and duration.
 * - `null` indicates that the corresponding activity should be removed from the schedule.
 *
 * Resolution strategy (high level):
 * 1. Compute the current required time as the sum of activity durations plus a fixed
 *    inter-activity buffer ([tripBufferEstimate]) between consecutive remaining activities.
 * 2. If the activities already fit, shift them to start at [Conflict.startTime] and insert buffers
 *    to avoid overlaps.
 * 3. If they do not fit, repeatedly remove activities starting with the *lowest priority*
 *    (as defined by [priority]) until the remaining set can fit within the available time window.
 *    The first activity is typically assigned very high priority by default and therefore tends to be
 *    removed last, but it may still be removed if necessary to obtain feasibility.
 * 4. Once a feasible subset remains, compress ("squeeze") durations down towards their per-activity
 *    minimum bounds (as defined by minimumDuration) and shift again to produce a non-overlapping
 *    schedule.
 *
 * Notes:
 * - This resolver is heuristic/greedy: it aims for a feasible solution, not an optimal one.
 * - It proposes changes only; applying them to the schedule (including deletion) is the caller's job.
 */
class PriorityBasedConflictResolver(

    private val choiceModelPurposes: ChoiceModelPurposes,
    private val tripBufferEstimate: Duration = 15.minutes,
    @Suppress("MagicNumber")
    private val priority: (IndexedValue<StationaryAction>) -> Int = { (index, action) ->
        if (index == 0) {
            Int.MAX_VALUE
        } else {
            when (action.type) {
                choiceModelPurposes.home -> 99999
                choiceModelPurposes.work, in choiceModelPurposes.educationTypes -> 999
                else -> -action.startTime.minutesSinceStart.toInt()
            }
        }
    },
    private val minimumDuration: (StationaryAction) -> Duration = { action ->
        if (action.duration < 15.minutes) {
            action.duration / 2 // It is already an asinine activity, a bound wouldnt protect it.
        } else {
            when (action.type) {
                choiceModelPurposes.home, choiceModelPurposes.work, in choiceModelPurposes.educationTypes ->
                    max(action.duration / 2, min(action.duration, 15.minutes))

                else -> min(action.duration, 15.minutes)
            }
        }
    },
) : ConflictResolver {
    fun setMinimumDuration(activity: LinkedActivity) {
        activity.duration = minimumDuration(activity)
    }
    private inner class ChangeTracker(activities: List<StationaryAction>) {
        private val minimumDuration = activities.map { minimumDuration(it) }.toMutableList()

        private val priorities: MutableList<Int> = activities.withIndex()
            .map { priority(it) }
            .withIndex().sortedBy { it.value }.map { it.index }.toMutableList()
        private val changeArray: Array<Change?> = activities.map { it.toChange() }.toTypedArray()
        private val indexTracker: MutableList<Int> = changeArray.indices.toMutableList()
        val size get() = indexTracker.size
        fun get(position: Int): Change = changeArray[indexTracker[position]]!!
        fun shiftStart(startTime: AbsoluteTime) {
            if (indexTracker.isEmpty()) return // There is nothing left to shift.
            get(0).startTime = startTime
            for (pos in 1 until indexTracker.size) {
                val current = get(pos)
                val previous = get(pos - 1)
                current.startTime = AbsoluteTime.Companion.max(current.startTime, previous.endTime + tripBufferEstimate)
            }
        }

        fun canHandle(requiredTimeReduction: Duration): Boolean = minimalDuration() <= requiredTimeReduction
        fun isNotEmpty(): Boolean = size != 0

        fun removeAt(position: Int) {
            val index = indexTracker.indexOf(position)
            changeArray[position] = null
            require(priorities.contains(position)) {
                "Else this dno senes"
            }
            indexTracker.removeAt(index)
            minimumDuration.removeAt(index)
            priorities.remove(position)
        }

        fun removeLeastPrioritizedElement() {
            require(priorities.isNotEmpty()) {
                "How did we get here"
            }
            removeAt(priorities.first())
        }

        fun currentDuration() = indexTracker.sumOfD {
            changeArray[it]!!.duration
        } + tripBufferEstimate * (minimumDuration.size - 1)

        fun minimalDuration(): Duration = minimumDuration.sumOfD {
            it
        } + tripBufferEstimate * (minimumDuration.size - 1)

        inline fun orderedByPriority(lambda: (IndexedValue<Change>) -> Unit) {
            priorities.withIndex().forEach { (index, pos) ->
                lambda(IndexedValue(index, changeArray[pos]!!))
            }
        }

        fun squeeze(howMuchTimeToSqueeze: Duration) {
            var remainingDelta = currentDuration() - howMuchTimeToSqueeze
            orderedByPriority { (index, element) ->
                if (remainingDelta <= Duration.Companion.ZERO) {
                    return
                }
                val currentDuration = element.duration
                require(minimumDuration.size > index) {
                    "ERRROROROENE"
                }
                element.duration = max(currentDuration - remainingDelta, minimumDuration[index])
                remainingDelta -= currentDuration - element.duration
            }
        }

        fun toActionSet(): List<Change?> = changeArray.toList()
    }

    override fun resolveConflict(conflict: Conflict): List<Change?> {
        val changeTracker = ChangeTracker(conflict.actions)
        val currentRequiredTime = changeTracker.currentDuration()
        val availableTime = conflict.endTime - conflict.startTime

        val conflictTimeAmount = currentRequiredTime - availableTime

        if (conflictTimeAmount <= Duration.Companion.ZERO) {
            changeTracker.shiftStart(conflict.startTime)

            return changeTracker.toActionSet()
        }
        while (!changeTracker.canHandle(availableTime) && changeTracker.isNotEmpty()) {
            if (changeTracker.size == 0) {
                println("Not a good scenario")
            }
            changeTracker.removeLeastPrioritizedElement()
        }
        changeTracker.squeeze(availableTime)
        changeTracker.shiftStart(conflict.startTime)
        return changeTracker.toActionSet()
    }

    private fun Collection<Activity>.minimalDuration(): Duration = sumOfD { minimumDuration(it) }

    private fun <T> Iterable<T>.sumOfD(selector: (T) -> Duration): Duration {
        var counter = Duration.Companion.ZERO
        forEach {
            counter += selector(it)
        }
        return counter
    }
}
