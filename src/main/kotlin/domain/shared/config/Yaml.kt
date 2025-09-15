package domain.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.simulation.config.ShortTermConfig
import java.io.File
import java.nio.file.Path

object Yaml {
    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    inline fun <reified T> readYaml(file: File): T {
        return mapper.readValue(file)
    }
    inline fun <reified T> readYaml(path: Path) = readYaml<T>(path.toFile())
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))
}

