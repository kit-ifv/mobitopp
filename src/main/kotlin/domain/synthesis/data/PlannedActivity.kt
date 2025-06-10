package domain.synthesis.data

import Mutable
import core.datastructure.schedule.Activity
import core.datastructure.schedule.ActivityBlock
import core.datastructure.schedule.LinkTrip
import core.datastructure.schedule.LinkedActivity
import core.datastructure.schedule.LinkedLeg
import core.datastructure.schedule.LinkedTrip
import core.datastructure.schedule.Schedule
import core.datastructure.schedule.isConsistent
import core.datastructure.schedule.plans.BlockModel
import core.datastructure.schedule.plans.IDispatcher
import core.location.LOCATIONUNKNOWN
import core.location.Location
import domain.shared.enums.ActivityType
import domain.shared.enums.MODEUNKOWN
import utils.ID
import utils.Identifiable
import utils.random.SeededActor
import utils.units.AbsoluteTime
import kotlin.time.Duration

typealias ActivityId = ID<PlannedActivity>

@Mutable
abstract class PlannedActivity(
    final override val id: ActivityId,
    val person: MutablePerson,
    seed: Long
) : SeededActor<PlannedActivity>(seed), Identifiable<ActivityId> {

    init {
        this.addAsActivity()
    }

    private fun addAsActivity() {
        this.person.plannedActivities.add(this)
    }

    abstract val activityType: ActivityType
    abstract val observedTripDuration: Duration
    abstract val startTime: AbsoluteTime
    abstract val duration: Duration
    abstract val location: Location?

    val endTime: AbsoluteTime
        get() = startTime + duration

    fun toActivity(): Activity {
        return Activity.fromDuration(
            location = location ?: LOCATIONUNKNOWN,
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
