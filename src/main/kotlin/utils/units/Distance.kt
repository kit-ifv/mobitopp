package utils.units

import kotlin.math.roundToLong

@JvmInline
value class Distance internal constructor(private val rawValue: Long): Comparable<Distance> {

    companion object {
        val ZERO: Distance = Distance(0L)

        val INFINITE: Distance = Distance(MAX_VALUE)
        val NEG_INFINITE: Distance = Distance(-MAX_VALUE)

        fun ofMeters(m: Int) : Distance = m.toDistance(DistanceUnit.METERS)
        fun ofKilometers(m: Double) : Distance = m.toDistance(DistanceUnit.KILOMETERS)


    }
    fun isInfinite(): Boolean = rawValue == INFINITE.rawValue || rawValue == NEG_INFINITE.rawValue

    fun isFinite(): Boolean = !isInfinite()
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Distance): Int {
        return rawValue.compareTo(other.rawValue)

    }


    operator fun plus(other: Distance) : Distance {

        when {
            this.isInfinite() && other.isInfinite() -> throw IllegalArgumentException("Summing infinite distances ")
            this.isInfinite() && other.isFinite() -> return this
            this.isFinite() && other.isInfinite() -> return other

        }
        val value = rawValue + other.rawValue
        val max = maxOf(rawValue, other.rawValue)

        return Distance(rawValue + other.rawValue)
    }


    operator fun minus(other: Distance) : Distance {
        return this + (-other)
    }

    operator fun unaryMinus(): Distance {
        return Distance(-rawValue)
    }

    operator fun times(scalar: Int): Distance {
        if (isInfinite()) {
            return when {
                scalar == 0 -> throw IllegalArgumentException("0 times Infinity does not work")
                scalar > 0 -> this
                else -> -this
            }
        }
        if (scalar == 0) return ZERO
        return Distance(scalar * rawValue)
    }

    operator fun times(scalar: Double): Distance {

        return (scalar * rawValue).toDistance(DistanceUnit.atomic())
    }

    operator fun div(scalar: Double): Distance {
        return (rawValue / scalar).toDistance(DistanceUnit.atomic())
    }

    operator fun div(scalar: Int): Distance {
        if(scalar == 0) {
            return when {
              this.rawValue > 0 -> INFINITE
              this.rawValue < 0 -> NEG_INFINITE
              else -> throw IllegalArgumentException("dividing zero by zero ")

            }
        }
        return Distance(rawValue / scalar)
    }

    operator fun times(distance: Distance): Area {
        TODO()
    }

    val inWholeMillimeters: Long
        get() = toLong(DistanceUnit.MILLIMETERS)

    val inWholeCentimeters: Long
        get() = toLong(DistanceUnit.CENTIMETERS)

    val inWholeMeters: Long
        get() = toLong(DistanceUnit.METERS)

    val inWholeKilometers: Long
        get() = toLong(DistanceUnit.KILOMETERS)


    fun toInt(unit: DistanceUnit): Int {
        return toLong(unit).coerceIn(Int.MAX_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }
    fun toLong(unit: DistanceUnit): Long {
        return convertDistanceUnit(rawValue, DistanceUnit.atomic(), unit)
    }

    fun toDouble(unit: DistanceUnit): Double {
        return when(rawValue) {
            INFINITE.rawValue -> Double.POSITIVE_INFINITY
            NEG_INFINITE.rawValue -> Double.NEGATIVE_INFINITY

            else -> {
                convertDistanceUnit(rawValue.toDouble(), DistanceUnit.atomic(), unit)
            }
        }
    }
}
internal const val MAX_VALUE = Long.MAX_VALUE

/**
 *
 * If the need for smaller representation than [DistanceUnit.MICROMETERS] ever arises and different range
 * representations as in the [kotlin.time.Duration] are necessary this method is the entry point for all
 * conversions to allow simple alteration
 */
fun Double.toDistance(unit: DistanceUnit): Distance {
    val millis = this.roundToLong()
    return Distance(convertDistanceUnit(millis, unit, DistanceUnit.atomic()))
}

fun Long.toDistance(unit: DistanceUnit): Distance {
    return Distance(convertDistanceUnit(this, unit, DistanceUnit.atomic()))
}

fun Int.toDistance(unit: DistanceUnit): Distance {
    return toLong().toDistance(unit)
}
