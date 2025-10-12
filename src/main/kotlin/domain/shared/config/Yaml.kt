package domain.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.jackson.CoreCodePlanModule
import domain.jackson.DestinationChoiceParameterModule
import domain.jackson.ZoneMatrixCreation
import java.nio.file.Path

/**
 * To register new json mappers/parser in a subproject create a directory META-INF/services/
 * in src/main/resources/ of the subproject.
 */
object Yaml {
    val mapper = ObjectMapper(YAMLFactory())
        .registerKotlinModule()
        .registerModule(CoreCodePlanModule())
        .registerModule(ZoneMatrixCreation())
        .registerModule(DestinationChoiceParameterModule())
        .findAndRegisterModules()

    inline fun <reified T> readYaml(path: Path): T {
        val file = path.toFile()
        return mapper.readValue(file)
    }
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))
}
