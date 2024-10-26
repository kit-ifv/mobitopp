package domain.data

import Buildable
import datastructure.Activity
import datastructure.ActivityBlock
import datastructure.LinkTrip
import datastructure.LinkedActivity
import datastructure.LinkedLeg
import datastructure.LinkedTrip
import datastructure.Schedule
import datastructure.isConsistent
import datastructure.plans.BlockModel
import datastructure.plans.IDispatcher
import domain.enums.ActivityType
import domain.enums.MODEUNKOWN
import domain.location.LOCATIONUNKNOWN
import utils.ID
import utils.Identifiable
import utils.units.AbsoluteTime
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration
// TODO is it really sensible to assign the household id to the activity??
typealias ActivityId = ID<Household>

@Suppress("LongParameterList")
@Buildable
class PlannedActivity(
    override val id: ActivityId,
    val person: Person,
    val activityType: ActivityType,
    val observedTripDuration: Duration,
    val startTime: AbsoluteTime,
    val duration: Duration,
    val random: Random,
) : Identifiable<ActivityId> {

    init {
//        person.addActivity(this)
    }

    val endTime: AbsoluteTime
        get() = startTime + duration

    fun toActivity(): Activity {
        return Activity.fromDuration(
            LOCATIONUNKNOWN,
            startTime = startTime,
            duration = duration,
            type = activityType
        )
    }
}

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
