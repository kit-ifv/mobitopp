package edu.kit.ifv.utils.units
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class AbsoluteTimeTest {

    @Test
    fun toStringTest() {
        var time = Time.START
        assertEquals("0s", time.toString())
        time += 1.seconds
        assertEquals("1s", time.toString())
        time += 1.minutes
        assertEquals("1m 1s", time.toString())
        time += 1.hours
        assertEquals("1h 1m 1s", time.toString())
        time += 1.days
        assertEquals("1d 1h 1m 1s", time.toString())

        time += 61.seconds
        assertEquals("1d 1h 2m 2s", time.toString())

        time += 7.days
        assertEquals("1w 1d 1h 2m 2s", time.toString())
    }

    @Test
    fun compareToTest() {
        val time1 = Time(1.days)
        val time2 = Time(2.days)

        // check for operation of < and >
        // check different time
        assertEquals(time1 > time2, false)
        assertEquals(time1 < time2, true)

        // compare same time
        assertEquals(time1 < time1, false)
        assertEquals(time1 > time1, false)

        // check operation for ==
        val time3 = Time(1.days)
        assertEquals(time1 == time3, true)
        assertEquals(time1 == time2, false)
    }
}
