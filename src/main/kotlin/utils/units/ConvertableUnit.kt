package utils.units

import kotlin.math.roundToLong

/**
 * Represents a property that can be expressed in different units (such as Length (m) == 1000 x (mm),
 * Time (HH) == 60 x (MM) or (€) = 1.194 ($)) where a minimal precision can be specified sufficiently by an
 * Integer variable rather than a floating point number.
 *
 * @property scale The scaling factor in regard to the minimal precision. If (mm) is the minimum precision then a meter
 * would have a scale factor of 1000L
 */
interface ConvertableUnit {
    val scale: Long
}
internal interface Helper<E: Helper<E, F>, F: ConvertableUnit>: Comparable<Helper<E, F>> {
    val rawValue: Long
    private val infinity: Long
        get() = Long.MAX_VALUE

    private val negInfinity: Long
        get() = -Long.MAX_VALUE

    private val zero: Long
        get() = 0


    fun isInfinite(): Boolean {
        return rawValue == infinity || rawValue == negInfinity
    }
    fun toDouble(unit: F): Double {
        return when(rawValue) {
            infinity -> Double.POSITIVE_INFINITY
            negInfinity -> Double.NEGATIVE_INFINITY
            else -> {
                convertUnit(rawValue.toDouble(), 1L, unit.scale)
            }
        }
    }
    fun toLong(unit: F): Long {
        return convertUnit(rawValue, 1L, unit.scale)
    }
    fun toInt(unit: F): Int {
        return toLong(unit).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    operator fun plus(other: E): E

    operator fun unaryMinus(): E
    operator fun minus(other: E): E



    override fun compareTo(other: Helper<E, F>): Int {
        return rawValue.compareTo(other.rawValue)
    }

}

internal interface ScalarHelper<E: ScalarHelper<E, F>, F: ConvertableUnit>: Helper<E, F> {
    operator fun times(scalar: Int): E

    operator fun times(scalar: Double): E

    operator fun div(scalar: Int): E
    operator fun div(scalar: Double): E
}






private fun convert(d: Long, dst: Long, src: Long): Long {

    if (src == dst)
        return d;
    else if (src < dst)
        return d / (dst / src)

    val r = src / dst
    val m = Long.MAX_VALUE / r
    if (d > m)
        return Long.MAX_VALUE;
    else if (d < -m)
        return Long.MIN_VALUE;
    else
        return d * r;
}



internal fun convertUnit(value: Long, sourceUnit: Long, targetUnit : Long = 1L): Long {
    return convert(value, targetUnit, sourceUnit)
}

internal fun convertUnit(value: Double, sourceUnit: Long, targetUnit: Long): Double {
    val sInT = convert(1, targetUnit, sourceUnit)
    if(sInT > 0) {
        return value * sInT
    }
    val oInThis = convert(1, sourceUnit, targetUnit)
    return value / oInThis
}