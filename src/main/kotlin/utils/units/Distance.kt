package utils.units


import kotlin.math.roundToLong

@JvmInline
value class Distance (override val rawValue: Long): ScalarUnit<Distance, DistanceUnit> {


    val inWholeMillimeters: Long
        get() = toLong(DistanceUnit.MILLIMETERS)

    val inWholeCentimeters: Long
        get() = toLong(DistanceUnit.CENTIMETERS)

    val inWholeMeters: Long
        get() = toLong(DistanceUnit.METERS)

    companion object {
        val INFINITE = Distance(Long.MAX_VALUE)
        fun ofMeters(i: Int): Distance {
            return i.toDistance(DistanceUnit.METERS)
        }
        fun ofKilometers(i: Int): Distance {
            return i.toDistance(DistanceUnit.KILOMETERS)
        }
        fun ofKilometers(d: Double): Distance {
            return d.toDistance(DistanceUnit.KILOMETERS)
        }

    }

    override fun unaryMinus(): Distance {
        return Distance(-rawValue)
    }

    override fun times(scalar: Int): Distance {
        if (isInfinite()) {
            return when {
                scalar == 0 -> throw IllegalArgumentException("Multiplying infinity with 0 is an undefined operation")
                scalar > 0 -> INFINITE
                else -> Distance(-Long.MAX_VALUE)
            }
        }
        if (scalar == 0) {
            return Distance(0)
        }
        return Distance(scalar * rawValue)
    }

    override fun times(scalar: Double): Distance {
        return Distance((scalar * rawValue).roundToLong())
    }

    override fun div(scalar: Int): Distance {
        if(scalar == 0) {
            return when {
                rawValue > 0 -> INFINITE
                rawValue < 0 -> Distance(-Long.MAX_VALUE)
                else -> throw IllegalArgumentException("Dividing 0 by 0 is an undefined mathematical operation")
            }
        }
        return Distance(rawValue / scalar)

    }

    override fun div(scalar: Double): Distance {
        return Distance((rawValue / scalar).roundToLong())
    }

    override fun minus(other: Distance): Distance {
        return this + (-other)
    }

    override fun plus(other: Distance): Distance {
        return Distance(rawValue + other.rawValue)
    }
}

enum class DistanceUnit(override val scale: Long) : LongUnitScale {
    MICROMETERS(1L),
    MILLIMETERS(1000L),
    CENTIMETERS(10_000L),
    INCH(25_400L),
    DECIMETERS(100_000L),
    YARD(914_400L),
    METERS(1_000_000L),
    KILOMETERS(1_000_000_000L),
    MILE(1_609_340_000L),
    SEA_MILE(1_852_000_000L);

    companion object {
        /**
         * The smallest possible unit of distance represented by this enumeration
         */
        fun atomic(): DistanceUnit {
            val atomic = MICROMETERS
            assert(atomic.scale == 1L)
            return atomic
        }
    }
}



/**
 *
 * If the need for smaller representation than [DistanceUnit.MICROMETERS] ever arises and different range
 * representations as in the [kotlin.time.Duration] are necessary this method is the entry point for all
 * conversions to allow simple alteration
 */
fun Double.toDistance(unit: DistanceUnit): Distance {
    val millis = (this*unit.scale).roundToLong()
    return Distance(convertUnit(millis, 1L))
}

fun Long.toDistance(unit: DistanceUnit): Distance {
    return Distance(convertUnit(this, unit.scale))
}

fun Int.toDistance(unit: DistanceUnit): Distance {
    return toLong().toDistance(unit)
}

inline val Int.meters: Distance
    get() =  this.toDistance(DistanceUnit.METERS)

inline val Int.kilometers: Distance
    get() =  this.toDistance(DistanceUnit.KILOMETERS)


inline val Long.meters: Distance
    get() =  this.toDistance(DistanceUnit.METERS)

inline val Long.kilometers: Distance
    get() =  this.toDistance(DistanceUnit.KILOMETERS)


inline val Double.meters: Distance
    get() =  this.toDistance(DistanceUnit.METERS)

inline val Double.kilometers: Distance
    get() =  this.toDistance(DistanceUnit.KILOMETERS)




