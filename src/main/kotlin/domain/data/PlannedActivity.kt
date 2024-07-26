package domain.data

import Buildable
import domain.enums.ActivityType
import utils.ID
import utils.Identifiable
import utils.units.AbsoluteTime
import kotlin.random.Random
import kotlin.time.Duration

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
        person.addActivity(this)
    }

    val endTime: AbsoluteTime
        get() = startTime + duration
}
