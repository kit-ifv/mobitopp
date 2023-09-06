package utils.units

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

/**
 * Provides the basic functionality for arithmetic operations and primitive type conversions for a unit that can
 * be represented by a long value. The designated inheritors of this interface should be units that do not require
 * high precision but which can cause errors by conversion errors. (Such as assuming a raw Integer as seconds
 * instead of minutes which is not inherently clear to other team members).
 *
 * Support translation (+ and -) operations
 *
 * @param E This self-referential Type is used to prevent arithmetic operations on arbitrary unit types.
 * (such as 1 km + 20 seconds). Only identical units should support arithmetic.
 *
 * @param F A unit should be supplied specifying both the type and the scaling factor to determine the raw value of
 * the unit
 */
internal interface AddableUnit<E: AddableUnit<E, F>, F: ConvertableUnit>:
    Comparable<AddableUnit<E, F>> {
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



    override fun compareTo(other: AddableUnit<E, F>): Int {
        return rawValue.compareTo(other.rawValue)
    }

}

/**
 *  Adds scaling to the set of operations on the underlying unit. *
 */
internal interface ScalarUnit<E: ScalarUnit<E, F>, F: ConvertableUnit>
    : AddableUnit<E, F> {
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