package modeling.steps

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.csv.DefaultRowCsvParser
import utils.csv.TestBuilder
import utils.csv.expectedBuildersMappedStringLength
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

abstract class ResourceMetadataTest<E> {
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
        assertEquals(expectedBaseSource(), resource.source)
    }

    @Test
    open fun elements() {
        assertContentEquals(expectedElements(), resource.elements.toList())
    }

    @Test
    open fun testToString() {
        assertEquals(expectedToString(), resource.toString())
    }

    protected fun <T> validateMetadata(
        result: Resource<T>,
        vararg expectedSources: String,
        expectedName: String? = null,
    ) {
        val compareName = expectedName ?: expectedName()
        assertEquals(compareName, result.name)

        val sourcePath = result.source.split("->")
        assertEquals(expectedSources.size, sourcePath.size,
            "expected sources ${expectedSources.toList()} but got ${sourcePath.toList()}")

        expectedSources.zip(sourcePath).forEach { pair ->
            val expected = pair.first
            val actual = pair.second
            assertEquals(expected, actual.trim())
        }
    }
}

abstract class ResourceTest<E>: ResourceMetadataTest<E>() {

    abstract fun mapping1(): (E) -> E?
    abstract fun expectedMapping1Results(): List<E>

    abstract fun filter1(): (E) -> Boolean
    abstract fun expectedFilter1Results(): List<E>

    abstract fun expectedFilter1Map1Results(): List<E>

    open fun expectedMergeResults(): List<E> = listOf(
        expectedElements(),
        mergeResource().elements.toList()
    ).flatten()

    open fun isMergeWithEmpty(): Boolean = false

    @Test
    open fun map() {
        val operation = "mapping1"
        val result = resource.map(operation, mapping1())
        assertContentEquals(expectedMapping1Results(), result.elements.toList())

        validateMetadata(result, expectedBaseSource(), "map $operation")
    }

    @Test
    open fun filter() {
        val operation = "filter1"
        val result = resource.filter(operation, filter1())
        assertContentEquals(expectedFilter1Results(), result.elements.toList())

        validateMetadata(result, expectedBaseSource(), "filter $operation")
    }

    @Test
    open fun filterThenMap() {
        val op1 = "filter1"
        val op2 = "mapping1"
        val result = resource.filter(op1, filter1())
                             .map(op2, mapping1())

        assertContentEquals(expectedFilter1Map1Results(), result.elements.toList())
        validateMetadata(result, expectedBaseSource(), "filter $op1", "map $op2")
    }

    abstract fun mergeResource(): Resource<E>

    open fun intermediateOperationsBeforeMerge(): Array<String> = emptyArray()

    @Test
    open fun merge() {
        val otherResource = mergeResource()
        val description = otherResource.source
        val otherName = otherResource.name
        val result = resource.merge(otherResource)



        val actual = result.elements.toList()
        assertContentEquals(expectedMergeResults(), actual,
            "expected \n${expectedMergeResults().joinToString(separator = "\n")}\n" +
                    " but got \n${actual.joinToString(separator = "\n")}"
        )

        val intermed = intermediateOperationsBeforeMerge()
        if (isMergeWithEmpty()) {
            validateMetadata(result, *intermed, description, expectedName = otherName)
        } else {
            val expectName = "${expectedName()}, $otherName"
            validateMetadata(result, expectedBaseSource(), *intermed,
                "merge with $description", expectedName=expectName)
        }

    }

}

class SequenceResourceTest: ResourceTest<String>() {
    private val name: String = "TestStringList"
    private val source: String = "SequenceResourceTest#init()"
    private val elements: List<String> = listOf("Hello", "World", "!", "This", "is", "1", "test")
    override fun init() = SequenceResource(
        name, source, elements.asSequence()
    )

    override fun expectedName() = name
    override fun expectedBaseSource() = source

    override fun expectedToString() = "${expectedName()} (${expectedBaseSource()})"
    override fun expectedElements() = elements

    override fun mapping1(): (String) -> String? = {
        s -> if (s.length > 1) "${s[1]}" else null
    }
    override fun expectedMapping1Results() = listOf("e", "o", "h", "s", "e")

    override fun filter1(): (String) -> Boolean = {
        s -> s.length > 2
    }
    override fun expectedFilter1Results() = listOf("Hello", "World", "This", "test")

    override fun expectedFilter1Map1Results() = listOf("e", "o", "h", "e")

    override fun mergeResource() = resource

    override fun expectedMergeResults() = listOf(elements, elements).flatten()

}

class CsvResourceTest: ResourceTest<TestBuilder>() {

    override fun init(): Resource<TestBuilder> {
        val file = File("src/test/resources/test_data.csv")
        val parser = DefaultRowCsvParser { row ->
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

    override fun mapping1(): (TestBuilder) -> TestBuilder? = {
        b -> b.also { b.int = b.string.length }
    }

    override fun expectedMapping1Results() = expectedBuildersMappedStringLength

    override fun filter1(): (TestBuilder) -> Boolean = {
        b -> (b.rowIndex % 2) == 0
    }

    override fun expectedFilter1Results() = listOf(
        TestBuilder(rowIndex = 0, string = "a"),
        TestBuilder(rowIndex = 2, string = "42"),
        TestBuilder(rowIndex = 4, string = "test"),
        TestBuilder(rowIndex = 6, string = "1+2*3"),
        TestBuilder(rowIndex = 8, string = "IfV"),
    )

    override fun expectedFilter1Map1Results() = listOf(
        TestBuilder(rowIndex = 0, string = "a", int = 1),
        TestBuilder(rowIndex = 2, string = "42", int = 2),
        TestBuilder(rowIndex = 4, string = "test", int = 4),
        TestBuilder(rowIndex = 6, string = "1+2*3", int = 5),
        TestBuilder(rowIndex = 8, string = "IfV", int = 3),
    )

    @Test
    override fun testToString() {
        assertEquals("CSV ${expectedName()} (${expectedBaseSource()})", resource.toString())
    }

    private fun expectedBuildResults() = utils.csv.expectedElements

    @Test
    fun build() {
        val result = resource.build()

        assertEquals(10, result.size)
        assertEquals(expectedName(), result.name)
        validateMetadata(result, expectedBaseSource(), "build")
        assertContentEquals(expectedBuildResults(), result.elements.toList())
    }

    override fun mergeResource() = SequenceResource(
        resourceName="csv merge resource",
        description="csv merge resource",
        sequence = expectedElements().mapIndexed{ index, te ->
            te.copy(rowIndex = index+expectedElements().size )
        }.asSequence()
    )

}
