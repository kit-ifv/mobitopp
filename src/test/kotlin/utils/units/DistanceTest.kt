package utils.units

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DistanceTest : GenericUnitTest<DistanceUnit, Distance>(
    DistanceUnit.values(),
    Int::toDistance,
    Long::toDistance,
    Double::toDistance
) {

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

    @Test
    fun convertingCommaToHighUnit() {
        val test = 42.2.toDistance(DistanceUnit.KILOMETERS)
        val result = test.toDouble(DistanceUnit.KILOMETERS)
        println(result)
    }

    @Test
    fun convenienceConstructors() {
        assertEquals(1L.kilometers, 1.0.kilometers)
        assertEquals(1.kilometers, 1L.kilometers)
        assertEquals(1.0.meters, 1L.meters)
        assertEquals(1.meters, 1L.meters)
        assertEquals(1.kilometers, Distance.ofKilometers(1))
    }
}
