package utils.units

import kotlin.math.roundToLong

@JvmInline
value class Temperature(override val rawValue: Long) : LongUnit<TemperatureUnit>{
    override fun plus(other: LongUnit<TemperatureUnit>): Temperature {
        return Temperature(rawValue + other.rawValue.toLong())
    }

    override fun unaryMinus(): Temperature {
        return Temperature(-rawValue)
    }

    override fun minus(other: LongUnit<TemperatureUnit>): Temperature {
        return this + (-other)
    }

    override fun toLong(unit: TemperatureUnit): Long {
        return convertUnit(rawValue - unit.offset, 1L, unit.scale)
    }

    override fun toInt(unit: TemperatureUnit): Int {
        return toLong(unit).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    override fun toDouble(unit: TemperatureUnit): Double {
        return convertUnit(rawValue.toDouble() - unit.offset, 1L, unit.scale)


    }
}

enum class TemperatureUnit(override val scale: Long, val offset : Long): LongUnitScale {

    KELVIN(1_000_000L, 0),
    FAHRENHEIT(555_556L, 255_372_222L),
    CELSIUS(1_000_000L, 273_150_000L);


}
fun Int.toTemperature(unit: TemperatureUnit): Temperature {
    return Temperature(convertUnit(toLong(), unit.scale) + unit.offset)
}
fun Long.toTemperature(unit: TemperatureUnit): Temperature {
    return Temperature(convertUnit(this, unit.scale) + unit.offset)
}
fun Double.toTemperature(unit: TemperatureUnit): Temperature {
    val roundToLong = (this * unit.scale).roundToLong()
    return Temperature(roundToLong + unit.offset)
}
