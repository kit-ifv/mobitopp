package edu.kit.ifv.domain.jackson
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelModes

val CoreChoiceModelModes = GenericKeyValueBuilder(
    javaType(ChoiceModelModes::class.java),
    default = mapOf(
        "legacyChoiceModelModes" to legacyChoiceModelModes,
    ),
    loadFromSubmodules = false, // submodules probably have other choiceModelModes
).getModule()
