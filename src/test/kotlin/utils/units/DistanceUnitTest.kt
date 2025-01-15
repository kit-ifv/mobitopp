package utils.units
import units.DistanceUnit
import units.toDistance
import kotlin.test.Test
import kotlin.test.assertEquals

class DistanceUnitTest {

    @Test
    fun distanceInspector() {
        val d = 5.0
        val dist = d.toDistance(DistanceUnit.CENTIMETERS)
        assertEquals(50, dist.inWholeMillimeters)
        assertEquals(5, dist.inWholeCentimeters)
        assertEquals(0, dist.inWholeMeters)
        assertEquals(50.0, dist.toDouble(DistanceUnit.MILLIMETERS))
        assertEquals(5.0, dist.toDouble(DistanceUnit.CENTIMETERS))
        assertEquals(0.05, dist.toDouble(DistanceUnit.METERS))
        assertEquals(0.00005, dist.toDouble(DistanceUnit.KILOMETERS))
    }
}
