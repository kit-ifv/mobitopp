package domain.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import core.results.plots.data.Min
import domain.jackson.durationParsing.ShortTimeStrategy
import domain.jackson.durationParsing.SimpleTimeStrategy
import utils.units.AbsoluteTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


/**
 * Handles the serialization of kotlin durations and AbsoluteTime.
 */
val durationModule: SimpleModule get() = SimpleModule("Duration")
    .addDeserializer(Duration::class.java, DurationDeserializer())
    .addSerializer(Duration::class.java, DurationSerializer())
    .addDeserializer(AbsoluteTime::class.java, AbsoluteTimeDeserializer())
    .addSerializer(AbsoluteTime::class.java, AbsoluteTimeSerializer())


private class DurationDeserializer(val durationParser: DurationParser = DurationParser())
    : JsonDeserializer<Duration>() {
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): Duration? {
        if (p != null) {
            return durationParser.parseDuration(p.valueAsString)
        }
        return null
    }
}
private class DurationSerializer(val durationParser: DurationParser = DurationParser()) : JsonSerializer<Duration>() {
    override fun serialize(
        value: Duration?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        if (gen != null && value != null) {
            gen.writeString(durationParser.serialize(value))
        }
    }

}

private class AbsoluteTimeDeserializer(val durationParser: DurationParser = DurationParser())
    : JsonDeserializer<AbsoluteTime>() {
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): AbsoluteTime? {
        if (p != null) {
            return AbsoluteTime(durationParser.parseDuration(p.valueAsString))
        }
        return null
    }
}

private class AbsoluteTimeSerializer(val durationParser: DurationParser = DurationParser())
    : JsonSerializer<AbsoluteTime>() {
    override fun serialize(
        value: AbsoluteTime?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        if (gen != null && value != null) {
            gen.writeString(durationParser.serialize(value.sinceStart))
        }
    }
}

class DurationParser(
    val strategies: List<DurationParseStrategy> = listOf(
        SimpleTimeStrategy,
        ShortTimeStrategy
    )
){

    fun parseDuration(input: String): Duration {
        for (strategy in strategies) {
            if (strategy.supportsFormat(input)) return strategy.parseDuration(input)
        }
        error("Unsupported time format. Got '$input' as input. Supported formats are $strategies")
    }
    fun serialize(duration: Duration): String {
        return duration.toString()
    }
}

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