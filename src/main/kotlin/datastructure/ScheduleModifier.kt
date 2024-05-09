package datastructure

import kotlin.time.Duration

fun interface ScheduleModifier {
    fun modify(planView: SeparablePlanModel, currentTime: Duration)
}

class Person(var location: Location)

class SkipToNextHomeActivity : ScheduleModifier {
    override fun modify(planView: SeparablePlanModel, currentTime: Duration) {
        if (planView.actions().hasExceedings()) {
            require(planView.actions().isConsistent())
            val nextHomeActivity =
                planView.activities().dropWhile { it.endTime < currentTime }.first { it.type == ActivityType.HOME }
            planView.dropUntil(nextHomeActivity)
            nextHomeActivity.startTime = currentTime
        }
    }
}
