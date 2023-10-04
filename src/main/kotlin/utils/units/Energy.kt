package utils.units

import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
value class Energy(override val rawValue: Double): FloatUnit<EnergyUnits>{
    companion object {
        fun of(mass: Mass, distance: Distance, duration: Duration): Energy {
            return Energy(mass.toDouble(MassUnit.KILOGRAM)
                    * distance.toDouble(DistanceUnit.METERS)
                    *distance.toDouble(DistanceUnit.METERS)
                    / duration.toDouble(DurationUnit.SECONDS)
                    / duration.toDouble(DurationUnit.SECONDS))
        }
    }

    override fun plus(other: FloatUnit<EnergyUnits>): Energy{
        return Energy(this.rawValue + other.rawValue)
    }

    override fun minus(other: FloatUnit<EnergyUnits>):Energy {
        return this + (-other)
    }

    override fun unaryMinus(): Energy {
        return Energy(-rawValue)
    }
}

fun Int.toEnergy(units: EnergyUnits): Energy {
    return Energy(this * units.scale)
}


fun Long.toEnergy(units: EnergyUnits): Energy {
    return Energy(this * units.scale)
}

fun Double.toEnergy(units: EnergyUnits): Energy {
    return Energy(this * units.scale)
}

enum class EnergyUnits(override val scale: Double) : FloatUnitScale {
    JOULE(1.0),
    KILOJOULE(1000.0),
    KILOWATTHOUR(3_600_000.0)
}
