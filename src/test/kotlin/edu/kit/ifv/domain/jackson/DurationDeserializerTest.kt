@file:Suppress("FunctionNameMaxLength")

package edu.kit.ifv.domain.jackson
import edu.kit.ifv.application.config.Yaml
import edu.kit.ifv.domain.jackson.durationParsing.DurationParser
import edu.kit.ifv.utils.units.AbsoluteTime
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationDeserializerTest {
    val durationTestMap = mapOf(
        "1Days 3Hours 5Minutes" to 1.days + 3.hours + 5.minutes,
        "120Days 2Hours 5Minutes" to 120.days + 2.hours + 5.minutes,
        "24Hours" to 1.days,
        "23Hours5Minutes" to 23.hours + 5.minutes,
        "40Days1Seconds" to 40.days + 1.seconds,
        "120d 2h 5m" to 120.days + 2.hours + 5.minutes,
        "5m" to 5.minutes,
        "40M 50S" to 40.minutes + 50.seconds,
        "120days 30hours" to 120.days + 30.hours,
        "-(24 hours 5 minutes)" to -(24.hours + 5.minutes),
        "2Days3Minutes" to 2.days + 3.minutes,
        "2d 4h 30s" to 2.days + 4.hours + 30.seconds,
        "40days 60seconds" to 40.days + 60.seconds,
        "- (50M 40S)" to -(50.minutes + 40.seconds),
        "-(40seconds)" to (-40).seconds,
        "-(70Days80Minutes1Seconds)" to -(70.days + 80.minutes + 1.seconds),
        "-( 9999h 5m)" to -(9999.hours + 5.minutes),
    )

    @Test
    fun mixedFormatParsing() {
        val parser = DurationParser()
        for ((durationString, duration) in durationTestMap) {
            assertEquals(duration, parser.parseDuration(durationString))
        }
    }

    @Test
    fun serializeDeserialize() {
        val tempFile = Path("src/test/resources/tempOutput/temp.yaml")
        tempFile.createParentDirectories()
        val parser = DurationParser()
        for ((durationString, duration) in durationTestMap) {
            assertEquals(duration, parser.parseDuration(durationString))
            Yaml.writeYaml(tempFile, duration)
            val parsed = Yaml.readYaml<Duration>(tempFile)
            assertEquals(duration, parsed)
            assertEquals(parser.parseDuration(durationString), parsed)
        }
        tempFile.deleteIfExists()
    }

    @Test
    fun serializeDeserializeAbsoluteTime() {
        val tempFile = Path("src/test/resources/tempOutput/temp.yaml")
        tempFile.createParentDirectories()
        val parser = DurationParser()
        for ((durationString, duration) in durationTestMap) {
            assertEquals(
                AbsoluteTime(duration),
                AbsoluteTime(parser.parseDuration(durationString)),
            )
            Yaml.writeYaml(tempFile, AbsoluteTime(duration))
            val parsed = Yaml.readYaml<AbsoluteTime>(tempFile)
            assertEquals(AbsoluteTime(duration), parsed)
            assertEquals(AbsoluteTime(parser.parseDuration(durationString)), parsed)
        }
        tempFile.deleteIfExists()
    }
}
