package utils.units

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.test.assertFails

class DistanceTest {

    @Test
    fun compareTo() {

        val d1 = Distance.ofMeters(1)
        val d2 = Distance.ofMeters(2)


        val d3 = Distance.ofKilometers(1.0)
        assertTrue(d1 < d2)
        assertTrue(d2 > d1)
        assertTrue(d1 < d3)
        assertTrue(d2 < d3)
    }
    @Test
    fun addition() {
        val d1 = Distance.ofMeters(1)
        val d2 = Distance.ofMeters(2)

        assertEquals(Distance.ofMeters(3), d1 + d2)

    }

    @Test
    fun multiplicationTest() {
        val distance = (1).toDistance(DistanceUnit.atomic())
        val result = distance * 3
        assertEquals(result.toLong(DistanceUnit.atomic()), 3)
        assertEquals(distance * 4.0, (4).toDistance(DistanceUnit.atomic()))
    }
    @Test
    fun divisionTest() {
        val distance = (1).toDistance(DistanceUnit.METERS)
        assertEquals(distance / 2, (500).toDistance(DistanceUnit.MILLIMETERS))
    }

}