package domain.shared.datastructure.matrix.binary

import org.jetbrains.kotlin.types.ConstantValueKind
import org.junit.jupiter.api.Assertions.*
import utils.collections.toBinaryRepresentation
import kotlin.test.Test
import kotlin.test.assertEquals

class HalfFloatTest {
    @Test
    fun specialConversions() {
        assertEquals(Double.NaN, Double.NaN.toHalfFloat().fromHalfFloat())
        assertEquals(Double.POSITIVE_INFINITY, 8000000.0.toHalfFloat().fromHalfFloat())
        assertEquals(0.0, 0.0.toHalfFloat().fromHalfFloat())
        assertEquals(Double.NaN, -0.0001.toHalfFloat().fromHalfFloat())
        assertEquals(65504.0, 65504.0.toHalfFloat().fromHalfFloat())
        assertEquals(Double.POSITIVE_INFINITY, 65504.1.toHalfFloat().fromHalfFloat())
    }

    @Test
    fun equalityTests() {
        assertEquals(0.0, 0.0.toHalfFloat().fromHalfFloat())
        assertEquals(0.001, 0.001.toHalfFloat().fromHalfFloat())
    }
}

/**
 * helper program for printing some transformed values.
 */
fun main() {
    println("| bits | double value | double bits | ")
    println("| --- | --- | --- |")
    for(i in (UShort.MIN_VALUE.toInt() until UShort.MAX_VALUE.toInt() + 1).step(4)) {
        val short = i.toUShort()
        val transformedResult = short.toShort().fromHalfFloat()
        println("| ${short.toString(2)}| $transformedResult | ${transformedResult.toBits().toString(2)} |")
    }
}