package utils.units

import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.kilometers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GPSLegacyCoordinateTest {
    private fun Distance.fuzzyEquals(d: Distance): Boolean {
        return (this - d) < 1.kilometers
    }

    @Test
    fun distance() {
        val pos1 = Pair(0.0, 0.0).toCoordinate()
        val pos2 = Pair(1.0, 0.0).toCoordinate()
        val d = pos1.distance(pos2)

        assertTrue(111.19.kilometers.fuzzyEquals(d))
    }

    @Test
    fun distance2() {
        val pos1 = Pair(50, 5).toCoordinate()
        val pos2 = Pair(51, 6).toCoordinate()
        val d = pos1.distance(pos2)

        assertTrue(131.78.kilometers.fuzzyEquals(d))
    }
}
