package synthesis.activityGeneration

import datastructure.Activity
import domain.enums.ActivityType
import domain.location.LOCATIONUNKNOWN
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.time.Duration

fun Activity.Companion.fromTimes(start: AbsoluteTime, end: AbsoluteTime, type: ActivityType): Activity {
    require(
        start <= end
    ) { "Cannot create activity where start time is larger than end time: [start=$start , end=$end]" }

    return fromDuration(LOCATIONUNKNOWN, start, end - start, type)
}

fun Activity.Companion.fromTimes(start: Duration, end: Duration, type: ActivityType): Activity {
    return fromTimes(start.sinceStart, end.sinceStart, type)
}
