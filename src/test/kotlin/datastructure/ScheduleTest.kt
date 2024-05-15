package datastructure

import OTHER
import START
import THIRD
import datastructure.plans.BlockModel
import datastructure.plans.addAll
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ScheduleTest {
    private lateinit var schedule: Schedule

    private val a1 =
        Activity.fromDuration(START, 0.hours, 8.hours, earliestStartTime = 0.hours, latestEndTime = 10.hours)
    private val l1 = Leg.fromDuration(8.hours, 30.minutes, START, OTHER)

    private val a2 =
        Activity.fromDuration(OTHER, 10.hours, 8.hours, earliestStartTime = 8.hours, latestEndTime = 20.hours)

    private val l2 = Leg.fromDuration(18.5.hours, 1.5.hours, OTHER, THIRD)
    private val a3 = Activity.fromDuration(
        THIRD,
        21.hours,
        1.hours,
        earliestStartTime = 8.hours,
        latestEndTime = 24.hours
    ) // This is the activity that should end up late
    private val l3 = Leg.fromDuration(22.hours, 1.5.hours, THIRD, START)

    private val a4 =
        Activity.fromDuration(START, 23.5.hours, 8.hours, earliestStartTime = 18.hours, latestEndTime = 7.hours)

    @BeforeTest
    fun setup() {
        schedule = Schedule(BlockModel())
        a4.type = ActivityType.HOME
        schedule
    }

    @Test
    fun skippingWorksProperly() {
        schedule.addAll(a1, a2, a3, a4)
        schedule.addAll(l1, l2, l3)
        val holder = schedule.tripView()

        repeat(7) { schedule.step() }
        schedule.present?.endTime = 24.5.hours
        assertTrue(schedule.actions().hasTimeBoundViolations())
        schedule.applyAt(24.5.hours, SkipToNextHomeActivity)
    }
}
