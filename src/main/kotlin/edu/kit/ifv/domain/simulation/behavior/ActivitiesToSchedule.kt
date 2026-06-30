package edu.kit.ifv.domain.simulation.behavior
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedLeg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.isConsistent
import edu.kit.ifv.domain.shared.datastructure.schedule.blocks.ActivityBlock
import edu.kit.ifv.domain.shared.datastructure.schedule.blocks.LinkedTrip
import edu.kit.ifv.domain.shared.datastructure.schedule.plans.BlockModel
import edu.kit.ifv.domain.shared.datastructure.schedule.plans.IDispatcher
import edu.kit.ifv.domain.shared.enums.MODEUNKOWN
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.data.PlannedActivity
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

fun List<PlannedActivity>.toSchedule(
    dispatcher: IDispatcher,
    repairStrategy: (Activity, LinkedActivity) -> Unit = { prev, broken ->
        // TODO check and inform if triggered.
        broken.shiftStartTo(prev.endTime + 1.minutes)
    },
): Schedule {
    val linkedActivities = map { LinkedActivity(it.toActivity()) }

    require(isNotEmpty()) {
        "Cannot use an empty list to generate a schedule, at least a home activity is required"
    }

    val filteredActivities = mutableListOf(linkedActivities.first())
    for (i in 1 until size) {
        val linkedActivity = linkedActivities[i]
        val previous = filteredActivities.last()
        if (previous.endTime > linkedActivity.startTime) {
            repairStrategy(previous, linkedActivity)
        }
        filteredActivities.add(linkedActivity)
        require(linkedActivity.duration >= Duration.ZERO) {
            "Duration must be positive, $linkedActivity has a negative duration." +
                " The repair strategy may have corrected too strongly"
        }
        require(previous.endTime <= linkedActivity.startTime) {
            "Activity $linkedActivity starts before predecessor ends pred=$previous"
        }
    }

    require(filteredActivities.size == this.size) {
        "Not alle planned activities were translated to schedule activities, please fix"
    }

    val activityBlocks = filteredActivities.map { ActivityBlock(sortedSetOf(it)) }
    val firstActivityBlock: ActivityBlock = activityBlocks.first()
    val createdLegs = activityBlocks.zipWithNext { first, second ->

        val firstActivity = first.item.first()
        val secondActivity = second.item.first()
        val linkedLeg = LinkedLeg(firstActivity.createLegTo(secondActivity, MODEUNKOWN))
        firstActivity.next = linkedLeg
        linkedLeg.previous = firstActivity
        linkedLeg.next = secondActivity
        secondActivity.previous = linkedLeg
        val legBlock = LinkedTrip(setOf(linkedLeg), first, second)
        first.next = legBlock
        second.previous = legBlock
        LinkTrip(legBlock, dispatcher)
    }

    val targetModel =
        BlockModel(
            dispatcher,
            createdLegs.toMutableList(),
            firstActivityBlock,
            filteredActivities.toSortedSet(),
        )

    require(targetModel.actions().isConsistent()) {
        "The built schedule is inconsistent, please fix: \n${targetModel.actions().joinToString("\n -")}"
    }

    return Schedule(targetModel)
}

// TODO this should be exposed in a factory so that the system can handle different start end time logics.
fun PlannedActivity.toActivity(): Activity = Activity.fromDuration(
    location = location ?: StandardLocation.LOCATIONUNKNOWN,
    startTime = startTime,
    duration = duration,
    type = activityType,
    earliestStartTime = startTime.truncateDays(),
    latestEndTime = startTime.truncateDays() + 1.days,

)
