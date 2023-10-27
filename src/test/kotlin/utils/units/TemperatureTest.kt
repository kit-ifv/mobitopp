package utils.units


import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TemperatureTest : GenericUnitTest<TemperatureUnit, Temperature>(
    TemperatureUnit.values(),
    Int::toTemperature,
    Long::toTemperature,
    Double::toTemperature
) {
    @Test
    fun absoluteZero() {
        val absZero = 0.toTemperature(TemperatureUnit.KELVIN)
        assertTrue((-459.67).toTemperature(TemperatureUnit.FAHRENHEIT).rawValue < 1000)
        assertEquals(absZero, (-273.15).toTemperature(TemperatureUnit.CELSIUS))

    }

    @Test
    fun lukewarmBody() {
        val temperature = 100.toTemperature(TemperatureUnit.FAHRENHEIT)

        val kelvin = 310.928.toTemperature(TemperatureUnit.KELVIN)
        val celsius = 37.7778.toTemperature(TemperatureUnit.CELSIUS)
        assertTrue((temperature - kelvin).rawValue < 1000)
        assertTrue((temperature - celsius).rawValue < 1000)
    }

    @Test
    fun freezingPointOfBrine() {
        val celsius = (-17.7778).toTemperature(TemperatureUnit.CELSIUS)
        val temperature = 0.toTemperature(TemperatureUnit.FAHRENHEIT)
        val kelvin = 255.372.toTemperature(TemperatureUnit.KELVIN)
        assertTrue((temperature - kelvin).rawValue < 1000)
        assertTrue((temperature - celsius).rawValue < 1000)
    }

    @Test
    fun meltingPointOfWater() {
        val temperature = 100.toTemperature(TemperatureUnit.CELSIUS)
        val kelvin = 373.15.toTemperature(TemperatureUnit.KELVIN)
        val celsius = 212.toTemperature(TemperatureUnit.FAHRENHEIT)
        assertTrue((temperature - kelvin).rawValue < 1000)
        assertTrue((temperature - celsius).rawValue < 1000)
    }

    @Test
    fun addition() {
        val t = 1.toTemperature(TemperatureUnit.KELVIN)
        assertEquals(2, (t + t).toLong(TemperatureUnit.KELVIN))
    }
}
