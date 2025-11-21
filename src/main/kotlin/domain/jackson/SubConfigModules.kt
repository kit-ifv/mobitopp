package domain.jackson

import application.config.subconfigs.CSVConfig
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule

val csvConfigModule = SimpleModule("csvConfigModule").apply {
    addDeserializer(CSVConfig::class.java, CustomCSVConfigSerializer() )
    // serialization is straight forward.
}

class CustomCSVConfigSerializer(): JsonDeserializer<CSVConfig>() {
    override fun deserialize(
        p0: JsonParser?,
        p1: DeserializationContext?
    ): CSVConfig? {
        if (p0 == null) return null

        if (p0.currentToken == JsonToken.START_OBJECT) {
            p0.nextToken()
        }
        do {
            if (p0.currentToken == JsonToken.FIELD_NAME) {}
            givenParams[p0.currentName()] =  p0.valueAsString

        } while (p0.nextToken() != JsonToken.END_OBJECT)

        TODO("Not yet implemented")
    }

}