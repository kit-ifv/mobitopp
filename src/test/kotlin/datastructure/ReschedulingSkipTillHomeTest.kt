package datastructure

import OTHER
import START
import THIRD
import datastructure.plans.BlockModel
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ReschedulingSkipTillHomeTest {

    private val model = BlockModel()
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
        Activity.fromDuration(START, 23.5.hours, 8.hours, earliestStartTime = 18.hours, latestEndTime = 3.hours)

    @BeforeTest
    fun setup() {
        a1.type = ActivityType.HOME
        a4.type = ActivityType.HOME
        model.clear()
        model.add(a1)
        model.add(l1)
        model.add(a2)
        model.add(l2)
        model.add(a3)
        model.add(l3)
        model.add(a4)
    }

    @Test
    fun reschedulingShouldWork() {
//        // Some external force causes the second leg to take forever
//        val plan = model.actions()
//        val leg2inPlan = plan[3] as LinkedLeg
//        leg2inPlan.endTime = 23.5.hours
//
//        SkipToNextHomeActivity().modify(model, 23.5.hours)
//
//        assertTrue(model.actions().isConsistent())
//        assertEquals(plan.size, 6)
    }
}
