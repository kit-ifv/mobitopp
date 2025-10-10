package domain.jackson


import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.VisumMatrixCreator
import domain.shared.datastructure.matrix.ZoneMatrixCreation

/**
 * Module for deserialization of ZoneMatrixCreation methods.
 */
class ZoneMatrixCreation: SimpleModule("MatrixCreation") {
    init {
        addDeserializer(ZoneMatrixCreation::class.java, MatrixDeserializer() )
    }
}

/**
 * All deserializable ZoneMatrixCreation-Methods, that can be used in the Yaml configuration, need to be registered here.
 */
class MatrixDeserializer: JsonDeserializer<ZoneMatrixCreation>() {
    /**
     * Add new/other ZoneMatrixCreationMethods to this map.
     * If you want to create new ones in a subproject, this structure needs to be changed. Currently only methods in
     * reengineering (here) can be registered and used.
     */
    val deserializers: Map<String, ZoneMatrixCreation> = mapOf(
        "visum" to VisumMatrixCreator,
        "keyBased" to KeyBasedMatrixCreation
    )

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?
    ): ZoneMatrixCreation? {
        val node = p.codec.readTree<JsonNode>(p)

        val type = when {
            node.isTextual -> node.asText()
            else -> node.get("type").asText()
        }
        return deserializers[type] ?: error("Unknown type: $type. Can't deserialize $type. Known types: ${deserializers.keys}")

    }
}