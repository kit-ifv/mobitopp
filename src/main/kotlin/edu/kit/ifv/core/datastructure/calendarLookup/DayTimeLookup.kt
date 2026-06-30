package edu.kit.ifv.core.datastructure.calendarLookup
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * A [DayTimeLookup] is a [TimeLookup] with the modulus already fixed to 1 Day
 */
class DayTimeLookup<T>(timeIndices: Array<Duration>, elements: List<T>) : TimeLookup<T>(timeIndices, elements, 1.days)
