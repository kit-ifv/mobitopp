package core.datastructure.calendarLookup

typealias CalendarLookupOperation<T> = CalendarWeekLookupBuilder<T>.(Collection<WeekLookupOperation<T>>) -> Unit
typealias WeekLookupOperation<T> = WeekLookupBuilder<T>.() -> Unit
typealias TimeLookupOperation<T> = TimeLookupBuilder<T>.(Int) -> Unit
/**
 * A mutable builder for creating a [CalendarWeekLookup].
 *
 * This class allows incrementally populating week-specific [WeekLookupBuilder]s, keyed by
 * calendar week number, and optionally providing a **default** [WeekLookupBuilder] as a fallback.
 *
 * Behavior:
 * - Explicit entries are stored in [weekLookups].
 * - If a defaultWeekLookupInstructions is defined, it will be used for any calendar week number that is not explicitly present.
 * - If no default is defined, only explicitly set weeks are available.
 *
 * Once populated, [build] finalizes the structure into an immutable [CalendarWeekLookup].
 */
class CalendarWeekLookupBuilder<T>(
    private val weekLookups: MutableMap<Int, MutableList<WeekLookupOperation<T>>> = mutableMapOf()
) {
    private var defaultWeekLookupInstructions: MutableList<WeekLookupOperation<T>> = mutableListOf()

    /**
     * Sets the [default] week lookup, which is used for all week numbers
     * not explicitly present in [weekLookups].
     */
    fun applyDefaultInstructions(default: Collection<WeekLookupOperation<T>>) {
        defaultWeekLookupInstructions.addAll(default)
    }

    /**
     * Adds or replaces a [WeekLookup] for the given calendar week [weekNumber].
     */
    operator fun set(weekNumber: Int, day: Collection<WeekLookupOperation<T>>) {
        val currentWeek = weekLookups.getOrPut(weekNumber) { mutableListOf() }

        currentWeek.addAll(day)
    }

    /**
     * Finalizes the builder and returns a [CalendarWeekLookup].
     *
     * - If a [defaultWeekLookupInstructions] is set, the resulting lookup will fall back to it for
     *   any week numbers not explicitly present.
     * - If no [defaultWeekLookupInstructions] is set, only the explicitly inserted weeks will be available.
     */

    fun build(): CalendarWeekLookup<T> {
        val defaultWeek = WeekLookupBuilder<T>()
        defaultWeek.applyAllDefaultRules()
        val weekBuilders = weekLookups.mapValues { (_, weekBuilder) ->
            val builder = WeekLookupBuilder<T>()
            builder.applyAllDefaultRules()
            weekBuilder.forEach { instruction ->
                builder.instruction()
            }
            builder
        }
        val build = defaultWeek.build()
        return CalendarWeekLookup(weekBuilders.mapValues { it.value.build() }.withDefault { build })
    }

    private fun WeekLookupBuilder<T>.applyAllDefaultRules() {
        defaultWeekLookupInstructions.forEach { function ->
            this.apply(function)
        }
    }
}
