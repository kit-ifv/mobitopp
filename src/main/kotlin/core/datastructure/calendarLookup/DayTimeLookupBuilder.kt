package core.datastructure.calendarLookup

import kotlin.time.Duration.Companion.days

/**
 * A [TimeLookupBuilder] is a [TimeLookupBuilder] with the modulus already fixed to 1 Day
 */
class DayTimeLookupBuilder<T> : TimeLookupBuilder<T>(modulus = 1.days) {
    override fun build(): DayTimeLookup<T> = DayTimeLookup(
        segments.map { it.range.start.sinceStart }.toTypedArray(),
        segments.map { it.value },
    )
}