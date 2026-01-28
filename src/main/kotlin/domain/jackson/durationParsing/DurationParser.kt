package domain.jackson.durationParsing

import kotlin.time.Duration

/**
 * On default parses any string of similar form of
 * ```
 * 2Days3Minutes
 * 2d 4h 30s
 * 40days 60seconds
 * -(50M 40S)
 * -(40 seconds)
 * -(70Days80Minutes1Seconds)
 * ```
 * to a `Duration`. Spaces are ignored. Negative durations are possible.
 * @param strategies The strategies which are used to parse an input string. Default is described above.
 */
class DurationParser(
    val strategies: List<DurationParseStrategy> = listOf(
        SimpleTimeStrategy,
        LowerCaseShortTimeStrategy,
        LowerCaseTimeStrategy,
        ShortTimeStrategy
    )
) {

    fun parseDuration(input: String): Duration {
        for (strategy in strategies) {
            if (strategy.supportsFormat(input)) return strategy.parseDuration(input)
        }
        error(
            "Unsupported time format. Got '$input' as input. Supported formats are something like $strategies \n" +
                "For negative durations surround the duration with -() like -(24Hours). Space are ignored."
        )
    }

    /**
     * Returns the `duration` in the format of XDays YHours ZMinutes KSeconds. Integer valued and optionally
     * surrounded by `-( )` for negative durations. If `duration` is 0, then `0Seconds` is returned.
     */
    fun serialize(duration: Duration): String {
        var result = ""
        var end = ""
        if (duration.isNegative()) {
            result += "-("
            end = ")"
        }

        duration.absoluteValue.toComponents { days, hours, minutes, seconds, _ ->
            @Suppress("ComplexCondition")
            if (days == 0L && hours == 0 && minutes == 0 && seconds == 0) {
                return "0Seconds"
            }

            if (days != 0L) result += days.toString() + "Days "
            if (hours != 0) result += hours.toString() + "Hours "
            if (minutes != 0) result += minutes.toString() + "Minutes "
            if (seconds != 0) result += seconds.toString() + "Seconds "

            return result.substring(0, result.length - 1) + end
        }
    }
}
