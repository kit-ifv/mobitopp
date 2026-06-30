package edu.kit.ifv.domain.synthesis.attributes.person
import edu.kit.ifv.domain.shared.data.activity.ActivityId
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.utils.Identifiable

interface HasSchedule {
    val schedule: Schedule
}

interface HasMutableSchedule : HasSchedule {
    override var schedule: Schedule
}

interface HasPlannedActivities<A : Identifiable<ActivityId>> {
    val plannedActivities: MutableList<A>
}
