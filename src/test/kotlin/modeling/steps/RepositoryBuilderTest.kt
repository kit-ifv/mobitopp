package modeling.steps

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ID
import utils.csv.TestBuilder
import utils.csv.TestEntity
import utils.csv.TestId
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

open class RepositoryBuilderTest : RepositoryTest<TestEntity, TestId>() {
    protected lateinit var repoBuilder: RepositoryBuilder<TestBuilder, TestEntity, TestId>
    protected val elementResource = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 1, string = "Hello; World"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 3, string = "exitProcess(1)"),
        TestEntity(rowIndex = 4, string = "test"),
    ).asSequence().asResource(
        name = "RepositoryBuilderTestEntityList",
        source = "RepositoryBuilderTest#elementResource()"
    )
    protected val builders: List<TestBuilder> = listOf(
        TestBuilder(rowIndex = 0, string = "a"),
        TestBuilder(rowIndex = 1, string = "Hello; World"),
        TestBuilder(rowIndex = 2, string = "42"),
        TestBuilder(rowIndex = 3, string = "exitProcess(1)"),
        TestBuilder(rowIndex = 4, string = "test"),
    )

    override fun createRepo(): RepositoryBuilder<TestBuilder, TestEntity, TestId> {
        repoBuilder = RepositoryBuilder()
        return repoBuilder
    }
    override fun expectedSize() = 5
    override fun queryId(): ID<TestEntity> = ID(3L)
    override fun expectedQueryResult() = TestEntity(rowIndex = 3, string = "exitProcess(1)")
    override fun expectedName() = "RepositoryBuilderTestEntityList"
    override fun expectedBaseSource() = "RepositoryBuilderTest#elementResource()"
    override fun expectedToString() = "Uninitialized RepositoryBuilder"
    override fun expectedElements() = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 1, string = "Hello; World"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 3, string = "exitProcess(1)"),
        TestEntity(rowIndex = 4, string = "test"),
    )

    @Test
    override fun name() = assertStateException("finished/prepared") { repository.name }

    @Test
    override fun source() = assertStateException("finished/prepared") { repository.source }

    @Test
    open fun state() = assertEquals(RepositoryState.UNINITIALIZED, repoBuilder.state)

    @Test
    override fun elements() = assertStateException("finished") { repository.elements }

    @Test
    override fun size() = assertStateException("finished") { repository.size }

    @Test
    override fun getById() = assertStateException("finished") { repository.getById(queryId()) }

    open fun mapping1() = { te: TestEntity -> te }

    open fun expectedMapping1Results() = listOf<TestEntity>()

    open fun filter1() = { _: TestEntity -> true }

    open fun expectedFilter1Results() = listOf<TestEntity>()

    open fun expectedFilter1Map1Results() = listOf<TestEntity>()

//    @Test
//    open fun initialize() {
//        lazyRepo.initialize(elementResource)
//        assertEquals(RepositoryState.FINISHED, lazyRepo.state)
//    }

    @Test
    override fun testToString() = assertEquals(expectedToString(), repoBuilder.toString())

    protected fun assertStateException(
        expectedState: String = "finished",
        already: Boolean = false,
        runnable: () -> Unit
    ) {
        val e = assertThrows<IllegalStateException> {
            runnable()
        }

        if (already) {
            assertContains(e.message!!, "already been $expectedState!")
        } else {
            assertContains(e.message!!, "has not been $expectedState yet!")
        }
    }
    open fun expectedMergeResults() = mergeResource().elements.toList()
    open fun isMergeWithEmpty() = true

    open fun mergeResource() = SequenceResource(
        name = "csv merge resource",
        source = "description",
        elements = expectedElements().mapIndexed { index, te ->
            te.copy(rowIndex = index + expectedElements().size)
        }.asSequence()
    )


    protected fun builderResource() =
        SequenceResource(expectedName(), expectedBaseSource(), builders.asSequence())

    @Test
    open fun build() = assertStateException("prepared") { repoBuilder.build() }

    @Test
    open fun reduce() = assertStateException("prepared") { repoBuilder.reduce("reduce1") { e -> e.rowIndex % 2 == 0 } }

    @Test
    open fun update() =
        assertStateException("prepared") { repoBuilder.update("update1") { e -> e.also { e.int = e.string.length } } }


    @Test
    open fun prepare() {
        repoBuilder.prepare(builderResource())
        assertEquals(RepositoryState.PREPARING, repoBuilder.state)
        repoBuilder.build()
        assertContentEquals(expectedElements(), repoBuilder.elements.toList())
    }

    @Test
    open fun mergeBuilders() {
        repoBuilder.mergeBuilders(builderResource())
        assertEquals(RepositoryState.PREPARING, repoBuilder.state)
        repoBuilder.build()
        assertContentEquals(expectedElements(), repoBuilder.elements.toList())
    }
}

open class PreparedRepositoryTest : RepositoryBuilderTest() {
    private val name = "TestBuilderList"
    private val source = "PreparedRepositoryTest#createRepo"

    override fun createRepo(): RepositoryBuilder<TestBuilder, TestEntity, TestId> {
        return super.createRepo().also { repoBuilder.prepare(builderResource()) }
    }
    override fun expectedName() = name
    override fun expectedBaseSource() = source

    @Test
    override fun name() = assertEquals(expectedName(), repoBuilder.name)

    @Test
    override fun source() = assertEquals(expectedBaseSource(), repoBuilder.source)
    // TODO check STATE after every test?

    @Test
    override fun state() = assertEquals(RepositoryState.PREPARING, repoBuilder.state)

    @Test
    override fun elements() = assertStateException("built") { repoBuilder.elements }

    @Test
    override fun getById() = assertStateException("built") { repoBuilder.getById(queryId()) }

    @Test
    override fun size() = assertStateException("built") { repoBuilder.size }

    @Test
    override fun build() {
        repoBuilder.build()

        assertEquals(RepositoryState.FINISHED, repoBuilder.state)
        validateMetadata(repoBuilder, expectedBaseSource(), "build")
        assertContentEquals(expectedElements(), repoBuilder.elements.toList())
    }

    protected val reducedElements: List<TestEntity> = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 4, string = "test"),
    )

    @Test
    override fun reduce() {
        repoBuilder.reduce("reduce1") { e -> e.rowIndex % 2 == 0 }
        assertEquals(RepositoryState.PREPARING, repoBuilder.state)

        repoBuilder.build()
        assertEquals(RepositoryState.FINISHED, repoBuilder.state)
        validateMetadata(repoBuilder, expectedBaseSource(), "filter reduce1", "build")
        assertContentEquals(reducedElements, repoBuilder.elements.toList())
    }

    protected val updatedElements: List<TestEntity> = listOf(
        TestEntity(rowIndex = 0, string = "a", int = 1),
        TestEntity(rowIndex = 1, string = "Hello; World", int = 12),
        TestEntity(rowIndex = 2, string = "42", int = 2),
        TestEntity(rowIndex = 3, string = "exitProcess(1)", int = 14),
        TestEntity(rowIndex = 4, string = "test", int = 4),
    )

    @Test
    override fun update() {
        repoBuilder.update("update1") { e -> e.also { e.int = e.string.length } }
        assertEquals(RepositoryState.PREPARING, repoBuilder.state)

        repoBuilder.build()
        assertEquals(RepositoryState.FINISHED, repoBuilder.state)
        validateMetadata(repoBuilder, expectedBaseSource(), "map update1", "build")
        assertContentEquals(updatedElements, repoBuilder.elements.toList())
    }

    @Test
    override fun testToString() =
        assertEquals("Initialized RepositoryBuilder[$name] ($source)", repoBuilder.toString())

    @Test
    override fun prepare() =
        assertStateException("started", already = true) { repoBuilder.prepare(builderResource()) }


    @Test
    override fun mergeBuilders() {
        repoBuilder.mergeBuilders(
            builderResource().elements.map {
                it.rowIndex += expectedSize()
                it
            }.asResource(name=builderResource().name, builderResource().source + " -> reindex")
        )
        assertEquals(RepositoryState.PREPARING, repoBuilder.state)
        repoBuilder.build()
        val expected = listOf(
            expectedElements(),
            expectedElements().map { it.copy(rowIndex = it.rowIndex + expectedSize()) }
        ).flatten()
        assertContentEquals(expected, repoBuilder.elements.toList())
    }
}

class ConstructorPreparedRepositoryTest : PreparedRepositoryTest() {
    override fun createRepo(): RepositoryBuilder<TestBuilder, TestEntity, TestId> {
        return RepositoryBuilder(builderResource()).also {
            repoBuilder = it
        }
    }
}

open class FinishedBuilderRepositoryTest : RepositoryBuilderTest() {

    override fun createRepo(): RepositoryBuilder<TestBuilder, TestEntity, TestId> {
        return super.createRepo().also {
            it.prepare(builderResource())
            it.build()
        }
    }

    @Test
    override fun name() = assertEquals(expectedName(), repoBuilder.name)

    @Test
    override fun source() = validateMetadata(repoBuilder, expectedBaseSource(), "build")

    @Test
    override fun state() = assertEquals(RepositoryState.FINISHED, repoBuilder.state)

    @Test
    override fun elements() = assertContentEquals(expectedElements(), repoBuilder.elements.toList())

    @Test
    override fun getById() = assertEquals(expectedQueryResult(), repoBuilder.getById(queryId()))

    @Test
    override fun size() = assertEquals(expectedSize(), repoBuilder.size)

    @Test
    override fun update() = assertStateException(already = true) {
        repoBuilder.update("invalidUpdate") { e -> e }
    }

    @Test
    override fun reduce() = assertStateException(already = true) {
        repoBuilder.reduce("invalidUpdate") { _ -> true }
    }

    @Test
    override fun build() = assertStateException(already = true) {
        repoBuilder.build()
    }

    @Test
    override fun prepare() = assertStateException(already = true) {
        repoBuilder.prepare(builderResource())
    }

    @Test
    override fun testToString() =
        assertEquals(
            "Finished RepositoryBuilder[${expectedName()}] (${expectedBaseSource()} -> build)",
            repoBuilder.toString()
        )

    override fun expectedMergeResults() = listOf(
        expectedElements(),
        mergeResource().elements.toList()
    ).flatten()
    override fun isMergeWithEmpty() = false

    @Test
    override fun mergeBuilders() = assertStateException(already = true) {
        repoBuilder.mergeBuilders(builderResource())
    }
}
