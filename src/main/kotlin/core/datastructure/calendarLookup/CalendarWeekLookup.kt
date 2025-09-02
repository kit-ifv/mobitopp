package core.datastructure.calendarLookup

import utils.WithExpiration
import utils.units.AbsoluteTime
import utils.units.sinceStart
import utils.units.weeks
import utils.withExpiration
import kotlin.time.Duration

/**
 * A calendar-level lookup composed of multiple [WeekLookup]s, indexed by calendar week number.
 *
 * Unlike [WeekLookup] or [TimeLookup], this structure is **not cyclic**: it maps absolute
 * calendar week numbers (e.g. ISO week numbers) to their corresponding week schedules.
 *
 * For a given [AbsoluteTime], this lookup can:
 * - Find the active element [T] in the corresponding [WeekLookup].
 * - Provide the next absolute time when that element changes (its "expiration").
 *
 * If no change occurs in the current week, the lookup searches later weeks in [weekLookup].
 * If none of those contain a change, an optional "default" week (defined as the week
 * immediately after the last explicit entry) is checked.
 * If still none is found, the expiration is treated as infinite.
 */
class CalendarWeekLookup<T>(private val weekLookup: Map<Int, WeekLookup<T>>) {

    /**
     * Returns the element active at [absoluteTime], along with the next absolute time
     * when it will be replaced by another element.
     *
     * @param absoluteTime the absolute timestamp to query
     * @return a [WithExpiration] wrapper containing the active element and its expiration
     */
    operator fun get(absoluteTime: AbsoluteTime): WithExpiration<T> {
        val weekNumber = absoluteTime.week
        val (element, nextChangeThisWeek) = weekLookup.getValue(weekNumber)[absoluteTime]
        val absoluteNextChangeTime = nextChangeThisWeek?.plus(weekNumber.weeks)?.sinceStart ?: findNextAbsoluteChange(absoluteTime, element)

        return element.withExpiration(absoluteNextChangeTime)
    }
    /**
     * Finds the next absolute time when [element] changes after [absoluteTime].
     *
     * Search order:
     * 1. Later weeks in [weekLookup] with a first change different from [element].
     * 2. A "default" week immediately after the last defined week (if present).
     * 3. Otherwise, returns [Duration.INFINITE].
     *
     * @return the absolute time of the next change.
     */
    private fun findNextAbsoluteChange(absoluteTime: AbsoluteTime, element: T): AbsoluteTime {
        val weekNumber = absoluteTime.week
        val potentialDuration = weekLookup.entries
            .filter { it.key > weekNumber }
            .firstNotNullOfOrNull { it.value.findFirstChangeInWeek(element)?.plus(it.key.weeks) }


        val changeTime = potentialDuration ?:
        getFallbackWeek()?.findFirstChangeInWeek(element)?.plus(ceilingWeekNumber.weeks)
        ?: Duration.Companion.INFINITE

        return changeTime.sinceStart
    }
    /**
     * Returns the optional "default" week, defined as the week immediately after
     * the highest key in [weekLookup]. Used as a fallback for element expiration.
     */
    private fun getFallbackWeek(): WeekLookup<T>? {
        if (weekLookup.isEmpty()) return null
        return weekLookup.getValue(ceilingWeekNumber)
    }

    /**
     * The highest number of a week not found in the lookup.
     */


    val ceilingWeekNumber: Int = if(weekLookup.isEmpty()) 0 else (weekLookup.keys.max() + 1)
}