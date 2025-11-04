package domain.jackson

import domain.simulation.behavior.DestinationChoiceParameters

val CoreDestinationChoiceParameterModule = GenericKeyValueBuilder(
    DestinationChoiceParameters::class.java,
    mapOf("default" to DestinationChoiceParameters()),
    loadFromSubmodules = true
).getModule()
