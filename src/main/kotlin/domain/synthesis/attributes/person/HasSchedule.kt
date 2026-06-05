package domain.synthesis.attributes.person

import domain.shared.data.activity.ActivityId
import domain.shared.datastructure.schedule.Schedule
import utils.Identifiable

interface HasSchedule {
    val schedule: Schedule
}

interface HasMutableSchedule : HasSchedule {
    override var schedule: Schedule
}

interface HasPlannedActivities<A : Identifiable<ActivityId>> {
    val plannedActivities: MutableList<A>
}
