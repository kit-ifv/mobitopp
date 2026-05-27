package utils.units

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private const val ORDER_OF_MAGNITUDE = 10.0

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= ORDER_OF_MAGNITUDE }
    return kotlin.math.round(this * multiplier) / multiplier
}

/**
 * Convenience function to get an [AbsoluteTime] from a number, by converting to Double -> Duration in Hours ->
 * Absolute Time.
 */
fun Number.toAbsoluteHours(): AbsoluteTime = toDouble().hours.sinceStart

fun Number.toAbsoluteMinutes(): AbsoluteTime = toDouble().minutes.sinceStart
