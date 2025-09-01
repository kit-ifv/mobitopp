package domain.shared.datastructure.matrix

import utils.units.AbsoluteTime
import utils.units.sinceStart
import utils.units.toAbsoluteMinutes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * Builder for creating a [TimeLookup] from interval definitions.
 *
 * This class manages a set of [Segment]s, where each segment associates an element [T]
 * with a half-open time interval. Segments may overlap, and overlaps are resolved according to:
 *
 * 1. **Priority** — the segment with the higher priority wins.
 * 2. **Insertion order** — if priorities are equal, the most recently inserted segment wins.
 *
 * If [modulus] is provided, all intervals are clamped to the range `[0, modulus)`, making the
 * lookup cyclic. Without a modulus, intervals are treated as absolute times.
 */
open class DayLookupBuilder<T>(private val modulus: Duration? = null) {

    protected val segments = mutableListOf<Segment<T>>()


    /**
     * Returns a read-only view of the current list of segments.
     */
    fun segments(): List<Segment<T>> {
        return segments.toList()
    }


    /**
     * Adds a time interval to the builder.
     *
     * - Overlapping segments are spliced so that no overlaps remain.
     * - If an overlap occurs:
     *   - Segments with higher priority override the new one.
     *   - Segments with equal priority are overridden by the most recently added one.
     *
     * If [modulus] is defined, the input range is clamped to `[0, modulus)`.
     *
     * @param rawRange the half-open absolute time range for the segment
     * @param path the element to associate with the range
     * @param priority the segment's priority (higher = wins overlaps). Default = 0
     */
    fun set(rawRange: OpenEndRange<AbsoluteTime>, path: T, priority: Int = 0) {
        val range = modulus?.let {
            val start = rawRange.start.coerceAtLeast(0.toAbsoluteMinutes())
            val end = rawRange.endExclusive.coerceAtMost(it.sinceStart)
            start..<end
        } ?: rawRange
        if (range.isEmpty()) return

        val newSegment = Segment(range, path, priority)

        val overlappedSegments = segments.filter { it.intersects(range) }
        segments.removeAll(overlappedSegments)
        val (higher, lower) = overlappedSegments.partition { it.priority > newSegment.priority }
        segments.addAll(lower.flatMap { it.splice(range) })
        segments.add(newSegment)
        // Recursively add segments with higher priority, so that the current element is spliced accordingly
        higher.forEach {
            set(it.range, it.element, it.priority)
        }
        segments.sortBy { it.range.start }
    }

    fun set(segment: Segment<T>) = set(segment.range, segment.element, segment.priority)

    /**
     * Adds a collection of segments.
     * Equivalent to calling [set] for each segment in the collection.
     */
    fun insertAll(segments: Collection<Segment<T>>) {
        segments.forEach(::set)
    }

    /**
     * Adds a segment using a relative duration range.
     * Equivalent to calling [set] with the converted absolute range.
     */
    fun setDuration(relativeRange: OpenEndRange<Duration>, path: T, priority: Int = 0) =
        set(relativeRange.start.sinceStart..<relativeRange.endExclusive.sinceStart, path, priority)

    /**
     * Convenience operator for adding a segment with default priority (0).
     */
    operator fun set(a: Duration, b: Duration, path: T) = set(a.sinceStart..<b.sinceStart, path)

    /**
     * Convenience operator for adding a segment with an explicit priority.
     */
    operator fun set(a: Duration, b: Duration, path: Pair<T, Int>) =
        set(a.sinceStart..<b.sinceStart, path.first, path.second)

    /**
     * Convenience function to set a default interval with priority 0
     */
    fun setDefault(path: T) = setDefault(path to 0)

    /**
     * Convenience function to set a default interval with element and priority
     */
    fun setDefault(path: Pair<T, Int>) = set(0.hours, modulus ?: Duration.INFINITE, path)

    /**
     * Finalizes the builder and creates a [TimeLookup].
     *
     * Segments are sorted by their start times before building.
     * The resulting [TimeLookup] can be queried for elements at any absolute time.
     */
    open fun build(): TimeLookup<T> {
        return TimeLookup(
            segments.map { it.range.start.sinceStart }.toTypedArray(),
            segments.map { it.element },
            modulus
        )
    }

    override fun toString(): String {
        return segments.toString()
    }

}

/**
 * A [DayLookupBuilder] is a [DayLookupBuilder] with the modulus already fixed to 1 Day
 */
class DayTimeLookupBuilder<T> : DayLookupBuilder<T>(modulus = 1.days) {
    override fun build(): DayTimeLookup<T> {
        return DayTimeLookup(
            segments.map { it.range.start.sinceStart }.toTypedArray(),
            segments.map { it.element },
        )
    }
}