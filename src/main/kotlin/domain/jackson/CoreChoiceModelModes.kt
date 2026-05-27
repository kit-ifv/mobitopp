package domain.jackson

import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.legacyChoiceModelModes

val CoreChoiceModelModes = GenericKeyValueBuilder(
    javaType(ChoiceModelModes::class.java),
    default = mapOf(
        "legacyChoiceModelModes" to legacyChoiceModelModes,
    ),
    loadFromSubmodules = false, // submodules probably have other choiceModelModes
).getModule()
