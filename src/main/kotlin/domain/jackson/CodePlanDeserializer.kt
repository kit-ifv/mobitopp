package domain.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.MainModes
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.synthesis.data.car.CarSegment
import domain.synthesis.data.car.engine.EngineType
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.person.Sex
import utils.CodePlan
import utils.Encodable
import utils.collections.invertMap
import java.util.ServiceLoader

class CoreCodePlanModule : SimpleModule("CoreCodePlanModule") {
    init {
        addDeserializer(CodePlan::class.java, CodePlanDeserializer())
        addSerializer(CodePlan::class.java, CodePlanSerializer())
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
        val surrogateClass = SurrogateRegistry.allSurrogates[type] ?: error("Unknown behavior type: $type")

        val surrogate = when {
            node.isTextual -> surrogateClass.getDeclaredConstructor().newInstance()
            else -> p.codec.treeToValue(node, surrogateClass)
        }

        return surrogate.resolve()
    }
}

/**
 * Serializes `CodePlan<*>`. If no serialization is known it maps to a default value using that codeplan's hashcode.
 */
class CodePlanSerializer : JsonSerializer<CodePlan<*>>() {
    override fun serialize(value: CodePlan<*>, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (gen != null) {
            val t: String? = SurrogateRegistry.inverseMapping[value.javaClass]
            if (t != null) {
                gen.writeString(t)
            } else {
                error(
                    "Unkown codeplan ${value.javaClass}. \n Codeplans that can be serialized:" +
                        " ${SurrogateRegistry.inverseMapping}",
                )
            }
        }
    }
}

/**
 * This object collects all registered code plan surrogates registered the loaded projects, and thus the subprojects.
 */
object SurrogateRegistry {

    val allSurrogates: Map<String, Class<out CodePlanSurrogate<*>>> by lazy {
        buildRegistry()
    }

    val inverseMapping: Map<Class<CodePlan<*>>, String> by lazy {
        buildRegistry().mapValues { entry -> entry.value.getDeclaredConstructor().newInstance().resolve().javaClass }
            .invertMap().mapValues { entry -> entry.value[0] } // this is cursed but works I guess...
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
        target: MutableMap<String, Class<out CodePlanSurrogate<*>>>,
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

class CoreCodePlans : CodePlanSurrogateProvider {
    private val registry: MutableMap<String, Class<out CodePlanSurrogate<*>>> = mutableMapOf()

    init {
        registry["coreModes"] = CoreModeSurrogate::class.java
        registry["mainModes"] = MainModeSurrogate::class.java
        registry["coreRegionType"] = CoreRegionTypeSurrogate::class.java
        registry["coreSexCodes"] = CoreSexSurrogate::class.java
        registry["coreGraduationCodes"] = CoreGraduationSurrogate::class.java
        registry["coreEmploymentCodes"] = CoreEmploymentSurrogate::class.java
        registry["coreEngineTypeCodes"] = CoreEngineTypeSurrogate::class.java
        registry["coreCarSegmentCodes"] = CoreCarSegmentSurrogate::class.java
        registry["coreActivityTypeCodes"] = CoreActivityTypeSurrogate::class.java
        registry["coreEconomicStatusCodes"] = CoreEconomicStatusSurrogate::class.java
    }

    override fun getSurrogateTypes(): Map<String, Class<out CodePlanSurrogate<*>>> = registry
}

/**
 * The most generic implementation of a class that can resolve to a codeplan.
 */
abstract class CodePlanSurrogate<T : Encodable> {
    abstract fun resolve(): CodePlan<T>
}

abstract class ModeSurrogate : CodePlanSurrogate<Mode>()
abstract class RegionTypeSurrogate : CodePlanSurrogate<RegionType>()
class CoreRegionTypeSurrogate : RegionTypeSurrogate() {
    override fun resolve(): CodePlan<RegionType> = RegioStaR17
}
class CoreModeSurrogate : ModeSurrogate() {
    override fun resolve(): CodePlan<Mode> = LegacyMode.Companion
}

class MainModeSurrogate : ModeSurrogate() {
    override fun resolve(): CodePlan<Mode> = MainModes
}

class CoreSexSurrogate : CodePlanSurrogate<Sex>() {
    override fun resolve(): CodePlan<Sex> = Sex.Companion
}

class CoreGraduationSurrogate : CodePlanSurrogate<Graduation>() {
    override fun resolve(): CodePlan<Graduation> = Graduation.Companion
}

class CoreEmploymentSurrogate : CodePlanSurrogate<Employment>() {
    override fun resolve(): CodePlan<Employment> = Employment.Companion
}

class CoreEngineTypeSurrogate : CodePlanSurrogate<EngineType>() {
    override fun resolve(): CodePlan<EngineType> = EngineType.Companion
}

class CoreCarSegmentSurrogate : CodePlanSurrogate<CarSegment>() {
    override fun resolve(): CodePlan<CarSegment> = CarSegment.Companion
}

class CoreActivityTypeSurrogate : CodePlanSurrogate<ActivityType>() {
    override fun resolve(): CodePlan<ActivityType> = LegacyActivityType.Companion
}

class CoreEconomicStatusSurrogate : CodePlanSurrogate<EconomicStatus>() {
    override fun resolve(): CodePlan<EconomicStatus> = EconomicStatus.Companion
}
