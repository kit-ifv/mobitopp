package utils.units

import kotlin.math.roundToLong

@JvmInline
value class Mass(override val rawValue: Long) : LongUnit<MassUnit>, ScalarUnit<MassUnit> {

    override fun unaryMinus(): Mass {
        return Mass(-rawValue)
    }

    override fun times(scalar: Int): Mass {
        if (isInfinite()) {
            return when {
                scalar == 0 -> throw IllegalArgumentException("Multiplying infinity with 0 is an undefined operation")
                scalar > 0 -> Mass(Long.MAX_VALUE)
                else -> Mass(-Long.MAX_VALUE)
            }
        }
        if (scalar == 0) {
            return Mass(0)
        }
        return Mass(scalar * rawValue)
    }

    override fun times(scalar: Double): Mass {
        return Mass((scalar * rawValue).roundToLong())
    }

    override fun div(scalar: Int): Mass {
        if (scalar == 0) {
            return when {
                rawValue > 0 -> Mass(Long.MAX_VALUE)
                rawValue < 0 -> Mass(-Long.MAX_VALUE)
                else -> throw IllegalArgumentException("Dividing 0 by 0 is an undefined mathematical operation")
            }
        }
        return Mass(rawValue / scalar)

    }

    override fun div(scalar: Double): Mass {
        return Mass((rawValue / scalar).roundToLong())
    }

    override fun minus(other: LongUnit<MassUnit>): Mass {
        return this + (-other)
    }

    override fun plus(other: LongUnit<MassUnit>): Mass {
        return Mass(rawValue + other.rawValue)
    }

}

enum class MassUnit(override val scale: Long) : LongUnitScale {
    MICROGRAM(1L),
    MILLIGRAM(1000L),
    GRAM(1_000_000L),
    KILOGRAM(1_000_000_000L),
    TON(1_000_000_000_000L);
}

/**
 * These extension functions provide basic functionality to convert to mass with a unit. Common use cases should be
 * implemented as convenience extension function in the setting below
 */
fun Int.toMass(unit: MassUnit): Mass {
    return Mass(convertUnit(toLong(), unit.scale))
}

fun Long.toMass(unit: MassUnit): Mass {
    return Mass(convertUnit(this, unit.scale))
}

fun Double.toMass(unit: MassUnit): Mass {
    val x = (this * unit.scale).roundToLong()
    return Mass(convertUnit(x, 1L))
}

operator fun Int.times(mass: Mass): Mass {
    return mass * this
}

inline val Int.grams: Mass
    get() = this.toMass(MassUnit.GRAM)

inline val Int.kilograms: Mass
    get() = this.toMass(MassUnit.KILOGRAM)



