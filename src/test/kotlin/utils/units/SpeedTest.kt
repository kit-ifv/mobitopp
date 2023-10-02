package utils.units

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class SpeedTest: GenericUnitTest<SpeedUnit, Speed>(SpeedUnit.values(), Int::toSpeed, Long::toSpeed, Double::toSpeed) {

    @Test
    fun conversion() {
        val speed = 1.toSpeed(SpeedUnit.METER_PER_SECOND)
        assertEquals(3.6, speed.toDouble(SpeedUnit.KILOMETER_PER_HOUR), 0.0001)

    }
    @Test
    fun multiplication() {
        val speed = 1.toSpeed(SpeedUnit.KILOMETER_PER_HOUR)
        val distance = 500.toDistance(DistanceUnit.METERS)
        val other = speed * 30.toDuration(DurationUnit.MINUTES)
        assertTrue(distance.fuzzyEquals(other, precision = DistanceUnit.MILLIMETERS))

    }
    @Test
    fun fromDistanceDivision() {
        val distance = 10.toDistance(DistanceUnit.METERS)
        val speed = distance / 10.toDuration(DurationUnit.SECONDS)
        assertEquals(speed, 1.toSpeed(SpeedUnit.METER_PER_SECOND))
    }
}
