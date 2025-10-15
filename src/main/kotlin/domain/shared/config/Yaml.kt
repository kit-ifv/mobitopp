package domain.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.jackson.CoreCodePlanModule
import domain.jackson.DestinationChoiceParameterRepo
import domain.jackson.ZoneMatrixCreation
import java.nio.file.Path

/**
 * To register new json mappers/parser in a subproject create a directory `META-INF/services/`
 * in src/main/resources/ of the subproject.
 *
 * In there add a `package.name.myInterface/Class` so the serviceloader can look for implementations of myClass/
 * myInterface in that subproject. The file should contain the package-path to the implementation of that class/
 * interface of that subproject.
 *
 * For example, if you want to register a new class SubProjectMapper in a subproject:
 * ```kotlin
 * package domain.subproject.yaml
 * class SubProjectMapper: SimpleModule("MyMapper") { ... }
 * ```
 * create a`com.fasterxml.jackson.databind.Module` file in the META-INF.services directory and add the following content
 * ```
 * domain.subproject.yaml.SubProjectMapper
 * ```
 * This way the ServiceLoader finds it in the `.findAndRegisterModules()` step.
 */
object Yaml {
    val mapper = ObjectMapper(YAMLFactory())
        .registerKotlinModule()
        .registerModule(CoreCodePlanModule())
        .registerModule(ZoneMatrixCreation())
        .registerModule( DestinationChoiceParameterRepo())
        .findAndRegisterModules()

    inline fun <reified T> readYaml(path: Path): T {
        val file = path.toFile()
        return mapper.readValue(file)
    }
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))
}