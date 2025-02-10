package datastructure

import OTHER
import START
import THIRD
import datastructure.plans.BlockModel
import org.junit.jupiter.api.Test
import usecases.LegacyActivityType
import utils.units.sinceStart
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ReschedulingSkipTillHomeTest {

    private val model = BlockModel()
    private val a1 =
        Activity.fromDuration(
            START,
            0.hours.sinceStart,
            8.hours,
            earliestStartTime = 0.hours.sinceStart,
            latestEndTime = 10.hours.sinceStart
        )
    private val l1 = Leg.fromDuration(8.hours.sinceStart, 30.minutes, START, OTHER)

    private val a2 =
        Activity.fromDuration(
            OTHER,
            10.hours.sinceStart,
            8.hours,
            earliestStartTime = 8.hours.sinceStart,
            latestEndTime = 20.hours.sinceStart
        )

    private val l2 = Leg.fromDuration(18.5.hours.sinceStart, 1.5.hours, OTHER, THIRD)
    private val a3 = Activity.fromDuration(
        THIRD,
        21.hours.sinceStart,
        1.hours,
        earliestStartTime = 8.hours.sinceStart,
        latestEndTime = 24.hours.sinceStart
    ) // This is the activity that should end up late
    private val l3 = Leg.fromDuration(22.hours.sinceStart, 1.5.hours, THIRD, START)

    private val a4 =
        Activity.fromDuration(
            START,
            23.5.hours.sinceStart,
            8.hours,
            earliestStartTime = 18.hours.sinceStart,
            latestEndTime = 3.hours.sinceStart
        )

    @BeforeTest
    fun setup() {
        a1.type = LegacyActivityType.HOME
        a4.type = LegacyActivityType.HOME
        model.clear()
        model.add(a1)
        model.add(l1)
        model.add(a2)
        model.add(l2)
        model.add(a3)
        model.add(l3)
        model.add(a4)
    }
}
