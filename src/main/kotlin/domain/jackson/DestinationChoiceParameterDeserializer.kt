package domain.jackson

import domain.simulation.behavior.DestinationChoiceParameters


val DestinationChoiceParameterModule = GenericKeyValueBuilder(
    DestinationChoiceParameters::class.java,
    mapOf("default" to DestinationChoiceParameters()),
    loadFromSubmodules = true
).getModule()
