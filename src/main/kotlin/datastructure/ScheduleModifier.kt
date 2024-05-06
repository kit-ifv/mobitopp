package datastructure

import kotlin.time.Duration

fun interface ScheduleModifier {
    fun modify(planView: SeparablePlanModel, currentTime: Duration)
}

class Person(var location: Location)

class SkipToNextHomeActivity(val person: Person) : ScheduleModifier {
    override fun modify(plan: SeparablePlanModel, currentTime: Duration) {

        val nextHomeActivity = plan.activities().first { it.type == ActivityType.HOME }
        plan.dropUntil(nextHomeActivity)
        nextHomeActivity.startTime = currentTime
    }
}