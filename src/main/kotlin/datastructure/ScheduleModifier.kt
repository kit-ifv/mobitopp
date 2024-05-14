package datastructure

import utils.collections.iterate
import kotlin.time.Duration

fun interface ScheduleModifier {
    fun modify(schedule: Schedule, currentTime: Duration)
}

object SkipToNextHomeActivity : ScheduleModifier {
    override fun modify(schedule: Schedule, currentTime: Duration) {
        require(schedule.present != null)
        require(schedule.present!!.type == ActionType.ACTIVITY)
        if (schedule.present.iterate(schedule.future).hasTimeBoundViolations()) {
            val nextHomeActivity =
                schedule.activities().dropWhile { it.endTime < currentTime }.first { it.type == ActivityType.HOME }
            schedule.dropUntil(nextHomeActivity)
//            nextHomeActivity.previous = schedule.present!!
//            nextHomeActivity.shiftStartTo(max(nextHomeActivity.previous?.endTime, currentTime))
//            schedule.present?.let {
//                if(it.actionType == ActionType.LEG) {
//                    it.endLocation = nextHomeActivity.location
//                } else {
//                    schedule.add(Leg.fromEndTime(it.endTime, nextHomeActivity.startTime))
//                }
//            }
//            val lastAction = schedule.lastAction()
//            when(lastAction.actionType) {
//                ActionType.LEG -> {
//                    if(lastAction.endLocation != nextHomeActivity.location) {
//                        schedule.add(Leg.fromDuration(lastAction.endTime, ))
//                    }
//                }
//                ActionType.ACTIVITY -> 1
//            }
        }
    }
    private fun redirectCurrentLeg(to: Location) {

    }
}
