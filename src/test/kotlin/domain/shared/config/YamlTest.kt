package domain.shared.config

import application.config.ShortTermConfig
import com.fasterxml.jackson.databind.module.SimpleModule
import domain.jackson.GenericKeyValueDeserializer
import domain.jackson.GenericKeyValueSerializer
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceParameters
import org.junit.jupiter.api.Assertions.*
import kotlin.io.path.Path
import kotlin.test.Test

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
    .addSerializer(ModeChoiceParameters::class.java,modeSerializer)

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

class YamlTest {

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
    }
}
