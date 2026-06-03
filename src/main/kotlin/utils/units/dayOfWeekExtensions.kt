package utils.units

import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days

@Suppress("MagicNumber")
fun DayOfWeek.encode() = when (this) {
    DayOfWeek.MONDAY -> 0
    DayOfWeek.TUESDAY -> 1
    DayOfWeek.WEDNESDAY -> 2
    DayOfWeek.THURSDAY -> 3
    DayOfWeek.FRIDAY -> 4
    DayOfWeek.SATURDAY -> 5
    DayOfWeek.SUNDAY -> 6
}

fun DayOfWeek.daysSinceStartOfWeek() = this.encode().days
fun decodeDayOfWeek(s: String): DayOfWeek = when (s.lowercase()) {
    "monday", "montag", "mo", "mo." -> (DayOfWeek.MONDAY)

    "tuesday", "dienstag", "di", "tu." -> (DayOfWeek.TUESDAY)

    "wednesday", "mittwoch", "mi", "we." -> (DayOfWeek.WEDNESDAY)

    "thursday", "donnerstag", "do", "th." -> (DayOfWeek.THURSDAY)

    "friday", "freitag", "fr", "fr." -> (DayOfWeek.FRIDAY)

    "saturday", "samstag", "sa", "sa." -> (DayOfWeek.SATURDAY)

    "sunday", "sonntag", "so", "su." -> (DayOfWeek.SUNDAY)

    else -> {
        require(s.trim().all { it.isDigit() }) { "Invalid weekday string: $s" }
        decodeDayOfWeek(s.toInt())
    }
}

@Suppress("MagicNumber")
fun decodeDayOfWeek(i: Int): DayOfWeek = when (i) {
    0 -> (DayOfWeek.MONDAY)
    1 -> (DayOfWeek.TUESDAY)
    2 -> (DayOfWeek.WEDNESDAY)
    3 -> (DayOfWeek.THURSDAY)
    4 -> (DayOfWeek.FRIDAY)
    5 -> (DayOfWeek.SATURDAY)
    6 -> (DayOfWeek.SUNDAY)
    else -> throw IllegalArgumentException("Invalid weekday code: $i")
}
