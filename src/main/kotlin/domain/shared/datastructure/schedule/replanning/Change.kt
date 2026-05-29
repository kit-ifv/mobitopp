package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.StationaryAction
import utils.units.AbsoluteTime
import kotlin.time.Duration

data class Change(var startTime: AbsoluteTime, var duration: Duration) {
    val endTime get() = startTime + duration
}

fun StationaryAction.toChange(): Change = Change(startTime, duration)
