package utils.units

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GPSPositionTest {

    @Test
    fun distance() {
        val pos1 = GPSPosition.degrees(0.0, 0.0)
        val pos2 = GPSPosition.degrees(1.0, 0.0)
        val d = pos1.distance(pos2)


        assertTrue(111.19.kilometers.fuzzyEquals(d, DistanceUnit.KILOMETERS))
    }

    @Test
    fun distance2() {
        val pos1 = GPSPosition.degrees(50.0, 5.0)
        val pos2 = GPSPosition.degrees(51.0, 6.0)
        val d = pos1.distance(pos2)


        assertTrue(131.78.kilometers.fuzzyEquals(d, DistanceUnit.KILOMETERS))
    }
}
