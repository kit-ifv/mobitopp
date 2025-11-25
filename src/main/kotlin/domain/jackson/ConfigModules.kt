package domain.jackson

import application.config.subconfigs.CSVConfig
import application.config.subconfigs.MatrixConfig
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * A MatrixConfig has multiple constructors. Jackson is not able to handle that, therefore we need to define a custom
 * deserializer for MatrixConfig's.
 */
val MatrixConfigModule = SimpleModule("MatrixConfigModule").apply {
    addDeserializer(MatrixConfig::class.java, InitializableDeserializer(MatrixConfig))
}

/**
 * A CSVConfig has multiple constructors. The Jackson JSON parser is not able to handle that, therefore we declare a
 * Module for deserialization of CSVConfigs. The serialization does not need to be altered as the default serialization
 * representation of a CSVConfig is properly handled by this custom deserializer.
 */
val CSVConfigModule = SimpleModule("CSVConfigModule").apply {
    addDeserializer(CSVConfig::class.java, InitializableDeserializer(CSVConfig))
}

interface JSONInitializer<T> {
    fun getParameterNames(): Set<String>
    fun init(givenParams: Map<String, String>): T
}

/**
 * A deserializer that retrieves all parameters the 'initializer' specifies and constructs whatever the initializer
 * does with the retrieved parameters.
 */
class InitializableDeserializer<T>(val initializer: JSONInitializer<T>): JsonDeserializer<T>() {
    override fun deserialize(
        p0: JsonParser?,
        p1: DeserializationContext?
    ): T? {
        if (p0 == null || p1 == null) return null
        if (p0.currentToken == JsonToken.START_OBJECT) {
            p0.nextToken()
        }
        val objectNode = p0.codec.readTree<JsonNode>(p0)
        val givenParams = mutableMapOf<String, String>()
        val parameterNames = initializer.getParameterNames()
        for(name in parameterNames) {
            if (objectNode.get(name) != null)
                givenParams[name] = objectNode.get(name).textValue()
        }
        return initializer.init(givenParams)
    }
}
