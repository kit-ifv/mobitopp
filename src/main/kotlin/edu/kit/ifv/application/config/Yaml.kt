package edu.kit.ifv.application.config
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.kit.ifv.domain.jackson.BikeSharingConfigModule
import edu.kit.ifv.domain.jackson.CSVConfigModule
import edu.kit.ifv.domain.jackson.CoreChoiceModelModes
import edu.kit.ifv.domain.jackson.CoreCodePlanModule
import edu.kit.ifv.domain.jackson.CoreZoneMatrixCreationModule
import edu.kit.ifv.domain.jackson.DestinationChoiceModule
import edu.kit.ifv.domain.jackson.DurationModule
import edu.kit.ifv.domain.jackson.MatrixConfigModule
import edu.kit.ifv.domain.jackson.ModeChoiceModule
import scala.jdk.javaapi.CollectionConverters
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.reflect.jvm.jvmName

/**
 * To register new json mappers/parser in a subproject create a directory `META-INF/services/`
 * in src/main/resources/ of the subproject.
 *
 * In there add a `package.name.myInterface/Class` so the serviceloader can look for implementations of myClass or
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

    var mapper: ObjectMapper = ObjectMapper(YAMLFactory())
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .registerKotlinModule()
        .registerModule(CoreCodePlanModule())
        .registerModule(CoreZoneMatrixCreationModule)
        .registerModule(CoreChoiceModelModes)
        .registerModule(DestinationChoiceModule)
        .registerModule(ModeChoiceModule)
        .registerModule(CSVConfigModule)
        .registerModule(BikeSharingConfigModule)
        .registerModule(MatrixConfigModule)
        .registerModule(DurationModule)
        .registerModule(PathModule)
        .findAndRegisterModules()

    inline fun <reified T> readYaml(path: Path): T {
        val file = path.toFile()
        return mapper.readValue(file)
    }

    /**
     * Can read yaml files with a `__parent__` field. The parent field specifies another yaml file, which this yaml
     * file is based on. All values from the parent file are included in the result. Single-Value-Fields specified in the child,
     * that are also present in the parent, are overwritten by the child.
     * Object fields are merged.
     * List fields are overwritten by child.
     *
     * - A file can at most contain one parent keyword
     * - Cyclic dependencies will lead to IllegalArgumentExceptions
     */
    inline fun <reified T> readYamlWithParent(path: Path): T {
        val mergedMap = YamlParentStackLoader().load(path)
        return mapper.convertValue(mergedMap, T::class.java)
    }
    inline fun <reified T> readYaml(string: String): T = readYaml(Path.of(string))

    inline fun <reified T> writeYaml(path: Path, obj: T) {
        val file = path.toFile()
        return mapper.writeValue(file, obj)
    }
    inline fun <reified T> writeYaml(string: String, obj: T) = writeYaml(Path.of(string), obj)

    /**
     * A single use loader that can handle parent references in a yaml file. Indicated with a `__parent__` field.
     */
    class YamlParentStackLoader {

        private val parentKey = "__parent__"
        val stack = mutableListOf<Path>()
        fun load(path: Path): Map<String, Any?> {
            require(path.toString().endsWith(".yaml")) { "Trying to load non-yaml file with a yaml parser: $path" }
            require(path.exists()) { "Yaml file $path does not exist." }
            val map = mapper.readValue<Map<String, Any?>>(path.inputStream()).toMutableMap()

            if (parentKey !in map) {
                return map
            }
            require(isPathNotSeenBefore(path)) {
                "Cycle detected, cannot read configs."
            }
            require(map[parentKey] != null) {
                "Parent field present, but no value specified. Occured in file: " +
                    "$path\nIf no parent is necessary, remove the __parent__ field from the file."
            }
            val parentPath = resolveParentKey(map[parentKey]!!, path)
            val parentMap = load(parentPath)
            // Remove the parentKey field from the output map.
            map.remove(parentKey)
            // Overwrite each and every field that is found in the parent map with entries from the childmap
            return mergeMaps(childMap = map, parentMap = parentMap)
        }

        /**
         * Recursively merges both provided maps by prioritizing the child over the parent. If the child contains a field with a
         * single value, it will overwrite the parents value. If the child contains a field which contains an object,
         * so a map of strings to values, it will merge those maps together, again prioritizing the child over the
         * parent, if a field occurs in both maps.
         */
        private fun mergeMaps(childMap: Map<String, Any?>, parentMap: Map<String, Any?>): Map<String, Any?> {
            val keysToMerge = childMap.filter { parentMap.containsKey(it.key) }.keys
            val nonConflicting = childMap.filter { it.key !in keysToMerge } +
                parentMap.filter { it.key !in keysToMerge }

            val merged = keysToMerge.map {
                if (childMap[it].isMap() && parentMap[it].isMap()) {
                    it to mergeMaps(childMap[it].toMap(), parentMap[it].toMap())
                } else {
                    it to childMap[it]
                }
            }
            return nonConflicting + merged
        }

        /**
         * Checks if `this::class` is a map (map used by yaml mapper)
         */
        private fun Any?.isMap(): Boolean {
            if (this == null) return false
            return this::class.jvmName.contains("Map")
        }

        /**
         * Converts `this `to a map. Expects this to be a scala map. (Mapper seems to output scala maps, in nested
         * values)
         */
        private fun Any?.toMap(): Map<String, Any?> = when (this) {
            null -> emptyMap()

            is Map<*, *> ->
                entries.associate { (key, value) ->
                    key.toString() to value
                }

            is scala.collection.Map<*, *> ->
                CollectionConverters
                    .asJava(this)
                    .entries
                    .associate { (key, value) ->
                        key.toString() to value
                    }

            else -> emptyMap()
        }

        private fun isPathNotSeenBefore(path: Path): Boolean {
            val normalized = path.toAbsolutePath().normalize()
            return (normalized !in stack).also {
                stack.add(normalized)
            }
        }

        private fun resolveParentKey(value: Any?, currentPath: Path): Path {
            val parentPath = Path.of(value as String)
            return if (parentPath.isAbsolute) {
                parentPath
            } else {
                currentPath.parent.resolve(parentPath)
            }
        }
    }
}

/**
 * Handles the serialization of paths.
 */
val PathModule: SimpleModule = SimpleModule("Path").addSerializer(Path::class.java, PathSerializer())

private class PathSerializer : JsonSerializer<Path>() {

    override fun serialize(value: Path?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (gen != null && value != null) {
            gen.writeString(value.toString())
        }
    }
}
