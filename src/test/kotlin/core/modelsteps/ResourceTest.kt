package core.modelsteps

import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.Resource
import core.modelsteps.resources.SequenceResource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.collections.muteProgressBars
import utils.collections.unmuteProgressBars
import utils.csv.CsvParser
import utils.csv.TestEntity
import utils.csv.expectedElements
import kotlin.io.path.Path
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

abstract class ResourceTest<E> {
    protected lateinit var resource: Resource<E>

    @BeforeEach
    fun setUp() {
        resource = init()
    }

    @BeforeEach
    fun muteProgress() {
        muteProgressBars()
    }

    @AfterEach
    fun unmuteProgress() {
        unmuteProgressBars()
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

class CsvResourceTest : ResourceTest<TestEntity>() {

    override fun init(): Resource<TestEntity> {
        val path = Path("src/test/resources/test_data.csv")
        val parser = CsvParser { row ->
            TestEntity(
                rowIndex = row.index,
                string = row("str")
            )
        }

        return CsvResource(
            path = path,
            parser = parser
        )
    }

    override fun expectedName() = "test_data.csv"
    override fun expectedBaseSource() = Path("src", "test", "resources", "test_data.csv").toString()
    override fun expectedToString() = "CSV ${expectedName()} (${expectedBaseSource()})"

    override fun expectedElements() = expectedElements

    @Test
    override fun testToString() {
        assertEquals("CSV ${expectedName()} (${expectedBaseSource()})", resource.toString())
    }

    // private fun expectedBuildResults() = utils.csv.expectedElements
}
