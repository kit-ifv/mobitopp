package domain.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path

object Yaml {
    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    inline fun <reified T> readYaml(path: Path): T {
        val file = path.toFile()
        return mapper.readValue(file)
    }
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))
}
