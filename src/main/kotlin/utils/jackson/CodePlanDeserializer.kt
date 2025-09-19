package utils.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import utils.CodePlan
import utils.Encodable
import java.util.ServiceLoader


class CoreCodePlanModule: SimpleModule("CoreCodePlanModule") {
    init {
        addDeserializer(CodePlan::class.java, CodePlanDeserializer())
    }
}

/**
 * Deserialization of [CodePlan] instantiations is tricky, because most of the time it is just an object and jackson
 * (well actually every yaml parser) struggles with reading objects. In addition, generics is also a bit tricky. The
 * goal is that in the end subprojects can trivially register a deserialization strategy for a CodePlan<X> instance,
 * where they only need to specify the name and perhaps the target class.
 */
class CodePlanDeserializer : JsonDeserializer<CodePlan<*>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CodePlan<*> {
        val node = p.codec.readTree<JsonNode>(p)

        val type = when {
            node.isTextual -> node.asText()
            else -> node.get("type").asText()
        }
        val surrogateClass = SurrogateRegistry.allSurrogates[type]
            ?: error("Unknown behavior type: $type")
        val surrogate = when {
            node.isTextual -> surrogateClass.getDeclaredConstructor().newInstance()
            else -> p.codec.treeToValue(node, surrogateClass)

        }

        return surrogate.resolve()
    }
}

/**
 * This object collects all registered code plan surrogates registered the loaded projects, and thus the subprojects.
 */
object SurrogateRegistry {

    val allSurrogates: Map<String, Class<out CodePlanSurrogate<*>>> by lazy {
        buildRegistry()
    }

    private fun buildRegistry(): Map<String, Class<out CodePlanSurrogate<*>>> {
        val result = mutableMapOf<String, Class<out CodePlanSurrogate<*>>>()

        // 1. Register core surrogates directly, we already know them without library loading.
        CoreCodePlans().getSurrogateTypes().forEach { (key, value) ->
            register(key, value, result)
        }

        // 2. Discover and register from subprojects via ServiceLoader
        ServiceLoader.load(CodePlanSurrogateProvider::class.java)
            .forEach { provider ->
                provider.getSurrogateTypes().forEach { (key, value) ->
                    register(key, value, result)
                }
            }

        return result.toMap() // make it immutable
    }

    private fun register(
        key: String,
        value: Class<out CodePlanSurrogate<*>>,
        target: MutableMap<String, Class<out CodePlanSurrogate<*>>>
    ) {
        val existing = target.putIfAbsent(key, value)
        if (existing != null) {
            error("Duplicate CodePlanSurrogate key '$key' from ${value.name}, already defined by ${existing.name}")
        }
    }
}

/**
 * This interface must be implemented by the subproject to register code plan deserializers.
 */
interface CodePlanSurrogateProvider {
    fun getSurrogateTypes(): Map<String, Class<out CodePlanSurrogate<*>>>
}


class CoreCodePlans: CodePlanSurrogateProvider {
    private val registry: MutableMap<String, Class<out CodePlanSurrogate<*>>> = mutableMapOf()

    init {
        registry["coreModes"] = CoreModeSurrogate::class.java
        registry["coreRegionType"] = CoreRegionTypeSurrogate::class.java
    }

    override fun getSurrogateTypes(): Map<String, Class<out CodePlanSurrogate<*>>> {
        return registry
    }
}
/**
 * The most generic implementation of a class that can resolve to a codeplan.
 */
abstract class CodePlanSurrogate<T: Encodable> {
    abstract fun resolve(): CodePlan<T>
}


abstract class ModeSurrogate: CodePlanSurrogate<Mode>()
abstract class RegionTypeSurrogate: CodePlanSurrogate<RegionType>()
class CoreRegionTypeSurrogate: RegionTypeSurrogate() {
    override fun resolve(): CodePlan<RegionType> {
        return RegioStaR17
    }
}
class CoreModeSurrogate: ModeSurrogate() {
    override fun resolve(): CodePlan<Mode> {
        return LegacyMode.Companion
    }
}

