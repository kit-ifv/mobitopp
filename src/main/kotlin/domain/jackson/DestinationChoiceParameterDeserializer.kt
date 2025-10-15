package domain.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import domain.simulation.behavior.DestinationChoiceParameters

class DestinationChoiceParameterRepo: SimpleModule("DestinationChoice"){
    init {
        addDeserializer(DestinationChoiceParameters().javaClass, DestinationChoiceDeserializer)
    }
}

val DestinationChoiceDeserializer = GenericKeyValueDeserializer(
    DestinationChoiceParameters().javaClass,
    mapOf("default" to DestinationChoiceParameters())
)