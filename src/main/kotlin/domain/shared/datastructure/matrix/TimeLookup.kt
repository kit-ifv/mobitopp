package domain.shared.datastructure.matrix

import edu.kit.ifv.mobitopp.actitoppNG.utils.smallerIndex
import utils.units.AbsoluteTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * A potentially cyclic lookup that returns the element T that would be considered "active" for a given absolute time
 * as input. The time indices are the "flip points" where the lookup changes to a different element.
 */
open class TimeLookup<T>(
    private val timeIndices: Array<Duration>,
    val elements: List<T>,
    val modulus: Duration?,
) {

    /**
     * Returns the element active at the given [absoluteTime].
     *
     * The lookup finds the largest time point ≤ [absoluteTime] (converted to a relative time
     * using [modulus], if present), and returns the associated element.
     *
     * @throws IllegalArgumentException if [elements] is empty, or if no flip point exists
     *         before the given [absoluteTime].
     */
    operator fun get(absoluteTime: AbsoluteTime): T {
        val time = absoluteTime.relativeTime()
        val indexBinarySearch = timeIndices.binarySearch(time).smallerIndex()
        require(elements.isNotEmpty()) {
            "The requested time $absoluteTime landed in an empty Time Lookup. If this happens you have no default" +
                    "defined and the simulation tried to access a day that is not specified."
        }
        require(indexBinarySearch >= 0) {
            "The structure has no time for the given entry $absoluteTime"
        }

        return elements[indexBinarySearch]
    }

    /**
     * Convenience function to locate the first change of this lookup from the start. Used for example when a higher
     * data structure has found no change, and tries to find a change in a future time lookup
     */

    fun findNextChangeTime(element: T): Duration? = findNextChangeTime(element, Duration.ZERO)

    /**
     *
     * @see [findNextChangeTime]
     * The absolute time converted to a relative time matching this lookup.
     */
    fun findNextChangeTime(element: T, skipUntil: AbsoluteTime) =
        findNextChangeTime(element, skipUntil.relativeTime())

    /**
     * Returns the first relative time (read duration) where this lookup would return a different result than the
     * target element - if there exists a later entry. This lookup does not cycle through the entries.
     * @param skipUntil a relative offset where the lookup should start.
     */
    fun findNextChangeTime(element: T, skipUntil: Duration): Duration? {
        val optionalIndex =
            elements.withIndex().firstOrNull {timeIndices[it.index] >= skipUntil && it.value != element }?.index
        if (optionalIndex == null) return null
        return timeIndices[optionalIndex]
    }


    /**
     * Get the relative time applicable to this lookup. If the modulus is 1 day, then an absolute time of 1d 1h should
     * be considered 1h for the purposes of the lookup
     */
    private fun AbsoluteTime.relativeTime(): Duration =
        modulus?.let { this % it } ?: this.sinceStart

    override fun toString(): String {
        val durationIntervals = timeIndices.toList().zipWithNext { a, b -> "[$a, $b)" } + "[${timeIndices.last()},1d)"
        return durationIntervals.zip(elements).joinToString { "${it.first}: ${it.second}" }
    }
}

/**
 * A [DayTimeLookup] is a [TimeLookup] with the modulus already fixed to 1 Day
 */
class DayTimeLookup<T>(timeIndices: Array<Duration>, elements: List<T>) : TimeLookup<T>(timeIndices, elements, 1.days)