package datastructure

import kotlin.time.Duration

fun interface ScheduleModifier {
    fun modify(planView: SeparablePlanModel, currentTime: Duration)
}

class Person(var location: Location)

class SkipToNextHomeActivity() : ScheduleModifier {
    override fun modify(plan: SeparablePlanModel, currentTime: Duration) {
        if(plan.actions().hasExceedings()) {
            require(plan.actions().isConsistent())
            val nextHomeActivity = plan.activities().dropWhile { it.endTime < currentTime }.first { it.type == ActivityType.HOME }
            plan.dropUntil(nextHomeActivity)
            nextHomeActivity.startTime = currentTime
        }


    }
}