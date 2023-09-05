package utils.units


import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TemperatureTest {
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
    @Test
    fun integerConversion() {
        for (i in listOf(1, 2, 3, 42, -1337, 9001)) {
            for(unit in TemperatureUnit.values()) {
                assertEquals(i, i.toTemperature(unit).toInt(unit))
            }
        }
    }

    @Test
    fun longConversion() {
        for (i in listOf(1L, 2L, 3L, 42L, -1337L, 9001L)) {
            for(unit in TemperatureUnit.values()) {
                assertEquals(i, i.toTemperature(unit).toLong(unit))
            }
        }
    }

    @Test
    fun doubleConversion() {
        for (i in listOf(1.0, 2.0, 3.0, 42.2, -1337.1, 9001.1)) {
            for(unit in TemperatureUnit.values()) {
                assertEquals(i, i.toTemperature(unit).toDouble(unit), 0.001)
            }
        }
    }
}