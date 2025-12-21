package domain.jackson.durationParsing

import kotlin.collections.iterator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SimpleTimeStrategyTest {
    val examples = mapOf(
        "1Days" to 1.days,
        "2Days" to 2.days,
        "10Hours" to 10.hours,
        "24Hours" to 1.days,
        "3Hours" to 3.hours,
        "0Hours" to 0.hours,
        "20Minutes" to 20.minutes,
        "60Minutes" to 60.minutes,
        "60Minutes" to 1.hours,
        "10Seconds" to 10.seconds,
        "0Seconds" to 0.seconds,
        "60Seconds" to 60.seconds,
        "60Seconds" to 1.minutes,
        "1Days3Hours10Minutes2Seconds" to 1.days + 3.hours + 10.minutes + 2.seconds,
        "1Days10Minutes2Seconds" to 1.days + 10.minutes + 2.seconds,
        "1Days2Seconds" to 1.days + 2.seconds,
        "1Hours2Seconds" to 1.hours + 2.seconds,
        "100Hours66Seconds" to 100.hours + 66.seconds,
        "99 Days 3Hours 10 Minutes 2 Seconds" to 99.days + 3.hours + 10.minutes + 2.seconds,
        " 1 Ho u   rs       200 S e c o n d s  " to 1.hours + 200.seconds, // parsing is done without spaces
    )

    val antiExamples = listOf(
        "",
        "1",
        "Hours",
        "Minutes",
        "Seconds",
        "Days",
        " ",
        "2Days4Days", // days twice
        "3Hours4Days", // wrong order
        "20Minutes4Days", // wrong order
        "10Seconds4Days3Hours10Minutes2Seconds", // wrong order
        "10Seconds4Days3Hours10Minutes", // wrong order
        "Hours3Minutes",
        "-Hours",
        "-Minutes",
        "-Seconds",
        "-Days",
        "99 Days  -3Hours 10 Minutes 2 Seconds", // - only at the start allowed
        "99 Days 3Hours 10 Minutes 2 Seconds #", // no other characters allowed
    )

    @Test
    fun shouldNotMatch() {
        val strategy = SimpleTimeStrategy

        for (example in antiExamples) {
            assert(!strategy.supportsFormat(example))
        }
    }

    @Test
    fun shouldParse() {
        val strategy = SimpleTimeStrategy

        for (example in examples) {
            assert(strategy.supportsFormat(example.key)) { "Following string was not accepted ${example.key}" }
            assertEquals(example.value, strategy.parseDuration(example.key))
        }
    }

    @Test
    fun negativeValues() {
        val strategy = SimpleTimeStrategy

        for (example in examples) {
            assert(strategy.supportsFormat(example.key)) { "Following string was not accepted ${example.key}" }
            assertEquals(-example.value, strategy.parseDuration(" - " + example.key))
        }
    }
}
