package domain.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

interface JSONInitializer<T> {
    fun getParameterNames(): Set<String>
    fun init(givenParams: Map<String, String>): T
}

/**
 * A deserializer that retrieves all parameters the 'initializer' specifies and constructs whatever the initializer
 * does with the retrieved parameters.
 */
class InitializableDeserializer<T>(val initializer: JSONInitializer<T>) : JsonDeserializer<T>() {
    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): T? {
        if (p0 == null || p1 == null) return null
        if (p0.currentToken == JsonToken.START_OBJECT) {
            p0.nextToken()
        }
        val objectNode = p0.codec.readTree<JsonNode>(p0)
        val givenParams = mutableMapOf<String, String>()
        val parameterNames = initializer.getParameterNames()
        for (name in parameterNames) {
            if (objectNode.get(name) != null) {
                givenParams[name] = objectNode.get(name).textValue()
            }
        }
        return initializer.init(givenParams)
    }
}
