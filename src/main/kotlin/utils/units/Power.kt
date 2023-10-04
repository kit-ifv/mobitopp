package utils.units

import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
value class Power(override val rawValue: Double) : FloatUnit<PowerUnit> {
    companion object {
        fun of(energy: Energy, duration: Duration): Power {
            return Power(energy.toDouble(EnergyUnits.JOULE) / duration.toDouble(DurationUnit.SECONDS))
        }
    }
    operator fun times(duration: Duration) : Energy {
        return Energy(this.rawValue * duration.toDouble(DurationUnit.SECONDS))
    }

    override fun plus(other: NumericUnit<PowerUnit>): Power {
        return Power(rawValue + other.rawValue.toLong())
    }

    override fun minus(other: NumericUnit<PowerUnit>): Power {
        return this + (-other)
    }

    override fun unaryMinus(): Power {
        return Power(-rawValue)
    }
}

fun Int.toPower(units: PowerUnit): Power {
    return Power(this * units.scale)
}


fun Long.toPower(units: PowerUnit): Power {
    return Power(this * units.scale)
}

fun Double.toPower(units: PowerUnit): Power {
    return Power(this * units.scale)
}


enum class PowerUnit(override val scale: Double): FloatUnitScale {
    WATTS(1.0),
    KILOWATTS(1000.0)
}
