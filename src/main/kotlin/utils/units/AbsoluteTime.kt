package utils.units

import usecases.choicemodels.rem
import java.time.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

typealias Time = AbsoluteTime

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
fun decodeDayOfWeek(i: Int): DayOfWeek =
    when (i) {
        0 -> (DayOfWeek.MONDAY)
        1 -> (DayOfWeek.TUESDAY)
        2 -> (DayOfWeek.WEDNESDAY)
        3 -> (DayOfWeek.THURSDAY)
        4 -> (DayOfWeek.FRIDAY)
        5 -> (DayOfWeek.SATURDAY)
        6 -> (DayOfWeek.SUNDAY)
        else -> throw IllegalArgumentException("Invalid weekday code: $i")
    }

const val DAYS_PER_WEEK = 7
const val HOURS_PER_DAY = 24
const val MINUTES_PER_HOUR = 60
const val SECONDS_PER_MINUTE = 60

/**
 * Absolute time can be seen as the time passed since the start of the simulation
 *
 * @property offset time offset from the beginning of the operation
 * @see [Duration]
 */
@JvmInline
value class AbsoluteTime(private val offset: Duration) : Comparable<AbsoluteTime> {

    constructor(
        weekday: DayOfWeek,
        hour: Int,
        minute: Int,
        second: Int,
        week: Int,
    ) : this(
        (week * DAYS_PER_WEEK).days +
            weekday.daysSinceStartOfWeek() +
            hour.hours + minute.minutes +
            second.seconds
    )

    val daysSinceStart get() = offset.inWholeDays
    val hoursSinceStart get() = offset.inWholeHours
    val minutesSinceStart get() = offset.inWholeMinutes
    val secondsSinceStart get() = offset.inWholeSeconds

    val weekDay
        get() = decodeDayOfWeek(
            (day % DAYS_PER_WEEK).let { if (it >= 0) it else it + DAYS_PER_WEEK }
        )

    // TODO convert to duration?
    val week get() = (offset.inWholeDays / DAYS_PER_WEEK).toInt()
    val day get() = (offset.inWholeDays % DAYS_PER_WEEK).toInt()
    val hour get() = (offset.inWholeHours % HOURS_PER_DAY).toInt()
    val minute get() = (offset.inWholeMinutes % MINUTES_PER_HOUR).toInt()
    val second get() = (offset.inWholeSeconds % SECONDS_PER_MINUTE).toInt()

    override fun toString(): String {
        val weekPrefix = if (week != 0) "${week}w " else ""
        return weekPrefix + (offset % 1.weeks).toString()
    }

    operator fun plus(other: Duration): AbsoluteTime {
        return AbsoluteTime(offset + other)
    }

    operator fun minus(other: Duration): AbsoluteTime {
        return AbsoluteTime(offset - other)
    }

    operator fun minus(other: AbsoluteTime): Duration {
        return offset - other.offset
    }

    override operator fun compareTo(other: AbsoluteTime): Int {
        return this.offset.compareTo(other.offset)
    }

    companion object {
        val START = AbsoluteTime(Duration.ZERO)
        val MINUS_INFINITY = AbsoluteTime(-Duration.INFINITE)
        val INFINITY = AbsoluteTime(Duration.INFINITE)

        fun max(first: AbsoluteTime, second: AbsoluteTime): AbsoluteTime {
            return if (first >= second) first else second
        }

        fun min(first: AbsoluteTime, second: AbsoluteTime): AbsoluteTime {
            return if (first <= second) first else second
        }
    }
}

/** Returns a [Duration] equal to this [Int] number of weeks. */
inline val Int.weeks get() = (this * DAYS_PER_WEEK).days

/** Returns a [Duration] equal to this [Long] number of weeks. */
inline val Long.weeks get() = (this * DAYS_PER_WEEK).days

/**
 * Returns a [Duration] equal to this [Double] number of weeks.
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this [Double] value is `NaN`.
 */
inline val Double.weeks get() = (this * DAYS_PER_WEEK).days

inline val Duration.sinceStart get() = AbsoluteTime.START + this

fun main() {
    val start = Time.START + 5.hours
    val end = Time.START + DAYS_PER_WEEK.hours + 12.minutes

    print(end - start)
}
