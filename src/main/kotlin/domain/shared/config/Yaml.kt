package domain.shared.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.jackson.CoreCodePlanModule
import domain.jackson.DestinationChoiceParameterModule
import domain.jackson.ZoneMatrixCreationModule
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.parseIsoString

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

    var mapper = ObjectMapper(YAMLFactory())
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .registerKotlinModule()
        .registerModule(CoreCodePlanModule())
        .registerModule(ZoneMatrixCreationModule)
        .registerModule(DestinationChoiceParameterModule)
        .registerModule(durationModule)
        .registerModule(pathModule)
        .findAndRegisterModules()

    inline fun <reified T> readYaml(path: Path): T {
        val file = path.toFile()
        return mapper.readValue(file)
    }
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))

    inline fun <reified T> writeYaml(path: Path, obj: T) {
        val file = path.toFile()
        return mapper.writeValue(file, obj)
    }
    inline fun <reified T> writeYaml(string: String, obj: T) = writeYaml(Path.of(string), obj)
}

/**
 * Handles the serialization of kotlin durations.
 */
private val durationModule = SimpleModule("Duration")
    .addDeserializer(Duration::class.java, DurationDeserializer())
    .addSerializer(Duration::class.java, DurationSerializer())
private class DurationDeserializer : JsonDeserializer<Duration>() {
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): Duration? {
        if (p != null) {
            return parseIsoString(p.valueAsString)
        }
        return null
    }
}
private class DurationSerializer : JsonSerializer<Duration>() {
    override fun serialize(
        value: Duration?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        if (gen != null && value != null) {
            gen.writeString(value.toIsoString())
        }
    }
}

/**
 * Handles the serialization of paths.
 */
private val pathModule = SimpleModule("Path").addSerializer(Path::class.java, PathSerializer())
private class PathSerializer : JsonSerializer<Path>() {

    override fun serialize(
        value: Path?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        if (gen != null && value != null) {
            gen.writeString(value.toString())
        }
    }
}
