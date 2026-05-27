package domain.shared.datastructure.matrix.binary

import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

const val TEST_OVERFLOW_VAL = Double.POSITIVE_INFINITY
const val TEST_UNDERFLOW_VAL = -1.0

class HalfFloatTest {

    @Test
    fun sanityTests() {
        assertEquals(Double.NaN, Double.NaN.toFromHF())
        assertEquals(0.0, 0.0.toFromHF())
        assertEquals(65504.0, 65504.0.toFromHF())
        assertEquals(56.0, 56.0.toFromHF())
        assertEquals(6.0, 6.0.toFromHF())
        assertEquals(12036.0, 12036.0.toFromHF())
        // due to only having 3.5 digits of precision 12036.0 works, because it can be represented nicely with binary,
        // 12037.0 is not convertible back to the original val.
        assertNotEquals(12037.0, 12037.0.toFromHF())
        // those values are rounded down to the next representable value
        assertEquals(12036.0, 12037.0.toFromHF())
        assertEquals(240000.0, 240044.0.toFromHF())
    }

    @Test
    fun edgeTests() {
        val hfMinPositiveVal = 0x0001.toShort().fromHalfFloat()
        val hfMaxPositiveVal = 0xFFFC.toShort().fromHalfFloat()
        val hfOneUnderMax = 0xFFFB.toShort().fromHalfFloat()
        val hfOneOverMin = 0x0002.toShort().fromHalfFloat()

        assertEquals(TEST_UNDERFLOW_VAL, Double.NEGATIVE_INFINITY.toFromHF())
        assertEquals(TEST_UNDERFLOW_VAL, (-Double.MAX_VALUE).toFromHF())
        assertEquals(TEST_UNDERFLOW_VAL, (-(10.0.pow(20))).toFromHF())
        assertEquals(TEST_UNDERFLOW_VAL, (0.0 - 0.0000001).toFromHF())
        assertEquals(TEST_UNDERFLOW_VAL, (-Double.MIN_VALUE).toFromHF())
        assertEquals(0.0, 0.0.toFromHF())
        assertEquals(0.0, Double.MIN_VALUE.toFromHF())
        assertEquals(0.0, 0.000000001.toFromHF())
        assertEquals(0.0, (hfMinPositiveVal - 0.00000001).toFromHF())
        assertEquals(hfMinPositiveVal, hfMinPositiveVal.toFromHF())
        assertEquals(hfMinPositiveVal, (hfMinPositiveVal + 0.00000001).toFromHF())
        assertEquals(hfMinPositiveVal, (hfOneOverMin - 0.00000001).toFromHF())
        assertEquals(hfOneOverMin, (hfMinPositiveVal + 0.00000003).toFromHF())
        assertEquals(hfOneOverMin, hfOneOverMin.toFromHF())
        assertEquals(1.0, 1.0.toFromHF())

        // binary conversion leads to rounding down, even though the value is closer to MaxPositiveVal than fhOneUnderMax
        assertEquals(hfOneUnderMax, (hfMaxPositiveVal - 0.1).toFromHF())
        assertEquals(hfOneUnderMax, (hfMaxPositiveVal - 1).toFromHF())
        assertEquals(hfMaxPositiveVal, hfMaxPositiveVal.toFromHF())
        assertEquals(TEST_OVERFLOW_VAL, (hfMaxPositiveVal + 1).toFromHF())
        assertEquals(TEST_OVERFLOW_VAL, 299999.0.toFromHF())
        assertEquals(TEST_OVERFLOW_VAL, 10.0.pow(20).toFromHF())
        assertEquals(TEST_OVERFLOW_VAL, Double.POSITIVE_INFINITY.toFromHF())
        assertEquals(TEST_OVERFLOW_VAL, Double.MAX_VALUE.toFromHF())
    }

    @Test
    fun threeDigitPrecisionTests() {
        // we have roughly log_10(2^12) = 3,6 decimal places of precision.
        val hfMaxPositiveVal = 0xFFFC.toShort().fromHalfFloat()
        for (k in (-6 until 4)) {
            for (i in (100 until 1000)) {
                val testVal = i * 10.0.pow(k)
                if (testVal >= hfMaxPositiveVal) break
                val transformedVal = testVal.toFromHF()
                // +-5 on the forth decimal place
                val expectedRange = (testVal - (5 * 10.0.pow(k - 1))).rangeTo(testVal + (5 * 10.0.pow(k - 1)))
                assert(transformedVal in expectedRange)
            }
        }
    }
}

val matrixFormat = MatrixHalfFloatFormat(TEST_OVERFLOW_VAL, TEST_UNDERFLOW_VAL)
fun Short.fromHalfFloat(): Double {
    val t = this
    return matrixFormat.run { t.fromHalfFloat() }
}

fun Double.toHalfFloat(): Short {
    val t = this
    return matrixFormat.run { t.toHalfFloat() }
}

fun Double.toFromHF(): Double = this.toHalfFloat().fromHalfFloat()

/**
 * helper program for printing half floats in some range
 */
fun main() {
    println("| bits | double value | double bits | ")
    println("| --- | --- | --- |")
    for (i in (UShort.MIN_VALUE.toInt() until UShort.MAX_VALUE.toInt() + 1).step(128)) {
        val short = i.toUShort()
        val transformedResult = short.toShort().fromHalfFloat()
        println("| ${short.toString(2)}| $transformedResult | ${transformedResult.toBits().toString(2)} |")
    }
}
