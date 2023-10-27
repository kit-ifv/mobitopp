package utils.units

import kotlin.math.abs


/**
 * Provides the basic functionality for arithmetic operations and primitive type conversions for a unit that can
 * be represented by a long value. The designated inheritors of this interface should be units that do not require
 * high precision but which can cause errors by conversion errors. (Such as assuming a raw Integer as seconds
 * instead of minutes which is not inherently clear to other team members).
 *
 * Support translation (+ and -) operations
 *
 *
 * @param SCALE A unit should be supplied specifying both the type and the scaling factor to determine the raw value of
 * the unit
 */
interface LongUnit<SCALE> : NumericUnit<SCALE>, Comparable<LongUnit<SCALE>>
        where SCALE : LongUnitScale {
    override val rawValue: Long
    private val infinity: Long
        get() = Long.MAX_VALUE

    private val negInfinity: Long
        get() = -Long.MAX_VALUE


    operator fun plus(other: LongUnit<SCALE>): LongUnit<SCALE>

    operator fun minus(other: LongUnit<SCALE>): LongUnit<SCALE>

    override fun unaryMinus(): LongUnit<SCALE>

    fun isInfinite(): Boolean {
        return rawValue == infinity || rawValue == negInfinity
    }

    override fun toDouble(unit: SCALE): Double {
        return when (rawValue) {
            infinity -> Double.POSITIVE_INFINITY
            negInfinity -> Double.NEGATIVE_INFINITY
            else -> {
                convertUnit(rawValue.toDouble(), 1L, unit.scale)
            }
        }
    }

    override fun toLong(unit: SCALE): Long {
        return convertUnit(rawValue, 1L, unit.scale)
    }

    override fun toInt(unit: SCALE): Int {
        return toLong(unit).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    override fun compareTo(other: LongUnit<SCALE>): Int {
        return rawValue.compareTo(other.rawValue)
    }

    fun fuzzyEquals(other: LongUnit<SCALE>, precision: SCALE): Boolean {
        return abs(rawValue - other.rawValue) < precision.scale
    }

}

/**
 * Represents a property that can be expressed in different units (such as Length (m) == 1000 x (mm),
 * Time (HH) == 60 x (MM) or (€) = 1.194 ($)) where a minimal precision can be specified sufficiently by an
 * Integer variable rather than a floating point number.
 *
 * @property scale The scaling factor in regard to the minimal precision. If (mm) is the minimum precision then a meter
 * would have a scale factor of 1000L
 */
interface LongUnitScale : NumericUnitScale {
    override val scale: Long

}


/**
 *  Adds scaling to the set of operations on the underlying unit. *
 */
internal interface ScalarUnit<F : NumericUnitScale> {
    operator fun times(scalar: Int): ScalarUnit<F>

    operator fun times(scalar: Double): ScalarUnit<F>

    operator fun div(scalar: Int): ScalarUnit<F>
    operator fun div(scalar: Double): ScalarUnit<F>
}


/**
 * This method is directly stolen from the JVM Long conversion for Kotlin. The "ReturnCount" issue could be resolved
 * by holding the return value in a variable, but I think the overhead of implementing this is not worth
 * the effort as it does not increase readability.
 */
@Suppress("ReturnCount")
private fun convert(d: Long, dst: Long, src: Long): Long {

    if (src == dst)
        return d
    else if (src < dst)
        return d / (dst / src)

    val r = src / dst
    val m = Long.MAX_VALUE / r
    return if (d > m)
        Long.MAX_VALUE
    else if (d < -m)
        Long.MIN_VALUE
    else
        d * r
}


internal fun convertUnit(value: Long, sourceUnit: Long, targetUnit: Long = 1L): Long {
    return convert(value, targetUnit, sourceUnit)
}

internal fun convertUnit(value: Double, sourceUnit: Long, targetUnit: Long): Double {
    val sInT = convert(1, targetUnit, sourceUnit)
    if (sInT > 0) {
        return value * sInT
    }
    val oInThis = convert(1, sourceUnit, targetUnit)
    return value / oInThis
}
