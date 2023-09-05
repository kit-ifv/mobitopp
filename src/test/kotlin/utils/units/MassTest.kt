package utils.units

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class MassTest {
    @Test
    fun reasonableNumbers() {
        val massFromDouble = 10.0.toMass(MassUnit.GRAM)
        val massFromLong = (10L).toMass(MassUnit.GRAM)
        val massFromInt = (10).toMass(MassUnit.GRAM)
        assertEquals(massFromDouble, massFromLong)
        assertEquals(massFromLong, massFromInt)
        val kilo = 100 * massFromDouble
        assertEquals(1, kilo.toInt(MassUnit.KILOGRAM))

    }
    @Test
    fun unreasonableNumbers() {
        val massFromLong = (Long.MAX_VALUE / 2).toMass(MassUnit.KILOGRAM)
        assertEquals(massFromLong, Mass(Long.MAX_VALUE))
    }
    @Test
    fun conversions() {
        val mass = (1000L).toMass(MassUnit.GRAM)
        assertEquals(0.001, mass.toDouble(MassUnit.TON))
        assertEquals(1.0, mass.toDouble(MassUnit.KILOGRAM))
        assertEquals(1000000.0, mass.toDouble(MassUnit.MILLIGRAM))

        assertEquals(1000, mass.toInt(MassUnit.GRAM))
        assertEquals(1000, mass.toLong(MassUnit.GRAM))

    }
    @Test
    fun additionTest() {
        val mass = (1000L).toMass(MassUnit.GRAM)
        assertEquals(2.kilograms, mass + mass)
    }
    @Test
    fun multiplicationTest() {
        val mass = (1000L).toMass(MassUnit.GRAM)
        assertEquals(3, (mass * 3).toLong(MassUnit.KILOGRAM))
        assertEquals(3, (3 * mass).toLong(MassUnit.KILOGRAM))
    }
    @Test
    fun convenienceConstructors() {
        assertEquals(10.kilograms, (10).toMass(MassUnit.KILOGRAM))
        assertEquals(3.grams, (3).toMass(MassUnit.GRAM))
    }
}