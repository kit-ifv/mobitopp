package domain.jackson.durationParsing

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Parser for the format of
 * `X`Days`X`Hours`X`Minutes`X`Seconds
 * (or any subset of that, so `X`Hours`X`Seconds would also be valid).
 *
 * Accepts strings like
 * ```
 * "-1Days4Hours"
 * "30Seconds"
 * "80Days 20Hours 3Minutes"
 * ```
 * Parsed durations are added together, so `1Days24Hours` will be the same as `2Days`.
 */
val SimpleTimeStrategy get() = ParameterizedDurationParseStrategy(
    "Days".toRegex(),
    "Hours".toRegex(),
    "Minutes".toRegex(),
    "Seconds".toRegex(),
)

val LowerCaseTimeStrategy get() = ParameterizedDurationParseStrategy(
    "days".toRegex(),
    "hours".toRegex(),
    "minutes".toRegex(),
    "seconds".toRegex(),
)

/**
 * Parser for the format of `X`d`X`h`X`m`X`s
 * (or any subset of that, so `X`d`X`h would also be valid).
 *
 * Accepts strings like
 * ```
 * "3d4h"
 * "-30s"
 * "80d 20h 300m"
 * ```
 * Parsed durations are added together, so `1d24h` will be the same as `2d`.
 */
val LowerCaseShortTimeStrategy get() = ParameterizedDurationParseStrategy(
    "d".toRegex(),
    "h".toRegex(),
    "m".toRegex(),
    "s".toRegex(),
)

val ShortTimeStrategy get() = ParameterizedDurationParseStrategy(
    "D".toRegex(),
    "H".toRegex(),
    "M".toRegex(),
    "S".toRegex(),
)

/**
 * Parser for the format
 * 1`days`1`hours`1`minutes`1`seconds`
 * (or any subset of that, so 4`minutes`5`seconds` would also be valid).
 * Also accepts a minus at the start (with braces around the duration) for negative durations and spaces anywhere.
 * So `-(1days1hours)` for negative durations.
 * @param days The pattern for days. Should only match what comes after the number of days.
 * @param hours The pattern for hours. Should only match what comes after the number of hours.
 * @param minutes The pattern for minutes. Should only match what comes after the number of minutes.
 * @param seconds The pattern for seconds. Should only match what comes after the number of seconds.
 */
class ParameterizedDurationParseStrategy(val days: Regex, val hours: Regex, val minutes: Regex, val seconds: Regex) :
    DurationParseStrategy {
    override val formatRegex: Regex = "^(\\d+$days)?(\\d+$hours)?(\\d+$minutes)?(\\d+$seconds)?".toRegex()
    private val negativeRegex: Regex = "^-\\((\\d+$days)?(\\d+$hours)?(\\d+$minutes)?(\\d+$seconds)?\\)".toRegex()
    private val ensureNotEmpty: Regex = "(\\d+$days)|(\\d+$hours)|(\\d+$minutes)|(\\d+$seconds)".toRegex()
    private val negativeDuration: Regex = "^-\\(.+\\)".toRegex()
    private val removeNegative: Regex = "^(-\\()|\\)".toRegex()
    private val unexpectedCall = "This should not have happened. supportsFormat() should not have accepted this format"
    override fun toString(): String = "1$days 2$hours 3$minutes 4$seconds"

    override fun supportsFormat(input: String): Boolean {
        val stripped = input.replace(" ", "")
        return (stripped.matches(formatRegex) || stripped.matches(negativeRegex)) && stripped.contains(ensureNotEmpty)
    }

    override fun parseDuration(input: String): Duration {
        val stripped = input.replace(" ", "")
        if (stripped.matches(negativeDuration)) {
            return -parsePositive(stripped.replace(removeNegative, ""))
        }
        return parsePositive(stripped)
    }

    /**
     * Expects input to be stripped of space and '-' characters and generally a valid duration string.
     */
    private fun parsePositive(input: String): Duration {
        var result = Duration.ZERO

        var rest = input
        var split = rest.split(days)
        if (split.size == 2) {
            result += split[0].toInt().days
            rest = split[1]
        } else if (split.size > 2) {
            error(unexpectedCall)
        }

        split = rest.split(hours)
        if (split.size == 2) {
            result += split[0].toInt().hours
            rest = split[1]
        } else if (split.size > 2) {
            error(unexpectedCall)
        }

        split = rest.split(minutes)
        if (split.size == 2) {
            result += split[0].toInt().minutes
            rest = split[1]
        } else if (split.size > 2) {
            error(unexpectedCall)
        }

        split = rest.split(seconds)
        if (split.size == 2) {
            result += split[0].toInt().seconds
        } else if (split.size > 2) {
            error(unexpectedCall)
        }

        return result
    }
}
