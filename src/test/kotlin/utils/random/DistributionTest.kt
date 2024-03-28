package utils.random

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

const val FIRST = 1
const val SECOND = 2
const val THIRD = 3
const val FOURTH = 4

class DistributionTest {

    var data: SortedMap<Int, Double> = TreeMap<Int, Double>().also {
        it[FIRST] = 0.25
        it[SECOND] = 0.25
        it[THIRD] = 0.25
        it[FOURTH] = 0.25
    }

    var data2: SortedMap<Int, Double> = TreeMap<Int, Double>().also {
        it[FIRST] = 1.25
        it[SECOND] = 1.25
        it[THIRD] = 1.25
        it[FOURTH] = 1.25
    }

    var randomVariable: Histogram<Int> = Histogram<Int>("test hist", data)

    @Test
    fun testRealization() {
        assertEquals(FIRST, randomVariable.drawValue(0.1))
        assertEquals(SECOND, randomVariable.drawValue(0.3))
        assertEquals(THIRD, randomVariable.drawValue(0.6))
        assertEquals(FOURTH, randomVariable.drawValue(0.8))
    }

    @Test
    fun testReproducibility() {
        assertEquals(FIRST, randomVariable.drawValue(0.1))
        assertEquals(FIRST, randomVariable.drawValue(0.1))
        assertEquals(FIRST, randomVariable.drawValue(0.1))
        assertEquals(FIRST, randomVariable.drawValue(0.1))
        assertEquals(FIRST, randomVariable.drawValue(0.1))
    }

    @Test
    fun testCornerCases() {
        assertTrue(0.25 - Math.ulp(0.25) < 0.25)
        assertEquals(FIRST, randomVariable.drawValue(0.0))
        assertEquals(FIRST, randomVariable.drawValue(0.25 - Math.ulp(0.25)))
        assertEquals(SECOND, randomVariable.drawValue(0.25))
        assertEquals(SECOND, randomVariable.drawValue(0.5 - Math.ulp(0.5)))
        assertEquals(THIRD, randomVariable.drawValue(0.5))
        assertEquals(THIRD, randomVariable.drawValue(0.75 - Math.ulp(0.75)))
        assertEquals(FOURTH, randomVariable.drawValue(0.75))
        assertEquals(FOURTH, randomVariable.drawValue(1.0))
    }

    @Test
    fun testZeroProbability() {
        val myData = TreeMap<Int, Double>()
        myData[FIRST] = 0.5
        myData[SECOND] = 0.0
        myData[THIRD] = 0.5
        val rv: Histogram<Int> = Histogram<Int>("my data", myData)
        assertEquals(FIRST, rv.drawValue(0.1))
        assertEquals(FIRST, rv.drawValue(0.49))
        assertEquals(FIRST, rv.drawValue(0.5 - Math.ulp(0.5)))
        assertEquals(THIRD, rv.drawValue(0.5))
        assertEquals(THIRD, rv.drawValue(0.9))
    }
}
