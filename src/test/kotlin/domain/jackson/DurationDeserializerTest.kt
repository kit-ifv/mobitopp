package domain.jackson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationDeserializerTest {
    val durationTestMap = mapOf(
        "1Days 3Hours 5Minutes" to 1.days + 3.hours + 5.minutes,
        "120Days2hours5Minutes" to 120.days + 2.hours + 5.minutes,
        "24Hours" to 1.days,
        "23Hours5Minutes" to 23.hours + 5.minutes,
        "40Days1Seconds" to 40.days + 1.seconds
    )

    @Test
    fun `deserialize duration`() {
        val parser = DurationParser()
        for (expected in durationTestMap) {
            assertEquals(parser.parseDuration(expected.key), expected.value)
        }
    }
}