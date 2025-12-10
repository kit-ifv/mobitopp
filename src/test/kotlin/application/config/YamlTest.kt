package application.config

import application.config.subconfigs.BikeSharingConfig
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.jackson.BikeSharingConfigModule
import domain.jackson.CSVConfigModule
import domain.jackson.CoreChoiceModelModes
import domain.jackson.CoreCodePlanModule
import domain.jackson.CoreZoneMatrixCreationModule
import domain.jackson.DestinationChoiceModule
import domain.jackson.GenericKeyValueBuilder
import domain.jackson.MatrixConfigModule
import domain.jackson.ModeChoiceModule
import domain.jackson.isSameOrSubtypeOf
import domain.jackson.javaType
import domain.shared.config.Yaml
import domain.shared.config.durationModule
import domain.shared.config.pathModule
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals

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
            .registerModule(CoreChoiceModelModes)
            .registerModule(DestinationChoiceModule)
            .registerModule(ModeChoiceModule)
            .registerModule(CSVConfigModule)
            .registerModule(BikeSharingConfigModule)
            .registerModule(MatrixConfigModule)
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

        val configObj = Yaml.readYaml<ShortTermConfig<BikeSharingConfig>>(input)
        Yaml.writeYaml(output, configObj)
        val writtenConfig = Yaml.readYaml<ShortTermConfig<BikeSharingConfig>>(output)
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
            javaType(MyParameterClass::class.java),
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
            javaType(MyParameterClass::class.java),
            default = mapOf(
                "fileDoesntUseThisKey" to MyParameterClass("test"),
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
            javaType(MyParameterClass::class.java),
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

    @Test
    fun typeTest() {
        val a1 = TypeFactory.defaultInstance().constructParametricType(A::class.java, B::class.java, C::class.java)
        val a2 = TypeFactory.defaultInstance().constructParametricType(A::class.java, B::class.java, D::class.java)
        val a3 = javaType(A::class.java)
        val a4 = TypeFactory.defaultInstance().constructParametricType(A::class.java, B::class.java, B::class.java)
        val e1 = TypeFactory.defaultInstance().constructParametricType(E::class.java, B::class.java)
        val f1 = TypeFactory.defaultInstance().constructParametricType(F::class.java, B::class.java)
        val x = javaType(TestInterface::class.java)

        assert(a1 != a2)
        assert(a1 != a3)
        assert(a2 != a3)

        assert(!a3.isSameOrSubtypeOf(a1))
        assert(!a3.isSameOrSubtypeOf(a2))
        assert(!a1.isSameOrSubtypeOf(a3))
        assert(!a2.isSameOrSubtypeOf(a3))

        assert(a4.isSameOrSubtypeOf(a4))
        assert(e1.isSameOrSubtypeOf(a4))
        assert(f1.isSameOrSubtypeOf(e1))
        assert(f1.isSameOrSubtypeOf(a4))

        assert(a1.isSameOrSubtypeOf(x))
        assert(x.isSameOrSubtypeOf(x))
        assert(a4.isSameOrSubtypeOf(x))
        assert(e1.isSameOrSubtypeOf(x))
        assert(f1.isSameOrSubtypeOf(x))
    }
}

private class B
private class C
private class D
private open class A<X, Y> : TestInterface
private open class E<Z> : A<Z, Z>()
private class F<Z> : E<Z>()

private interface TestInterface
