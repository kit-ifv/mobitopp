package domain.jackson

import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.VisumMatrixCreator
import domain.shared.datastructure.matrix.ZoneMatrixCreation

/**
 * Module for deserialization of ZoneMatrixCreation methods.
 */
val CoreZoneMatrixCreationModule = GenericKeyValueBuilder(
    javaType(ZoneMatrixCreation::class.java),
    /* Register mappings here, to make them available in yaml configs.*/
    mapOf(
        "visum" to VisumMatrixCreator,
        "keyBased" to KeyBasedMatrixCreation
    ),
    loadFromSubmodules = true
).getModule()
