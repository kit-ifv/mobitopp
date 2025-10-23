package domain.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.ServiceLoader
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Provides a simple deserializing from string to instance.
 * Collects all mappings from strings to T from subprojects through the ServiceLoader
 * API. (Collects all Repo<T> instances that are declared in a subproject).
 *
 * @param default default mappings from strings to their appropriate translation.
 * @param wraps parameter to declare which class this Deserializer wraps. Needed for the collection of Repo<T>
 *     instances.
 */
class GenericKeyValueDeserializer<T>(
    val wraps: Class<T>,
    val default: Map<String, T> = emptyMap(),
    val loadFromSubmodules: Boolean
) : JsonDeserializer<T>() {

    val deserializers: Map<String, T> by lazy {
        collectDeserializers()
    }

    /**
     * Collects all deserializers of subprojects and the default ones.
     */
    private fun collectDeserializers(): Map<String, T> {
        val result = mutableMapOf<String, T>()
        result.putAll(default)
        // 2. Discover and register from subprojects via ServiceLoader
        if (loadFromSubmodules) {
            ServiceLoader.load(Repo::class.java)
                .forEach { parameterRepo ->
                    if (parameterRepo.wraps == wraps) {
                        println("Loading ${parameterRepo.name}")
                        parameterRepo.addAllPairs(result)
                    }
                }
        }
        return result.toMap()
    }

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?
    ): T {
        val node = p.codec.readTree<JsonNode>(p)

        val type = when {
            node.isTextual -> node.asText()
            else -> node.get("type").asText()
        }
        return deserializers[type] ?: error("Unknown type: $type. Can't deserialize $type. " +
            "Known types: ${deserializers.keys}")
    }
}

/**
 * Provides a simple deserializing from string to instance.
 * Collects all mappings from strings to T from subprojects through the ServiceLoader
 * API. (Collects all Repo<T> instances that are declared in a subproject).
 *
 * @param default default mappings from strings to their appropriate translation.
 * @param wraps parameter to declare which class this Deserializer wraps. Needed for the collection of Repo<T>
 *     instances.
 */
class GenericKeyValueSerializer<T>(
    val wraps: Class<T>,
    val default: Map<T, String> = emptyMap(),
    val loadFromSubmodules: Boolean
) : JsonSerializer<T>() {

    val serializerMapping: Map<T, String> by lazy {
        collectSerializers()
    }

    /**
     * Collects all deserializers of subprojects and the default ones.
     */
    private fun collectSerializers(): Map<T, String> {
        val result = mutableMapOf<T, String>()
        result.putAll(default)
        // 2. Discover and register from subprojects via ServiceLoader
        if (loadFromSubmodules) {
            ServiceLoader.load(Repo::class.java)
                .forEach { parameterRepo ->
                    if (parameterRepo.wraps == wraps) {
                        println("Loading ${parameterRepo.name}")
                        parameterRepo.addPairsReversed(result)
                    }
                }
        }
        return result.toMap()
    }

    override fun serialize(
        value: T?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        if (gen != null && value != null) {
            val out = serializerMapping[value] ?: error("Unknown class: $value")
            gen.writeString(out)
        }
    }
}

interface Repo<T> {
    val name: String
    val wraps: Class<T>
    fun getParameterSets(): Map<String, T>
}

fun<T> Repo<*>.addAllPairs(map: MutableMap<String, T>) {
    getParameterSets().forEach { (key, value) ->
        val existing = map.putIfAbsent(key, value as T)
        if (existing != null) {
            error(
                "Duplicate Key-Value-Pair with key '$key' encountered. From " +
                    "Repo with name '$name'"
            )
        }
    }
}

fun<T> Repo<*>.addPairsReversed(map: MutableMap<T, String>) {
    getParameterSets().forEach { (key, value) ->
        val existing = map.putIfAbsent(value as T, key)
        if (existing != null) {
            error(
                "Duplicate Key-Value-Pair with key '$key' encountered. From " +
                        "Repo with name '$name'"
            )
        }
    }
}
