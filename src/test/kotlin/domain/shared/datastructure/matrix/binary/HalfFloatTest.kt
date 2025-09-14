package domain.shared.datastructure.matrix.binary

import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

const val overflowVal = Double.POSITIVE_INFINITY
const val underflowVal = -1.0

class HalfFloatTest {
    @Test
    fun flowTests() {
        assertEquals(overflowVal, 8000000.0.toFromHF())
        assertEquals(underflowVal, (-0.0001).toFromHF())
    }

    @Test
    fun equalityTests() {
        assertEquals(Double.NaN, Double.NaN.toFromHF())
        assertEquals(0.0, 0.0.toFromHF())
        assertEquals(65504.0, 65504.0.toFromHF())
        assertEquals(56.0, 56.0.toFromHF())
    }

    @Test
    fun edgeTests() {
        assertEquals(0.0, 0.000000001.toFromHF())
        assertEquals()
    }

    @Test
    fun threeDigitPrecisionTests() {
        // we have roughly log_10(2^12) = 3,6 decimal places of precision.
        val t = 65504.1.toFromHF()
        assert( t in 65450.0.rangeTo(65550.0) )
        for (k in (-6 until 3)) {
            for(i in (100 until 1000)) {
                val testVal = i * 10.0.pow(k)
                val transformedVal = testVal.toFromHF()
                // +-5 on the forth decimal place
                val expectedRange = (testVal - (5 * 10.0.pow(k - 1))).rangeTo(testVal + (5* 10.0.pow(k - 1)))
                assert(transformedVal in expectedRange)
            }
        }
    }
}

/**
 * helper program for printing some transformed values.
 */
fun main() {
    println("| bits | double value | double bits | ")
    println("| --- | --- | --- |")
    for(i in (UShort.MIN_VALUE.toInt() until UShort.MAX_VALUE.toInt() + 1).step(128)) {
        val short = i.toUShort()
        val transformedResult = short.toShort().fromHalfFloat()
        println("| ${short.toString(2)}| $transformedResult | ${transformedResult.toBits().toString(2)} |")
    }
}

val matrixFormat = MatrixHalfFloatFormat(overflowVal, underflowVal)
fun Short.fromHalfFloat(): Double {
    val t = this
    return matrixFormat.run { t.fromHalfFloat() }
}

fun Double.toHalfFloat(): Short {
    val t = this
    return matrixFormat.run { t.toHalfFloat() }
}

fun Double.toFromHF(): Double {
    return this.toHalfFloat().fromHalfFloat()
}