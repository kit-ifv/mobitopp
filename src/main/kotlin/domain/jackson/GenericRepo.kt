package domain.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import java.util.ServiceLoader
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Registers custom deserializers through the service loader api to the yaml parsing.
 */
class RepoFactory<T>(val name: String, containedClass: Class<T>, deserializer: JsonDeserializer<T> ): SimpleModule(name) {
    init {
        addDeserializer(containedClass, deserializer)
    }
}

/**
 * Collects all mappings from strings to T from subprojects through the ServiceLoader
 * API.
 */
class GenericKeyValueDeserializer<T>(val wraps: Class<T>, val default: Map<String, T> = emptyMap()): JsonDeserializer<T>() {

    val deserializers: Map<String, T> by lazy {
        collectParameterSets()
    }

    /**
     * Collects all parameter sets of subprojects and ofc. the default one.
     */
    private fun collectParameterSets() : Map<String, T>  {
        val result = mutableMapOf<String, T>()
        result.putAll(default)
        // 2. Discover and register from subprojects via ServiceLoader
        ServiceLoader.load(Repo::class.java)
            .forEach { parameterRepo ->
                println("Loading ${parameterRepo.name}")
                if(parameterRepo.wraps == wraps) {
                    parameterRepo.addAllPairs(result)
                }
            }
        return result.toMap()
    }

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?
    ): T? {
        val node = p.codec.readTree<JsonNode>(p)

        val type = when {
            node.isTextual -> node.asText()
            else -> node.get("type").asText()
        }
        return deserializers[type] ?: error("Unknown type: $type. Can't deserialize $type. Known types: ${deserializers.keys}")

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
            error("Duplicate Key-Value-Pair with key '$key' encountered. From " +
                    "Repo with name '${name}'")
        }
    }
}