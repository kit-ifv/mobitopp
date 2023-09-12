package utils.units

import kotlin.math.roundToInt
import kotlin.math.roundToLong



/**
 * Some units are not reasonably representable with an Integer or Long variable, since floating point arithmetics
 * support calculations with infinities no special shenanigans are needed to handle overflows
 */
interface FloatUnit<F: FloatUnitScale> {
    val rawValue: Double
    fun toDouble(unit: F): Double {
        return rawValue / unit.scale
    }

    fun toLong(unit: F): Long {
        return (rawValue / unit.scale).roundToLong()
    }

    fun toInt(unit: F): Int {
        return (rawValue / unit.scale).roundToInt()
    }

}

interface FloatUnitScale {
    val scale: Double
}