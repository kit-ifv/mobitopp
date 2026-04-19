package application.config.subconfigs

import domain.shared.config.Yaml
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

private data class ParentConf(
    val name: String,
    val numberField: Int,
    val doubleField: Double,
)

/**
 * Tests the functionality of yaml files declaring a parent with a `__parent__` field.
 */
class YamlParentTest {
    private val testFileRoot = "src/test/resources/yamlParsing/parentYamlParsing/"

    @Test
    fun parseParent() {
        val expected = ParentConf("I am the parent", 2, 3.0)
        val parsed = Yaml.readYaml<ParentConf>(testFileRoot + "parent.yaml")
        assertEquals(expected, parsed)
    }

    @Test
    fun simpleParseWithParent() {
        val expected = ParentConf("I am the parent", 2, 3.0)
        val parsed = Yaml.readYamlWithParent<ParentConf>(Path(testFileRoot + "simple-child.yaml"))
        assertEquals(expected, parsed)
    }

    @Test
    fun singleOverwriteParent() {
        val expected = ParentConf("Overwritten by child", 2, 3.0)
        val parsed = Yaml.readYamlWithParent<ParentConf>(Path(testFileRoot + "overwriter-child.yaml"))
        assertEquals(expected, parsed)
    }

    @Test
    fun fullOverwriteTest() {
        val expected = ParentConf("Overwritten by child", 500, 3.141592)
        val parsed = Yaml.readYamlWithParent<ParentConf>(Path(testFileRoot + "full-overwrite.yaml"))
        assertEquals(expected, parsed)
    }

    /** Config with extra field*/
    private data class ChildConf(
        val name: String,
        val numberField: Int,
        val doubleField: Double,
        val extra: String,
    )

    /**
     * Child now specifies more than parent. Therefore, it is a different config we want to read.
     */
    @Test
    fun overwriteMoreThanParent() {
        val expected = ChildConf("Overwritten by child with extra fields", 420, 6767.0,
            extra = "My Parent does not have this field")
        val parsed = Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "over-overwrite-child.yaml"))
        assertEquals(expected, parsed)
    }

    /** Config with extra-extra field*/
    private data class SecondChildConf(
        val name: String,
        val numberField: Int,
        val doubleField: Double,
        val extra: String,
        val extraExtraField: String,
    )

    /**
     * Child is now child of a child of parent.
     */
    @Test
    fun testSecondGenerationChild() {
        val expected = SecondChildConf(
            "overwritten by second gen",
            420,
            6767.0,
            extra = "My Parent does not have this field",
            extraExtraField= "Only second gen has this lit field"
        )
        val parsed = Yaml.readYamlWithParent<SecondChildConf>(Path(testFileRoot + "extend-child-child.yaml"))
        assertEquals(expected, parsed)
    }

    /**
     * In this test the child refers to itself as a parent, this should definitely lead to some kind of exception.
     */
    @Test
    fun tryRecursiveParentChild() {
        assertThrows<IllegalArgumentException>{
            Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "dangerous-child.yaml"))
        }
    }

    /**
     * Does the position of the keyword within the child matter? It should probably not?
     */
    @Test
    fun testPositionOfParentKeyword() {
        val expected = ChildConf("Overwritten by child", 500, 3.141592,
            extra = "With extra field")
        val parsed = Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "position-in-file-child.yaml"))
        assertEquals(expected, parsed)
    }

    @Test
    fun cyclicDependency() {
        assertThrows<IllegalArgumentException>{
            Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "cyclic-depend-1.yaml"))
        }
        assertThrows<IllegalArgumentException>{
            Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "cyclic-depend-2.yaml"))
        }
        assertThrows<IllegalArgumentException>{
            Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "cyclic-depend-3.yaml"))
        }
        assertThrows<IllegalArgumentException>{
            Yaml.readYamlWithParent<ChildConf>(Path(testFileRoot + "cyclic-depend-4.yaml"))
        }
    }
}