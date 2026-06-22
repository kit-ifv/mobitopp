package edu.kit.ifv.domain.jackson
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import edu.kit.ifv.domain.jackson.durationParsing.DurationParser
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.time.Duration

/**
 * Handles the serialization of kotlin durations and AbsoluteTime.
 */
val DurationModule: SimpleModule get() = SimpleModule("Duration")
    .addDeserializer(Duration::class.java, DurationDeserializer())
    .addSerializer(Duration::class.java, DurationSerializer())
    .addDeserializer(AbsoluteTime::class.java, AbsoluteTimeDeserializer())
    .addSerializer(AbsoluteTime::class.java, AbsoluteTimeSerializer())

private class DurationDeserializer(val durationParser: DurationParser = DurationParser()) :
    JsonDeserializer<Duration>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Duration? {
        if (p != null) {
            return durationParser.parseDuration(p.valueAsString)
        }
        return null
    }
}
private class DurationSerializer(val durationParser: DurationParser = DurationParser()) : JsonSerializer<Duration>() {
    override fun serialize(value: Duration?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (gen != null && value != null) {
            gen.writeString(durationParser.serialize(value))
        }
    }
}

private class AbsoluteTimeDeserializer(val durationParser: DurationParser = DurationParser()) :
    JsonDeserializer<AbsoluteTime>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AbsoluteTime? {
        if (p != null) {
            return AbsoluteTime(durationParser.parseDuration(p.valueAsString))
        }
        return null
    }
}

private class AbsoluteTimeSerializer(val durationParser: DurationParser = DurationParser()) :
    JsonSerializer<AbsoluteTime>() {
    override fun serialize(value: AbsoluteTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (gen != null && value != null) {
            gen.writeString(durationParser.serialize(value.sinceStart))
        }
    }
}
