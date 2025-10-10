package domain.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.simulation.behavior.DestinationChoiceParameters
import java.util.ServiceLoader
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Module for deserialization of DestinationChoiceParameters methods.
 */
class DestinationChoiceParameterModule: SimpleModule("DestinationChoiceParameters") {
    init {
        addDeserializer(DestinationChoiceParameters::class.java, DestinationChoiceParameterDeserializer() )
    }
}

/**
 * 
 */
class DestinationChoiceParameterDeserializer: JsonDeserializer<DestinationChoiceParameters>() {
    /**
     * Add new/other DestinationChoiceParametersMethods to this map.
     * If you want to create new ones in a subproject, this structure needs to be changed. Currently only methods in
     * reengineering (here) can be registered and used.
     */
    val deserializers: Map<String, DestinationChoiceParameters> by lazy {
       collectParameterSets()
    }

    /**
     * Collects all parameter sets of subprojects and ofc. the default one.
     */
    private fun collectParameterSets() : Map<String, DestinationChoiceParameters>  {
        val result = mutableMapOf(
            "default" to DestinationChoiceParameters(),
        )
        // 2. Discover and register from subprojects via ServiceLoader
        ServiceLoader.load(DestinationChoiceParameterRepo::class.java)
            .forEach { parameterRepo ->
                parameterRepo.getParameterSets().forEach { (key, value) ->
                    val existing = result.putIfAbsent(key, value)
                    if (existing != null) {
                        error("Duplicate DestinationChoiceParameter instance with name '$key' encountered. From " +
                                "DestinationChoiceRepo with name '${parameterRepo.name}'")
                    }
                }
            }
        return result.toMap()
    }

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?
    ): DestinationChoiceParameters? {
        val node = p.codec.readTree<JsonNode>(p)

        val type = when {
            node.isTextual -> node.asText()
            else -> node.get("type").asText()
        }
        return deserializers[type] ?: error("Unknown type: $type. Can't deserialize $type. Known types: ${deserializers.keys}")

    }
}

/**
 * If you are a subproject wanting to register a custom parameter set (to be able to use it in the yaml config), you need at least one instance of this interface.
 * Register your implementation in the subproject in src/main/resources/META-INF.services/domain.jackson.DestinationChoiceParameterRepo
 * This file should contain the package-path and class-name of your instance of this interface.
 *
 * For example file-content: 'sub.project.package.structure.myDestinationChoiceRepoInMySubproject'
 */
interface DestinationChoiceParameterRepo {
    val name: String
    /**
     * This method should return all registered DestinationChoiceParameters.
     * The map should map the name of the parameters to well an instance of it. The name is the one that can be used in
     * the yaml config.
     */
    fun getParameterSets(): Map<String, DestinationChoiceParameters>
}