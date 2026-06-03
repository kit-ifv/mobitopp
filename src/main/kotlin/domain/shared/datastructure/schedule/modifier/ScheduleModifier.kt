package domain.shared.datastructure.schedule.modifier

import domain.shared.datastructure.schedule.Schedule
import utils.units.AbsoluteTime

// This is future work for rescheduling
fun interface ScheduleModifier {
    fun applyTo(schedule: Schedule, currentTime: AbsoluteTime)
}

fun Schedule.applyAt(currentTime: AbsoluteTime, functor: ScheduleModifier) {
    functor.applyTo(this, currentTime)
}
