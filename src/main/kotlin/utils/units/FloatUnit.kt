package utils.units

import kotlin.math.roundToInt
import kotlin.math.roundToLong



/**
 * Some units are not reasonably representable with an Integer or Long variable, since floating point arithmetics
 * support calculations with infinities no special shenanigans are needed to handle overflows
 */
interface FloatUnit<F: FloatUnitScale>: NumericUnit<F> {
    val rawValue: Double

    override fun toDouble(unit: F): Double {
        return rawValue / unit.scale
    }

    override fun toLong(unit: F): Long {
        return (rawValue / unit.scale).roundToLong()
    }

    override fun toInt(unit: F): Int {
        return (rawValue / unit.scale).roundToInt()
    }



}

interface NumericUnit<N: NumericUnitScale> {
    fun toDouble(unit: N): Double
    fun toLong(unit: N): Long
    fun toInt(unit: N): Int
}
interface NumericUnitScale {
    val scale: Number
}
interface FloatUnitScale: NumericUnitScale {
    override val scale: Double
}
