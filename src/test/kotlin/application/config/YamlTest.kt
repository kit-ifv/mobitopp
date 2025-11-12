package application.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.jackson.CoreCodePlanModule
import domain.jackson.DestinationChoiceModule
import domain.jackson.CoreZoneMatrixCreationModule
import domain.jackson.GenericKeyValueBuilder
import domain.jackson.ModeChoiceModule
import domain.shared.behavior.ChoiceModelModes
import domain.shared.config.Yaml
import domain.shared.config.durationModule
import domain.shared.config.pathModule
import domain.shared.enums.Mode
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceParameters
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals

private val modeTestModule = GenericKeyValueBuilder(
    wraps = ModeChoiceParameters::class.java,
    default = mapOf("default" to ModeChoiceParameters()),
    loadFromSubmodules = false
).getModule()

private val destinationChoiceParameterTestModule = GenericKeyValueBuilder(
    wraps = DestinationChoiceParameters::class.java,
    default = mapOf("default" to DestinationChoiceParameters()),
    loadFromSubmodules = false
).getModule()

private class TestCar(
    override val requiresVehicleTakeAlong: Boolean = false,
    override val code: Int = 0,
    override val description: String = ""
) : Mode

private val testModes = ChoiceModelModes(
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

private val choiceModelModesTestModule = GenericKeyValueBuilder(
    wraps = ChoiceModelModes::class.java,
    default = mapOf("default" to testModes),
    loadFromSubmodules = false
).getModule()

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

    @BeforeEach
    fun resetObjectMapper() {
        // needs to be in sync with the initial Yaml.mapper otherwise we change the behaviour for each test here.
        Yaml.mapper = ObjectMapper(YAMLFactory())
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .registerKotlinModule()
            .registerModule(CoreCodePlanModule())
            .registerModule(CoreZoneMatrixCreationModule)
            .registerModule(DestinationChoiceModule)
            .registerModule(ModeChoiceModule)
            .registerModule(durationModule)
            .registerModule(pathModule)
            .findAndRegisterModules()
    }

    @Test
    fun `equality of initial and written configs`() {
        val input = "src/test/resources/yamlParsing/basicConfig.yaml"
        val output = "src/test/resources/tempOutput/serializedConfig.yaml"
        Path(output).createParentDirectories()
        if (!Path(output).exists()) Path(output).createFile()
        Yaml.mapper
            .registerModule(modeTestModule)
            .registerModule(destinationChoiceParameterTestModule)
            .registerModule(choiceModelModesTestModule)

        val configObj = Yaml.readYaml<ShortTermConfig>(input)
        Yaml.writeYaml(output, configObj)
        val writtenConfig = Yaml.readYaml<ShortTermConfig>(output)
        assertEquals(configObj, writtenConfig)
        Path(output).deleteIfExists()
    }

    @Test
    fun keyValueParserTest() {
        val input = "src/test/resources/yamlParsing/keyValueTest.yaml"
        val output = "src/test/resources/tempOutput/keyValueTest.yaml"
        Path(output).createParentDirectories()
        if (!Path(output).exists()) Path(output).createFile()
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
        Path(output).createParentDirectories()
        if (!Path(output).exists()) Path(output).createFile()
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
