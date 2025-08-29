package domain.shared.datastructure.matrix

/**
 * A mutable builder for creating a [CalendarWeekLookup].
 *
 * This class allows incrementally populating week-specific [WeekLookup]s, keyed by
 * calendar week number, and optionally providing a **default** [WeekLookup] as a fallback.
 *
 * Behavior:
 * - Explicit entries are stored in [weekLookups].
 * - If a [defaultWeekLookup] is defined, it will be used for any calendar week number that is not explicitly present.
 * - If no default is defined, only explicitly set weeks are available.
 *
 * Once populated, [build] finalizes the structure into an immutable [CalendarWeekLookup].
 */
class CalendarWeekLookupBuilder<T>(private val weekLookups: MutableMap<Int, WeekLookup<T>> = mutableMapOf()) {
    private var defaultWeekLookup: WeekLookup<T>? = null
    /**
     * Sets the [default] week lookup, which is used for all week numbers
     * not explicitly present in [weekLookups].
     */
    fun setDefault(default: WeekLookup<T>) {
        this.defaultWeekLookup = default
    }
    /**
     * Adds or replaces a [WeekLookup] for the given calendar week [weekNumber].
     */
    operator fun set(weekNumber: Int, day: WeekLookup<T>) {
        weekLookups[weekNumber] = day
    }
    /**
     * Finalizes the builder and returns a [CalendarWeekLookup].
     *
     * - If a [defaultWeekLookup] is set, the resulting lookup will fall back to it for
     *   any week numbers not explicitly present.
     * - If no [defaultWeekLookup] is set, only the explicitly inserted weeks will be available.
     */

    fun build(): CalendarWeekLookup<T> {
        val lookupMap = defaultWeekLookup?.let { d -> weekLookups.withDefault { d } } ?: weekLookups
        return CalendarWeekLookup(lookupMap)
    }
}