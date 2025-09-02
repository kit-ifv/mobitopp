package core.datastructure.calendarLookup

import kotlinx.datetime.DayOfWeek
import utils.units.AbsoluteTime
import utils.units.daysSinceStartOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * A weekly lookup table built from per-day [TimeLookup]s.
 *
 * This class maps any [AbsoluteTime] to the element [T] that is active at that point in time.
 * It also supports finding the next time (relative to the start of the week) when the element changes,
 * allowing callers to know the "expiration time" of the current element.
 *
 * Internally, a [WeekLookup] holds one [DayTimeLookup] for each [DayOfWeek].
 *
 * Key behaviors:
 * - `get(time)` returns both the current element and the next change time (if any).
 * - The next change is searched within the same day first; if none is found, subsequent days are checked.
 * - The week does **not** wrap from Sunday to Monday. That wrap-around is handled by higher-level logic.
 */
class WeekLookup<T>(private val dayLookups: Map<DayOfWeek, DayTimeLookup<T>>) {
    /**
     * Returns the element active at [absoluteTime], along with the relative time (within the week)
     * when this element will next be replaced.
     *
     * @param absoluteTime the absolute timestamp to query
     * @return a pair consisting of the active element, and the expiration time as a [Duration]
     *         relative to the start of the week (or `null` if the element never changes).
     */
    operator fun get(absoluteTime: AbsoluteTime): Pair<T, Duration?> {
        val weekDay = absoluteTime.weekDay
        return dayLookups.getValue(weekDay).run {
            val element = this[absoluteTime]
            val potentialNextStateChangeTime = findNextChange(element, absoluteTime)
            element to potentialNextStateChangeTime
        }
    }

    /**
     * Finds the first time in the entire week when [element] is replaced by another value, if any.
     *
     * This is effectively the week-level equivalent of [TimeLookup.findNextChangeTime].
     *
     * @return the relative time within the week when the element changes, or `null` if it does not change.
     */
    fun findFirstChangeInWeek(element: T): Duration? {
        return findNextChangeInLaterDays(element, DayOfWeek.MONDAY)
    }

    /**
     * Finds the next change time for [element], starting from [absoluteTime].
     *
     * Strategy:
     * 1. Look for a change within the same day ([findNextChangeInDay]).
     * 2. If none exists, search subsequent days of the week ([findNextChangeInLaterDays]).
     *
     * @return the relative time (since the start of the week) when the element changes,
     *         or `null` if the element never changes within this week.
     */
    private fun TimeLookup<T>.findNextChange(element: T, absoluteTime: AbsoluteTime): Duration? {
        val currentWeekDay = absoluteTime.weekDay
        val currentDayChange = findNextChangeInDay(element, skipUntil = absoluteTime)?.plus(currentWeekDay.daysSinceStartOfWeek())
        val nextDaysChange = findNextChangeInLaterDays(element, skipUntil = currentWeekDay.next())

        return currentDayChange ?: nextDaysChange

    }

    /**
     * Finds the next change for [element] strictly within this day's [TimeLookup].
     *
     * "Local" indicates that the search is confined to a single day, without looking into later days.
     */
    private fun TimeLookup<T>.findNextChangeInDay(element: T, skipUntil: AbsoluteTime): Duration? {
        return findNextChangeTime(element, skipUntil)
    }

    /**
     * Finds the next change for [element], searching across later days in the week starting at [skipUntil].
     *
     * - If [skipUntil] is `null`, no later day exists.
     * - Otherwise, returns the first day (≥ [skipUntil]) where a change occurs, combined with the offset
     *   of that change within the day.
     */
    private fun findNextChangeInLaterDays(element: T, skipUntil: DayOfWeek?): Duration? {
        if (skipUntil == null) return null
        val firstMismatch =
            dayLookups.entries.firstOrNull { it.key >= skipUntil && it.value.findNextChangeTime(element) != null }
        if (firstMismatch == null) return null
        val (nextWeekday, lookup) = firstMismatch
        val withinDayOffset = lookup.findNextChangeTime(element)!!
        val timePoint = nextWeekday.toDuration() + withinDayOffset
        return timePoint
    }

    /**
     * Converts a [DayOfWeek] to a [Duration] offset from the start of the week (Monday = 0).
     *
     * Example: `MONDAY -> 0 days`, `TUESDAY -> 1 day`, … `SUNDAY -> 6 days`.
     */
    private fun DayOfWeek.toDuration(): Duration {
        return ordinal.days
    }

    /**
     * Returns the next day of the week, or `null` if this is Sunday.
     *
     * Unlike some cyclic calendars, [WeekLookup] does not wrap from Sunday back to Monday.
     * That wrap-around is handled externally by higher-level logic.
     */
    private fun DayOfWeek.next(): DayOfWeek? {
        return if (this == DayOfWeek.SUNDAY) null else this.plus(1)
    }

}