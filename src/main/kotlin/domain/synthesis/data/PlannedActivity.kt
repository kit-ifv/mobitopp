package domain.synthesis.data

import Mutable
import domain.shared.enums.ActivityType
import domain.shared.location.Location
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

    override fun toString(): String {
        return "${activityType.description.first()}(${activityType.code}) start=$startTime duration=$duration"
    }
}
