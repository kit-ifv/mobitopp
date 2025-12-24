package domain.jackson.durationParsing

import kotlin.collections.iterator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ShortTimeStrategyTest {
    val examples = mapOf(
        "1d" to 1.days,
        "2d" to 2.days,
        "10h" to 10.hours,
        "24h" to 1.days,
        "48h" to 2.days,
        "3h" to 3.hours,
        "0h" to 0.hours,
        "20m" to 20.minutes,
        "60m" to 60.minutes,
        "60m" to 1.hours,
        "10s" to 10.seconds,
        "0s" to 0.seconds,
        "0d0h0m0s" to 0.seconds,
        "60s" to 60.seconds,
        "60s" to 1.minutes,
        "10000d" to 10000.days,
        "1d3h10m2s" to 1.days + 3.hours + 10.minutes + 2.seconds,
        "1d10m2s" to 1.days + 10.minutes + 2.seconds,
        "1d2s" to 1.days + 2.seconds,
        "1h2s" to 1.hours + 2.seconds,
        "99 d 3h 10 m 2 s" to 99.days + 3.hours + 10.minutes + 2.seconds,
        " 1 h    3   m   200 s  " to 1.hours + 3.minutes + 200.seconds, // parsing is done without spaces
    )

    val antiExamples = listOf(
        "",
        "1",
        "h",
        "m",
        "s",
        "d",
        " ",
        " -",
        "2d4d", // days twice
        "3h4d", // wrong order
        "20m4d", // wrong order
        "3h20m4s4d", // wrong order
        "300h0d20m4s", // wrong order
        "10s4d3h10m2s", // wrong order
        "10s4d3h10m", // wrong order
        "h3m",
        "-h",
        "-m",
        "-s",
        "-d",
        "()",
        "-(x)",
        "-(1Days)",
        "-(10s4d3h10m)", // wrong order
        "- 1d10m2s", // only with surrounding parentheses allowed
        "-(h)",
        "-(m)",
        "-(s)",
        "-(d)",
        "99 d  - (3h 10 m 2 s)", // - only at the start allowed
        "99 d 3h 10 m 2 s #", // no other characters allowed
    )

    @Test
    fun shouldNotMatch() {
        val strategy = ShortTimeStrategy

        for (example in antiExamples) {
            assert(!strategy.supportsFormat(example))
        }
    }

    @Test
    fun shouldParse() {
        val strategy = ShortTimeStrategy

        for (example in examples) {
            assert(strategy.supportsFormat(example.key)) { "Following string was not accepted ${example.key}" }
            assertEquals(example.value, strategy.parseDuration(example.key))
        }
    }

    @Test
    fun negativeValues() {
        val strategy = ShortTimeStrategy

        for (example in examples) {
            assert(strategy.supportsFormat(example.key)) { "Following string was not accepted ${example.key}" }
            assertEquals(-example.value, strategy.parseDuration(" -( " + example.key + ")"))
        }
    }
}
