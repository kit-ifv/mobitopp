package synthesis.activityGeneration

import datastructure.Activity
import domain.enums.ActivityType
import utils.Decodable
import kotlin.time.Duration

data class PreliminaryActivitySchedule(private val activities: MutableList<Activity>) :
    MutableList<Activity> by activities {

    companion object {

        val STAY_AT_HOME = PreliminaryActivitySchedule(mutableListOf())

        operator fun invoke(
            decoder: Decodable<ActivityType>,
            lambda: ScheduleBuilder.() -> Unit
        ): PreliminaryActivitySchedule {
            val builder = ScheduleBuilder(decoder)
            builder.lambda()
            return builder.build()
        }
    }

    override fun toString(): String {
        return activities.toString()
    }

    class ScheduleBuilder(private val decoder: Decodable<ActivityType>) {
        val activities: MutableList<Activity> = mutableListOf()

        fun home(start: Duration, end: Duration) {
            extracted(start, end, "HOME")
        }

        fun work(start: Duration, end: Duration) {
            extracted(start, end, "WORK")
        }

        fun education(start: Duration, end: Duration) {
            extracted(start, end, "EDUCATION")
        }

        fun shopping(start: Duration, end: Duration) {
            extracted(start, end, "SHOPPING")
        }

        fun leisure(start: Duration, end: Duration) {
            extracted(start, end, "LEISURE")
        }

        private fun extracted(start: Duration, end: Duration, type: String) {
            activities.add(Activity.fromTimes(start, end, decoder.decode(type)))
        }

        fun build(): PreliminaryActivitySchedule {
            return PreliminaryActivitySchedule(activities)
        }
    }
}
