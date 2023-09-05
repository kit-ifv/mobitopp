package utils.units

import kotlin.math.roundToLong

@JvmInline
value class Mass(override val rawValue: Long) : Helper<Mass, MassUnit> {
    override fun create(value: Long): Mass {
        return Mass(value)
    }

    override fun plus(other: Mass): Mass {
        return Mass(rawValue + other.rawValue)
    }

}
enum class MassUnit(override val scale: Long) : ConvertableUnit {
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
    return Mass(convertUnit(toLong(), unit.scale, 1L))
}
fun Long.toMass(unit: MassUnit): Mass {
    return Mass(convertUnit(this, unit.scale, 1L))
}
fun Double.toMass(unit: MassUnit): Mass {
    return Mass(convertUnit(this.roundToLong(), unit.scale, 1L))
}

operator fun Int.times(mass: Mass) : Mass{
    return mass * this
}
val Int.grams: Mass
    get() = this.toMass(MassUnit.GRAM)



