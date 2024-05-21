package datastructure

import OTHER
import START
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class RawLegTest {

    @Test
    fun changingTimeChangesDuration() {
        val leg = Leg.fromDuration(0.hours, 2.hours, START, OTHER)
        assertEquals(leg.duration, 2.hours)
        leg.startTime = 1.hours
        assertEquals(leg.duration, 1.hours)
    }
}
