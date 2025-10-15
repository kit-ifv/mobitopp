package domain.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.VisumMatrixCreator
import domain.shared.datastructure.matrix.ZoneMatrixCreation

/**
 * Module for deserialization of ZoneMatrixCreation methods.
 */
class ZoneMatrixCreation: SimpleModule("MatrixCreation") {
    init {
        addDeserializer(ZoneMatrixCreation::class.java, MatrixDeserializer )
    }
}

val MatrixDeserializer =
    GenericKeyValueDeserializer(
        ZoneMatrixCreation::class.java,
        /* Register mappings here, to make them available in yaml configs.*/
        mapOf(
            "visum" to VisumMatrixCreator,
            "keyBased" to KeyBasedMatrixCreation
        )
    )
