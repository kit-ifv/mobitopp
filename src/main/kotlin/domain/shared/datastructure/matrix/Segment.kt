package domain.shared.datastructure.matrix

import utils.units.AbsoluteTime
import utils.units.toAbsoluteHours

data class Segment<T>(
    val range: OpenEndRange<AbsoluteTime>,
    val element: T,
    val priority: Int = 0,
) {
    constructor(a: Number, b: Number, element: T) : this(a.toAbsoluteHours()..<b.toAbsoluteHours(), element)

    fun intersects(other: OpenEndRange<AbsoluteTime>): Boolean {
        return !(range.start >= other.endExclusive || range.endExclusive <= other.start)
    }

    fun intersects(other: Segment<T>): Boolean = intersects(other.range)

    fun splice(a: Number, b: Number) = splice(a.toAbsoluteHours()..<b.toAbsoluteHours())

    /**
     * Return the potential Segments of the original that are not covered by the other range.
     */
    fun splice(other: OpenEndRange<AbsoluteTime>): List<Segment<T>> {
        val before = range.start..<(AbsoluteTime.Companion.min(other.start, range.endExclusive))

        val after = AbsoluteTime.Companion.max(range.start, other.endExclusive)..<range.endExclusive
        val results = mutableListOf<Segment<T>>()
        if (!before.isEmpty()) {
            results.add(Segment(before, element))
        }
        if (!after.isEmpty()) {
            results.add(Segment(after, element))
        }
        return results
    }

}