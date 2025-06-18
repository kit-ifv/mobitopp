package domain.simulation.behavior

import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.ActivityBlock
import domain.shared.datastructure.schedule.LinkTrip
import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.shared.datastructure.schedule.LinkedTrip
import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.isConsistent
import domain.shared.datastructure.schedule.plans.BlockModel
import domain.shared.datastructure.schedule.plans.IDispatcher
import domain.shared.enums.MODEUNKOWN
import domain.shared.location.LOCATIONUNKNOWN
import domain.synthesis.data.PlannedActivity

fun List<PlannedActivity>.toSchedule(dispatcher: IDispatcher): Schedule {
    val linkedActivities = map { LinkedActivity(it.toActivity()) }

    require(isNotEmpty()) { "Cannot use an empty list to generate a schedule, at least a home activity is required" }

    val filteredActivities = mutableListOf(linkedActivities.first())
    for (i in 1 until size) {
        val linkedActivity = linkedActivities[i]
        if (filteredActivities.last().endTime <= linkedActivity.startTime) filteredActivities.add(linkedActivity)
    }

    require(filteredActivities.isConsistent()) {
        "Cannot build a schedule from inconsistent data, please fix"
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
            filteredActivities.toSortedSet()
        )
    return Schedule(targetModel)
}

fun PlannedActivity.toActivity(): Activity {
    return Activity.Companion.fromDuration(
        location = location ?: LOCATIONUNKNOWN,
        startTime = startTime,
        duration = duration,
        type = activityType
    )
}
