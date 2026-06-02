package utils.units

import domain.shared.location.JTSDistanceCalculator
import domain.shared.location.PointCreator
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.kilometers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GPSLegacyCoordinateTest {
    private fun Distance.fuzzyEquals(d: Distance): Boolean = (this - d) < 1.kilometers

    @Test
    fun distance() {
        val pos1 = PointCreator.createWGS(0.0, 0.0)
        val pos2 = PointCreator.createWGS(1.0, 0.0)
        val d = JTSDistanceCalculator.distance(pos1, pos2)

        assertTrue(111.19.kilometers.fuzzyEquals(d))
    }

    @Test
    fun distance2() {
        val pos1 = PointCreator.createWGS(50, 5)
        val pos2 = PointCreator.createWGS(51, 6)
        val d = JTSDistanceCalculator.distance(pos1, pos2)

        assertTrue(131.78.kilometers.fuzzyEquals(d))
    }
}
