package utils.units

import java.time.DayOfWeek
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration

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

operator fun Duration.rem(other: Duration): Duration =
    (this.inWholeSeconds % other.inWholeSeconds).toDuration(DurationUnit.SECONDS)

fun Duration.floorRem(other: Duration): Duration {
    val result = this % other
    return if (result < Duration.ZERO) result + other else result
}

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

    val sinceStart get() = offset

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

    /**
     * Using + mod cheat to always get a positive number
     */
    operator fun rem(absoluteTime: AbsoluteTime): AbsoluteTime {
        return AbsoluteTime((offset + absoluteTime.offset).floorRem(absoluteTime.offset))
    }

    operator fun rem(duration: Duration): Duration {
        return (offset + duration).floorRem(duration)
    }
    fun floorDiv(duration: Duration): Long {
        val q = offset / duration
        return floor(q).toLong()
    }
    fun ceilDiv(duration: Duration): Duration {
        val q = offset / duration
        return ceil(q) * duration
    }
    fun floorDiv(time: AbsoluteTime): Long {
        return floorDiv(time.sinceStart)
    }
    operator fun rangeTo(other: AbsoluteTime): AbsoluteTimeProgression =
        AbsoluteTimeProgression(this, other, 1.minutes)

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

class AbsoluteTimeProgression(
    override val start: AbsoluteTime,
    override val endInclusive: AbsoluteTime,
    val step: Duration,
) : Iterable<AbsoluteTime>, ClosedRange<AbsoluteTime> {

    init {
        require(step != Duration.ZERO && !step.isNegative()) {
            "Step must be positive, but was $step"
        }
    }

    override fun iterator(): Iterator<AbsoluteTime> = object : Iterator<AbsoluteTime> {
        private var current = start

        override fun hasNext(): Boolean = current <= endInclusive

        override fun next(): AbsoluteTime {
            if (!hasNext()) {
                throw NoSuchElementException(
                    "Nex increment step ${current + step} is out of range: [$start, $endInclusive]"
                )
            }

            val result = current
            current += step
            return result
        }
    }

    infix fun step(newStep: Duration): AbsoluteTimeProgression =
        AbsoluteTimeProgression(start, endInclusive, newStep)
}
