package utils.units

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private const val ORDER_OF_MAGNITUDE = 10.0

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= ORDER_OF_MAGNITUDE }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Number.toAbsoluteHours(): AbsoluteTime {
    return toDouble().hours.sinceStart
}

fun Number.toAbsoluteMinutes(): AbsoluteTime {
    return toDouble().minutes.sinceStart
}
