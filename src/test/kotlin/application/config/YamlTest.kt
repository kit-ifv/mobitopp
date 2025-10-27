package application.config

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.jackson.GenericKeyValueBuilder
import domain.jackson.GenericKeyValueDeserializer
import domain.jackson.GenericKeyValueSerializer
import domain.shared.behavior.ChoiceModelModes
import domain.shared.config.Yaml
import domain.shared.enums.Mode
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceParameters
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertEquals

val modeDeserializer = GenericKeyValueDeserializer(
    wraps = ModeChoiceParameters::class.java,
    default = mapOf("default" to ModeChoiceParameters()),
    loadFromSubmodules = false
)
val modeSerializer = GenericKeyValueSerializer(
    wraps = ModeChoiceParameters::class.java,
    default = mapOf(ModeChoiceParameters() to "default"),
    loadFromSubmodules = false
)

val modeModule = SimpleModule("ModeDeserializing module")
    .addDeserializer(ModeChoiceParameters::class.java, modeDeserializer)
    .addSerializer(ModeChoiceParameters::class.java, modeSerializer)

val destinationChoiceParameterDeserializer = GenericKeyValueDeserializer(
    wraps = DestinationChoiceParameters::class.java,
    default = mapOf("default" to DestinationChoiceParameters()),
    loadFromSubmodules = false
)

val destinationChoiceParameterSerializer = GenericKeyValueSerializer(
    wraps = DestinationChoiceParameters::class.java,
    default = mapOf(DestinationChoiceParameters() to "default"),
    loadFromSubmodules = false
)

val destinationChoiceParameterModule = SimpleModule("DestinationChoiceParamModule")
    .addDeserializer(DestinationChoiceParameters::class.java, destinationChoiceParameterDeserializer)
    .addSerializer(DestinationChoiceParameters::class.java, destinationChoiceParameterSerializer)

class TestCar(
    override val requiresVehicleTakeAlong: Boolean = false,
    override val code: Int = 0,
    override val description: String = ""
) : Mode

val testModes = ChoiceModelModes(
    car = TestCar(),
    passenger = TestCar(),
    bike = TestCar(),
    pedestrian = TestCar(),
    publicTransport = TestCar(),
    bikeSharing = TestCar(),
    ridePooling = TestCar(),
    carSharingFree = TestCar(),
    carSharingStation = TestCar(),
    taxi = TestCar(),
    eScooter = TestCar()
)
val choiceModelModesDeserializer = GenericKeyValueDeserializer(
    wraps = ChoiceModelModes::class.java,
    default = mapOf("default" to testModes),
    loadFromSubmodules = false
)

val choiceModelModesSerializer = GenericKeyValueSerializer(
    wraps = ChoiceModelModes::class.java,
    default = mapOf(testModes to "default"),
    loadFromSubmodules = false,
)

val choiceModelModesModule = SimpleModule("ChoiceModelModes")
    .addDeserializer(ChoiceModelModes::class.java, choiceModelModesDeserializer)
    .addSerializer(ChoiceModelModes::class.java, choiceModelModesSerializer)

private class MyParameterClass(val name: String) {
    override fun equals(other: Any?): Boolean {
        if (other !is MyParameterClass) return false
        return this.name == other.name
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

private data class TestClass(
    var t: MyParameterClass
)

class YamlTest {
    val defaultMapper: ObjectMapper = Yaml.mapper.copy()

    @BeforeEach
    fun resetObjectMapper() {
        Yaml.mapper = defaultMapper.copy()
    }

    @Test
    fun `equality of initial and written configs`() {
        val input = "src/test/resources/yamlParsing/basicConfig.yaml"
        val output = "src/test/resources/tempOutput/serializedConfig.yaml"
        Yaml.mapper
            .registerModule(modeModule)
            .registerModule(destinationChoiceParameterModule)
            .registerModule(choiceModelModesModule)

        val configObj = Yaml.readYaml<ShortTermConfig<ModeChoiceParameters, DestinationChoiceParameters>>(input)
        Yaml.writeYaml(output, configObj)
        val writtenConfig = Yaml.readYaml<ShortTermConfig<ModeChoiceParameters, DestinationChoiceParameters>>(output)
        assertEquals(configObj, writtenConfig)
        // Path(output).deleteIfExists()
    }

    @Test
    fun keyValueParserTest() {
        val input = "src/test/resources/yamlParsing/keyValueTest.yaml"
        val output = "src/test/resources/tempOutput/keyValueTest.yaml"
        val builder = GenericKeyValueBuilder(
            wraps = MyParameterClass::class.java,
            default = mapOf(
                "custom" to MyParameterClass("test"),
            ),
            loadFromSubmodules = false
        )

        Yaml.mapper.registerModule(builder.getModule())

        val parsed = Yaml.readYaml<TestClass>(input)
        Yaml.writeYaml(output, parsed)
        val writtenConfig = Yaml.readYaml<TestClass>(output)
        assertEquals(parsed, writtenConfig)
        Yaml.mapper
        Path(output).deleteIfExists()
    }

    @Test
    fun nonExistentMappingTest() {
        val input = "src/test/resources/yamlParsing/keyValueTest.yaml"
        val builder = GenericKeyValueBuilder(
            wraps = MyParameterClass::class.java,
            default = mapOf(
                "thisDoesntExist" to MyParameterClass("test"),
            ),
            loadFromSubmodules = false
        )
        Yaml.mapper.registerModule(builder.getModule())
        assertThrows(JsonMappingException::class.java) {
            Yaml.readYaml<TestClass>(input)
        }
    }

    @Test
    fun changedParameterTest() {
        val input = "src/test/resources/yamlParsing/keyValueTest.yaml"
        val output = "src/test/resources/tempOutput/keyValueTest.yaml"
        val builder = GenericKeyValueBuilder(
            wraps = MyParameterClass::class.java,
            default = mapOf(
                "custom" to MyParameterClass("test"),
                "other" to MyParameterClass("test2"),
            ),
            loadFromSubmodules = false
        )

        Yaml.mapper.registerModule(builder.getModule())

        val parsed = Yaml.readYaml<TestClass>(input)
        assertEquals(MyParameterClass("test"), parsed.t)
        parsed.t = MyParameterClass("test2") // replacing parsed value
        Yaml.writeYaml(output, parsed)
        val writtenConfig = Yaml.readYaml<TestClass>(output)
        assertEquals(MyParameterClass("test2"), writtenConfig.t)
        assertEquals(parsed, writtenConfig)
        Path(output).deleteIfExists()
    }
}
