package utils.units

import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
value class Speed(override val rawValue: Double) :FloatUnit<SpeedUnit> {
    operator fun times(time: Duration): Distance {
        return (this.rawValue * time.toDouble(DurationUnit.SECONDS)).toDistance(DistanceUnit.METERS)
    }

}
fun Int.toSpeed(units: SpeedUnit): Speed {
    return Speed(this * units.scale)
}
fun Long.toSpeed(units: SpeedUnit): Speed {
    return Speed(this * units.scale)
}

fun Double.toSpeed(units: SpeedUnit): Speed {
    return Speed(this * units.scale)
}
enum class SpeedUnit(override val scale: Double) : FloatUnitScale {
    METER_PER_SECOND(1.0),
    KILOMETER_PER_HOUR(0.277778),
    MILES_PER_HOUR(0.44704),
    KNOTS(0.514444)
}
