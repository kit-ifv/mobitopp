package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.plans.BlockModel
import domain.shared.enums.LegacyActivityType
import domain.shared.location.Location

class ScheduleBuilderForTests(val duration: Number = 8) {
    private val HOMELOC = ActuallyUseableLocation(1)
    private val LOC2 = ActuallyUseableLocation(2)
    private val LOC3 = ActuallyUseableLocation(3)

    private val activities: MutableList<Activity> = mutableListOf()
    private val schedule = Schedule(BlockModel()).apply {
        add {
            location = HOMELOC
            startTime = 0
            duration = this@ScheduleBuilderForTests.duration
        }.also { activities.add(it) }
    }
    private var lastLocation: Location = HOMELOC
    fun Schedule.add(lambda: ActConfig.() -> Unit): Activity {
        val activity = ActConfig()
        activity.lambda()
        val activity1 = activity.toActivity()
        add(activity1)
        return activity1
    }
    private fun Location.alternate(): Location {
        return when (this) {
            HOMELOC -> LOC2
            LOC2 -> LOC3
            LOC3 -> LOC2
            else -> throw IllegalArgumentException("Location $this is not a valid location")
        }
    }

    fun activity(lambda: ActConfig.() -> Unit): Activity {
        val actCfg = ActConfig()
        actCfg.location = lastLocation.alternate()
        actCfg.lambda()

        val activity = actCfg.toActivity()
        schedule.addWithPrecedingLeg(activity)
        lastLocation = actCfg.location
        return activity.also { activities.add(it) }
    }

    fun home(lambda: ActConfig.() -> Unit): Activity {
        return activity {
            location = HOMELOC
            activityType = LegacyActivityType.HOME
            lambda()
        }
    }

    fun build(): Schedule {
        return schedule
    }

    fun actions() = activities
}