package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.plans.BlockModel
import domain.shared.enums.LegacyActivityType
import domain.shared.location.LocationOld
import domain.shared.location.StandardLocation

class ScheduleBuilderForTests(val duration: Number = 8) {


    private val homeLoc = StandardLocation.fromID(1)
    private val loc2 = StandardLocation.fromID(2)
    private val loc3 = StandardLocation.fromID(3)

    private val activities: MutableList<Activity> = mutableListOf()
    private val schedule = Schedule(BlockModel()).apply {
        add {
            location = homeLoc
            startTime = 0
            duration = this@ScheduleBuilderForTests.duration
        }.also { activities.add(it) }
    }
    private var lastLocation: StandardLocation = homeLoc
    fun Schedule.add(lambda: ActConfig.() -> Unit): Activity {
        val activity = ActConfig()
        activity.lambda()
        val activity1 = activity.toActivity()
        add(activity1)
        return activity1
    }
    private fun StandardLocation.alternate(): StandardLocation {
        return when (this) {
            homeLoc -> loc2
            loc2 -> loc3
            loc3 -> loc2
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
            location = homeLoc
            activityType = LegacyActivityType.HOME
            lambda()
        }
    }

    fun build(): Schedule {
        return schedule
    }

    fun actions() = activities
}
