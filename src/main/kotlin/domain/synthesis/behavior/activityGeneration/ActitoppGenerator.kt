package domain.synthesis.behavior.activityGeneration

import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.time.Duration

fun Activity.Companion.fromTimes(start: AbsoluteTime, end: AbsoluteTime, type: ActivityType): Activity {
    require(
        start <= end,
    ) { "Cannot create activity where start time is larger than end time: [start=$start , end=$end]" }

    return fromDuration(StandardLocation.LOCATIONUNKNOWN, start, end - start, type = type)
}

fun Activity.Companion.fromTimes(start: Duration, end: Duration, type: ActivityType): Activity = fromTimes(
    start.sinceStart,
    end.sinceStart,
    type,
)
