package core.datastructure.calendarLookup

import kotlinx.datetime.DayOfWeek

/**
 * Builder for constructing a [WeekLookup] from daily [DayTimeLookupBuilder]s.
 *
 * Definitions can be inserted at three priority levels:
 *
 * 1. **Default** — applies to all days of the week, priority 0.
 * 2. **Workdays** — applies to Monday through Friday, priority 1.
 * 3. **Specific** — applies to a single [DayOfWeek], priority 2.
 *
 * **Resolution rules:**
 * - Each day accumulates operations from all applicable priority levels, up to the maximum
 *   that has been applied for that day.
 * - When definitions overlap in time, higher-priority instructions override lower-priority ones.
 * - Lower-priority instructions are not discarded: they still apply in time ranges
 *   that are not covered by higher-priority instructions.
 *
 * The result is a [WeekLookup] where each day is composed from one or more definitions,
 * layered by priority. This allows partial definitions at higher levels to fall back
 * to lower-level definitions when needed.
 *
 * The generic type [T] represents the element associated with each time interval
 * and is not restricted to any particular domain.
 */
class WeekLookupBuilder<T>(
    private val dayLookups: Map<DayOfWeek, DayTimeLookupBuilder<T>> =
        DayOfWeek.entries.associateWith { DayTimeLookupBuilder() },
) {

    private val workdays: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    /**
     * Inserts the given [instruction] into all days with **priority 0** (default).
     *
     * These segments apply universally, but can be overridden by higher-priority
     * definitions where overlaps occur.
     */
    fun setDefault(instruction: TimeLookupOperation<T>) = applyToDays(DayOfWeek.entries, instruction, 0)

    /**
     * Inserts the given [instruction] into all workdays (Mon–Fri) with **priority 1**.
     *
     * These segments override defaults where they overlap, but may still fall back
     * to default definitions for uncovered time ranges.
     */
    fun setWorkdays(instruction: TimeLookupOperation<T>) = applyToDays(workdays, instruction, 1)

    /**
     * Inserts the given [day] into the specified [dayOfWeek] with **priority 2**.
     *
     * These segments override both workday and default definitions where they overlap,
     * but still fall back to lower-priority definitions where they do not provide coverage.
     */
    operator fun set(dayOfWeek: DayOfWeek, day: TimeLookupOperation<T>) =
        applyToDays(listOf(dayOfWeek), day, 2)
    private fun applyToDays(days: Iterable<DayOfWeek>, source: TimeLookupOperation<T>, priority: Int) =
        applyToDays(days, PrioritizedOperation(source, priority))
    private fun applyToDays(days: Iterable<DayOfWeek>, source: PrioritizedOperation<T>) {
        for (day in days) {
            val dayBuilder = dayLookups.getValue(day)
            val (operation, priority) = source
            dayBuilder.operation(priority)
        }
    }

    /**
     * Finalizes the builder and produces a [WeekLookup].
     *
     * Each [TimeLookupBuilder] is built into a [DayTimeLookup],
     * preserving the precedence rules that were applied.
     */
    fun build(): WeekLookup<T> {
        return WeekLookup(dayLookups.mapValues { it.value.build() })
    }

    override fun toString(): String {
        return dayLookups.toString()
    }
}
