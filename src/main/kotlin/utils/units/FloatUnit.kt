package utils.units

import kotlin.math.roundToInt
import kotlin.math.roundToLong



/**
 * Some units are not reasonably representable with an Integer or Long variable, since floating point arithmetics
 * support calculations with infinities no special shenanigans are needed to handle overflows
 */
interface FloatUnit<F: FloatUnitScale>: NumericUnit<F> {
    override val rawValue: Double

    override fun toDouble(unit: F): Double {
        return rawValue / unit.scale
    }

    override fun toLong(unit: F): Long {
        return (rawValue / unit.scale).roundToLong()
    }

    override fun toInt(unit: F): Int {
        return (rawValue / unit.scale).roundToInt()
    }

    operator fun plus(other: FloatUnit<F>): FloatUnit<F>

    operator fun minus(other: FloatUnit<F>): FloatUnit<F>

    override fun unaryMinus(): FloatUnit<F>





}

interface FloatUnitScale: NumericUnitScale {
    override val scale: Double
}
