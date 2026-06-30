package edu.kit.ifv.domain.shared.datastructure.schedule.modifier
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.utils.units.AbsoluteTime

// This is future work for rescheduling
fun interface ScheduleModifier {
    fun applyTo(schedule: Schedule, currentTime: AbsoluteTime)
}

fun Schedule.applyAt(currentTime: AbsoluteTime, functor: ScheduleModifier) {
    functor.applyTo(this, currentTime)
}
