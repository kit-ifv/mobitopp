package edu.kit.ifv.domain.shared.datastructure.schedule.replanning
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.time.Duration

data class Change(var startTime: AbsoluteTime, var duration: Duration) {
    val endTime get() = startTime + duration
}

fun StationaryAction.toChange(): Change = Change(startTime, duration)
