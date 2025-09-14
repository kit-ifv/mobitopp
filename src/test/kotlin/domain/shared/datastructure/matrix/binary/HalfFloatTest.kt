package domain.shared.datastructure.matrix.binary

import kotlin.test.Test
import kotlin.test.assertEquals

const val overflowVal = Double.POSITIVE_INFINITY
const val underflowVal = -1.0

class HalfFloatTest {
    @Test
    fun flowTests() {
        assertEquals(overflowVal, 8000000.0.toHalfFloat().fromHalfFloat())
        assertEquals(underflowVal, (-0.0001).toHalfFloat().fromHalfFloat())
    }

    @Test
    fun equalityTests() {
        assertEquals(Double.NaN, Double.NaN.toHalfFloat().fromHalfFloat())
        assertEquals(0.0, 0.0.toHalfFloat().fromHalfFloat())
        assertEquals(0.0, 0.000000001.toHalfFloat().fromHalfFloat())
        assertEquals(65504.0, 65504.0.toHalfFloat().fromHalfFloat())
        val t = 65504.1.toHalfFloat().fromHalfFloat()
        assert( t in 65450.0.rangeTo(65550.0) ) // we have roughly log_10(2^12) = 3,6 decimal places of precision.
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