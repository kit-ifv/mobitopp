package domain.jackson

import domain.simulation.behavior.DestinationChoiceParameters


val DestinationChoiceParameterModule = GenericKeyValueBuilder(
    DestinationChoiceParameters().javaClass,
    mapOf("default" to DestinationChoiceParameters()),
    loadFromSubmodules = true
).getModule()
