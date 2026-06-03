package domain.shared.datastructure.schedule

import OTHER
import START
import domain.shared.datastructure.schedule.action.Leg
import utils.units.sinceStart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class RawLegTest {

    @Test
    fun changingTimeChangesDuration() {
        val leg = Leg.fromDuration(0.hours.sinceStart, 2.hours, START, OTHER)
        assertEquals(leg.duration, 2.hours)
        leg.startTime = 1.hours.sinceStart
        assertEquals(leg.duration, 1.hours)
    }
}
