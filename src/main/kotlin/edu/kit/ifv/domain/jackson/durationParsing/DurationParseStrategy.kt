package edu.kit.ifv.domain.jackson.durationParsing
import kotlin.time.Duration

/**
 * Strategy interface for parsing
 */
interface DurationParseStrategy {
    val formatRegex: Regex

    /**
     * Should print what format the strategy supports.
     */
    override fun toString(): String
    fun supportsFormat(input: String): Boolean = input.matches(formatRegex)
    fun parseDuration(input: String): Duration
}
