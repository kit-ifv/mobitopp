package utils.units

import java.time.DayOfWeek
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

typealias Time = AbsoluteTime

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
            second.seconds,
    )

    val sinceStart get() = offset

    val daysSinceStart get() = offset.inWholeDays
    val hoursSinceStart get() = offset.inWholeHours
    val minutesSinceStart get() = offset.inWholeMinutes
    val secondsSinceStart get() = offset.inWholeSeconds

    val weekDay
        get() = decodeDayOfWeek(
            (day % DAYS_PER_WEEK).let { if (it >= 0) it else it + DAYS_PER_WEEK },
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

    operator fun plus(other: Duration): AbsoluteTime = AbsoluteTime(offset + other)

    operator fun minus(other: Duration): AbsoluteTime = AbsoluteTime(offset - other)

    operator fun minus(other: AbsoluteTime): Duration = offset - other.offset

    override operator fun compareTo(other: AbsoluteTime): Int = this.offset.compareTo(other.offset)

    operator fun rangeTo(other: AbsoluteTime): AbsoluteTimeProgression = AbsoluteTimeProgression(this, other, 1.minutes)

    /**
     * Using + mod cheat to always get a positive number
     */
    operator fun rem(absoluteTime: AbsoluteTime): AbsoluteTime =
        AbsoluteTime((offset + absoluteTime.offset).floorRem(absoluteTime.offset))

    operator fun rem(duration: Duration): Duration = (offset + duration).floorRem(duration)
    fun floorDiv(duration: Duration): Long {
        val q = offset / duration
        return floor(q).toLong()
    }
    fun ceilDiv(duration: Duration): Duration {
        val q = offset / duration
        return ceil(q) * duration
    }
    fun floorDiv(time: AbsoluteTime): Long = floorDiv(time.sinceStart)

    companion object {
        val START = AbsoluteTime(Duration.ZERO)
        val MINUS_INFINITY = AbsoluteTime(-Duration.INFINITE)
        val INFINITY = AbsoluteTime(Duration.INFINITE)

        fun max(first: AbsoluteTime, second: AbsoluteTime): AbsoluteTime = if (first >= second) first else second

        fun min(first: AbsoluteTime, second: AbsoluteTime): AbsoluteTime = if (first <= second) first else second
    }

    fun truncateMinutes() = AbsoluteTime(minutesSinceStart.minutes)
    fun truncateHours() = AbsoluteTime(hoursSinceStart.hours)
    fun truncateDays() = AbsoluteTime(daysSinceStart.days)
    fun roundToMultipleOf(duration: Duration) = AbsoluteTime(
        duration * (secondsSinceStart.div(duration.inWholeSeconds)).toInt(),
    )
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

/**
 * Convenience function to get an [AbsoluteTime] from a number, by converting to Double -> Duration in Hours ->
 * Absolute Time.
 */
fun Number.toAbsoluteHours(): AbsoluteTime = toDouble().hours.sinceStart

fun Number.toAbsoluteMinutes(): AbsoluteTime = toDouble().minutes.sinceStart
