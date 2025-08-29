package domain.shared.datastructure.matrix

import kotlinx.datetime.DayOfWeek

/**
 * Builder for constructing a [WeekLookup] from daily [DayTimeLookupBuilder]s.
 *
 * Definitions can be provided at three levels of specificity:
 *
 * 1. **Default** — applies to all days of the week.
 * 2. **Workdays** — applies to Monday through Friday.
 * 3. **Specific** — applies to a single [kotlinx.datetime.DayOfWeek].
 *
 * **Resolution rules:**
 * - Each [kotlinx.datetime.DayOfWeek] has an internal "level" marker, starting at `DEFAULT`.
 * - Segments from definitions at the current level or higher are accepted
 *   into that day's [DayTimeLookupBuilder].
 * - Once a day has been promoted to a higher level (e.g. `SPECIFIC`),
 *   definitions at lower levels will no longer be applied.
 *
 */
class WeekLookupBuilder<T>(
    private val dayLookups: Map<DayOfWeek, DayTimeLookupBuilder<T>> =
        DayOfWeek.entries.associateWith { DayTimeLookupBuilder() },
) {

    private enum class Level : Comparable<Level> {
        DEFAULT, WORKDAYS, SPECIFIC;
    }

    private val levels: MutableMap<DayOfWeek, Level> = DayOfWeek.entries
        .associateWith { Level.DEFAULT }
        .toMutableMap()
    private val workdays: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    /**
     * Inserts the given [default] builder into all days at **default** level.
     *
     * Days already marked at a higher level (`WORKDAYS` or `SPECIFIC`)
     * will not accept these segments.
     */
    fun setDefault(default: DayLookupBuilder<T>) = applyToDays(DayOfWeek.entries, Level.DEFAULT, default)

    /**
     * Inserts the given [time] builder into all **workdays** (Mon–Fri).
     *
     * Days already marked as `SPECIFIC` will not accept these segments.
     * Partial definitions at `WORKDAYS` can still fall back to segments
     * from `DEFAULT`.
     */
    fun setWorkdays(time: DayLookupBuilder<T>) = applyToDays(workdays, Level.WORKDAYS, time)


    /**
     * Inserts the given [day] builder into a specific [dayOfWeek] day.
     *
     * Marks that day as `SPECIFIC`. Once specific, the day will no longer
     * accept insertions from `WORKDAYS` or `DEFAULT`.
     */
    operator fun set(dayOfWeek: DayOfWeek, day: DayLookupBuilder<T>) = applyToDays(listOf(dayOfWeek), Level.SPECIFIC, day)

    private fun applyToDays(days: Iterable<DayOfWeek>, level: Level, source: DayLookupBuilder<T>) {
        for (day in days) {
            if (levels[day]!! <= level) {
                levels[day] = level
                dayLookups.getValue(day).insertAll(source.segments())
            }
        }
    }

    /**
     * Finalizes the builder and produces a [WeekLookup].
     *
     * Each [DayLookupBuilder] is built into a [DayTimeLookup],
     * preserving the precedence rules that were applied.
     */
    fun build(): WeekLookup<T> {
        return WeekLookup(dayLookups.mapValues { it.value.build() })
    }
}