package core.datastructure.calendarLookup

import utils.units.AbsoluteTime
import utils.units.toAbsoluteHours

/**
 * A half-open time interval `[start, end)` with an associated [value] and [priority].
 *
 * - [intersects] checks overlap with another interval or segment.
 * - [subtract] removes an interval from this one, returning the uncovered parts.
 *
 * @param T the type of value carried by the segment
 * @property range the covered time interval
 * @property value the element active during [range]
 * @property priority precedence for conflict resolution (default `0`)
 */
data class TimeSegment<T>(
    val range: OpenEndRange<AbsoluteTime>,
    val value: T,
    val priority: Int = 0,
) {
    constructor(a: Number, b: Number, element: T) : this(a.toAbsoluteHours()..<b.toAbsoluteHours(), element)
    /** Returns `true` if this segment overlaps with [other]. */
    fun intersects(other: OpenEndRange<AbsoluteTime>): Boolean {
        return !(range.start >= other.endExclusive || range.endExclusive <= other.start)
    }
    /** Returns `true` if this segment overlaps with [other]. */
    fun intersects(other: TimeSegment<T>): Boolean = intersects(other.range)

    fun subtract(a: Number, b: Number) = subtract(a.toAbsoluteHours()..<b.toAbsoluteHours())

    /** Returns the non-overlapping parts of this segment after removing [other]. */
    fun subtract(other: OpenEndRange<AbsoluteTime>): List<TimeSegment<T>> {
        val before = range.start..<(AbsoluteTime.Companion.min(other.start, range.endExclusive))

        val after = AbsoluteTime.Companion.max(range.start, other.endExclusive)..<range.endExclusive
        val results = mutableListOf<TimeSegment<T>>()
        if (!before.isEmpty()) {
            results.add(TimeSegment(before, value))
        }
        if (!after.isEmpty()) {
            results.add(TimeSegment(after, value))
        }
        return results
    }

}