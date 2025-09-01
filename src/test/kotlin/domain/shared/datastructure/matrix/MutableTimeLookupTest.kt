package domain.shared.datastructure.matrix

import org.junit.jupiter.api.Assertions.assertEquals
import utils.units.sinceStart
import utils.units.toAbsoluteHours
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MutableTimeLookupTest {

    @Test
    fun correctConstruction() {
        val timeLookupBuilder = DayLookupBuilder<Int>(modulus = 1.days)
        timeLookupBuilder[0, 4] = 1
        timeLookupBuilder[1, 3] = 2

        val output = timeLookupBuilder.build()
        assertEquals(output[0.hours], 1)
        assertEquals(output[(-1).hours], 1)
        assertEquals(output[1.hours], 2)
        assertEquals(output[3.hours], 1)
        timeLookupBuilder.segments()

    }

    @Test
    fun moduloLooping() {
        val dayLookup = DayTimeLookupBuilder<Int>()
        dayLookup[0.minutes, 30.minutes] = 1
        dayLookup[30.minutes, 99999999.minutes] = 2
        val output = dayLookup.build()
        assertEquals(1, output[1.days + 15.minutes])
        assertEquals(1, output[0.minutes])
        assertEquals(1, output[15.minutes])

        assertEquals(2, output[1.days + 30.minutes])
        assertEquals(2, output[30.minutes])
    }

    @Test
    fun negativeTimes() {
        val dayLookup = DayTimeLookupBuilder<Int>()
        dayLookup[0.minutes, 30.minutes] = 1
        dayLookup[30.minutes, 99999999.minutes] = 2
        val output = dayLookup.build()
        assertEquals(2, output[(-1).hours])

    }

    @Test
    fun priorityOverrides() {
        val dayLookup = DayLookupBuilder<Int>()
        dayLookup.setDefault(15 to -5) // Extremly low priority

        dayLookup[0.minutes, 120.minutes] = 42 to 5 // High priority
        dayLookup[30.minutes, 150.minutes] = 9001 to 3
        val output = dayLookup.build()
        assertEquals(output[1.minutes], 42)
        assertEquals(output[45.minutes], 42)
        assertEquals(output[120.minutes], 9001)
        assertEquals(output[160.minutes], 15)
    }

    @Test
    fun directSetting() {
        val dayLookup = DayLookupBuilder<Boolean>()
        dayLookup.setDefault(false)
        dayLookup.setDuration(18.hours..<6.hours, true)
        val output = dayLookup.build()
        assertEquals(1, output.elements.size)
        assertEquals(false, output.elements.first())


    }

    private operator fun <T> TimeLookup<T>.get(number: Duration) = get(number.sinceStart)

    private operator fun  DayLookupBuilder<Int>.set(a: Number, b: Number, path: Int) = set(a.toAbsoluteHours()..<b.toAbsoluteHours(), path)

}