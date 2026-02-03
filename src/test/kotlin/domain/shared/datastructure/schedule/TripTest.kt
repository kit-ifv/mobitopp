package domain.shared.datastructure.schedule

import OTHER
import START
import THIRD
import domain.shared.datastructure.schedule.replanning.SHIFT
import utils.units.sinceStart
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TripTest {

    private val firstActivity = Activity.Companion.fromDuration(START, 0.hours.sinceStart, 8.hours)
    private val secondActivity = Activity.Companion.fromDuration(OTHER, 9.hours.sinceStart, 1.hours)

    @Test
    fun runBuilder() {
        val builder = TripBuilder(firstActivity, secondActivity, emptyList(), SHIFT)
        builder.apply {
            +Step(THIRD, 10.minutes)
            +Pause(10.minutes)
            +Step(OTHER, 10.minutes)
        }
        val trip = builder.output()
        assertContentEquals(
            trip,
            listOf(
                Leg.Companion.fromDuration(firstActivity.endTime, 10.minutes, START, THIRD),
                Leg.Companion.fromDuration(firstActivity.endTime + 20.minutes, 10.minutes, THIRD, OTHER)
            )
        )
        assertTrue(trip.isConsistent())
    }
}
