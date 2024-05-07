package modeling.steps

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.csv.CsvParser
import utils.csv.TestBuilder
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

abstract class ResourceTest<E> {
    protected lateinit var resource: Resource<E>

    @BeforeEach
    fun setUp() {
        resource = init()
    }

    abstract fun init(): Resource<E>
    abstract fun expectedName(): String
    abstract fun expectedBaseSource(): String
    abstract fun expectedToString(): String
    abstract fun expectedElements(): List<E>

    @Test
    open fun name() {
        assertEquals(expectedName(), resource.name)
    }

    @Test
    open fun source() {
        assertEquals(
            expectedBaseSource(),
            resource.source,
            "Expected '${expectedBaseSource()}' but got '${resource.source}'!"
        )
    }

    @Test
    open fun elements() {
        assertContentEquals(expectedElements(), resource.elements.toList())
    }

    @Test
    open fun testToString() {
        assertEquals(
            expectedToString(),
            resource.toString(),
            "Expected '${expectedToString()}' but got '$resource'!"
        )

    }

    protected fun <T> validateMetadata(
        result: Resource<T>,
        vararg expectedSources: String,
        expectedName: String? = null,
    ) {
        val compareName = expectedName ?: expectedName()
        assertEquals(compareName, result.name)

        val sourcePath = result.source.split("->")
        assertEquals(
            expectedSources.size,
            sourcePath.size,
            "expected sources ${expectedSources.toList()} but got ${sourcePath.toList()}"
        )

        expectedSources.zip(sourcePath).forEach { pair ->
            val expected = pair.first
            val actual = pair.second
            assertEquals(expected, actual.trim())
        }
    }
}

class SequenceResourceTest : ResourceTest<String>() {
    private val name: String = "TestStringList"
    private val source: String = "SequenceResourceTest#init()"
    private val elements: List<String> = listOf("Hello", "World", "!", "This", "is", "1", "test")
    override fun init() = SequenceResource(
        name,
        source,
        elements.asSequence()
    )

    override fun expectedName() = name
    override fun expectedBaseSource() = source

    override fun expectedToString() = "${expectedName()} (${expectedBaseSource()})"
    override fun expectedElements() = elements
}

class CsvResourceTest : ResourceTest<TestBuilder>() {

    override fun init(): Resource<TestBuilder> {
        val file = File("src/test/resources/test_data.csv")
        val parser = CsvParser { row ->
            TestBuilder(
                rowIndex = row.index,
                string = row("str")
            )
        }

        return CsvResource(
            file = file,
            parser = parser
        )
    }

    override fun expectedName() = "test_data.csv"
    override fun expectedBaseSource() = "src\\test\\resources\\test_data.csv"
    override fun expectedToString() = "CSV ${expectedName()} (${expectedBaseSource()})"

    override fun expectedElements() = utils.csv.expectedBuilders

    @Test
    override fun testToString() {
        assertEquals("CSV ${expectedName()} (${expectedBaseSource()})", resource.toString())
    }

    private fun expectedBuildResults() = utils.csv.expectedElements

    @Test
    fun build() {
        val result = resource.build().reusable()

        assertEquals(10, result.elements.count())
        assertEquals(expectedName(), result.name, "Expected '${expectedName()}' but got '${result.name}'!")

        validateMetadata(result, expectedBaseSource(), "build")
        assertContentEquals(expectedBuildResults(), result.elements.toList())
    }
}
