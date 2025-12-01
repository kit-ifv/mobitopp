package domain.jackson

import application.config.subconfigs.CSVConfig
import application.config.subconfigs.MatrixConfig
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * A MatrixConfig has multiple constructors. Jackson is not able to handle that, therefore we need to define a custom
 * deserializer for MatrixConfig's.
 */
val MatrixConfigModule = SimpleModule("MatrixConfigModule").apply {
    addDeserializer(MatrixConfig::class.java, InitializableDeserializer(MatrixConfig))
}

/**
 * A CSVConfig has multiple constructors. The Jackson JSON parser is not able to handle that, therefore we declare a
 * Module for deserialization of CSVConfigs. The serialization does not need to be altered as the default serialization
 * representation of a CSVConfig is properly handled by this custom deserializer.
 */
val CSVConfigModule = SimpleModule("CSVConfigModule").apply {
    addDeserializer(CSVConfig::class.java, InitializableDeserializer(CSVConfig))
}
