package modeling.synthesis

import ID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.csv.TestBuilder
import utils.csv.TestEntity
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


open class LateInitRepositoryTest : RepositoryTest<TestEntity>() {
    protected lateinit var lazyRepo: LateInitRepository<TestEntity>
    protected val elementResource = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 1, string = "Hello; World"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 3, string = "exitProcess(1)"),
        TestEntity(rowIndex = 4, string = "test"),
    ).asSequence().asRepository(
        "LateInitTestEntityList",
        "LateInitRepositoryTest#finalized()"
    )


    override fun createRepo() = LateInitRepository<TestEntity>().also { lazyRepo = it }
    override fun expectedSize() = 5
    override fun queryId(): ID<TestEntity> = ID(3uL)
    override fun expectedQueryResult() = TestEntity(rowIndex = 3, string = "exitProcess(1)")
    override fun expectedName() = "LateInitTestEntityList"
    override fun expectedBaseSource() = "LateInitRepositoryTest#finalized()"
    override fun expectedToString() = "Uninitialized LateInitRepository"
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
    open fun state() = assertEquals(RepositoryState.UNINITIALIZED, lazyRepo.state)

    @Test
    override fun elements() = assertStateException("finished") { repository.elements }

    @Test
    override fun size() = assertStateException("finished") { repository.size }

    @Test
    override fun getById() = assertStateException("finished") { repository.getById(queryId()) }

    @Test
    open fun initialize() {
        lazyRepo.initialize(elementResource)
        assertEquals(RepositoryState.FINISHED, lazyRepo.state)
    }

    @Test
    override fun testToString() = assertEquals("Uninitialized LateInitRepository", lazyRepo.toString())

    protected fun assertStateException(
        expectedState: String = "finished",
        already: Boolean = false,
        runnable: () -> Unit)
    {
        val e = assertThrows<IllegalStateException> {
            runnable()
        }

        if (already) {
            assertContains(e.message!!, "already been $expectedState!")
        } else {
            assertContains(e.message!!, "has not been $expectedState yet!")
        }
    }

    //TODO map and filter?

}

class FinishedLateInitRepositoryTest: LateInitRepositoryTest() {
    override fun createRepo(): LateInitRepository<TestEntity> {
        return super.createRepo().also { it.initialize(elementResource) }
    }

    @Test
    override fun testToString() =
        assertEquals(
            "Finished LateInitRepository[${expectedName()}] (${expectedBaseSource()})",
            lazyRepo.toString()
        )

    @Test
    override fun name() = assertEquals(expectedName(), lazyRepo.name)

    @Test
    override fun source() = assertEquals(expectedBaseSource(), lazyRepo.source)

    @Test
    override fun state() = assertEquals(RepositoryState.FINISHED, lazyRepo.state)

    @Test
    override fun elements() = assertContentEquals(expectedElements(), lazyRepo.elements.toList())

    @Test
    override fun size() = assertEquals(5, lazyRepo.size)

    @Test
    override fun getById() = assertEquals(expectedQueryResult(), lazyRepo.getById(queryId()))

    @Test
    override fun initialize() {
        val e = assertThrows<IllegalStateException> {
            lazyRepo.initialize(elementResource)
        }

        assertContains(e.message!!, "has already been finished!")
    }

}

open class BuilderRepositoryTest: LateInitRepositoryTest() {
    protected lateinit var builderRepo: BuilderRepository<TestBuilder, TestEntity>
    protected val builders: List<TestBuilder> = listOf(
        TestBuilder(rowIndex = 0, string = "a"),
        TestBuilder(rowIndex = 1, string = "Hello; World"),
        TestBuilder(rowIndex = 2, string = "42"),
        TestBuilder(rowIndex = 3, string = "exitProcess(1)"),
        TestBuilder(rowIndex = 4, string = "test"),
    )

    fun builderResource() =
        SequenceResource(expectedName(), expectedBaseSource(), builders.asSequence())

    override fun createRepo() = BuilderRepository<TestBuilder, TestEntity>().also {
        builderRepo=it
        lazyRepo=it
    }

    @Test
    override fun testToString() = assertEquals("Uninitialized BuilderRepository", builderRepo.toString())

    @Test
    open fun build() = assertStateException("prepared") { builderRepo.build() }

    @Test
    open fun reduce() = assertStateException("prepared") { builderRepo.reduce("reduce1"){ e -> e.rowIndex%2==0 } }

    @Test
    open fun update() =
        assertStateException("prepared") { builderRepo.update("update1"){ e -> e.also { e.int = e.string.length }} }

    @Test
    fun map() {
        assertThrows<UnsupportedOperationException> { builderRepo.map("mapping") { e -> e } }
    }

    @Test
    fun filter() {
        assertThrows<UnsupportedOperationException> { builderRepo.filter("mapping") { e -> true } }
    }

    @Test
    override fun initialize() {
        builderRepo.initialize(elementResource)
        assertEquals(RepositoryState.FINISHED, builderRepo.state)
    }

    @Test
    open fun prepare() {
        builderRepo.prepare(builderResource())
        assertEquals(RepositoryState.PREPARING, builderRepo.state)
    }

}

open class PreparedRepositoryTest: BuilderRepositoryTest() {
    private val name = "TestBuilderList"
    private val source = "PreparedRepositoryTest#createRepo"


    override fun createRepo(): BuilderRepository<TestBuilder, TestEntity> {
        return super.createRepo().also { builderRepo.prepare(builderResource()) }
    }
    override fun expectedName() = name
    override fun expectedBaseSource() = source

    @Test
    override fun name() = assertEquals(expectedName(), builderRepo.name)

    @Test
    override fun source() = assertEquals(expectedBaseSource(), builderRepo.source)
    //TODO check STATE after every test?

    @Test
    override fun state() = assertEquals(RepositoryState.PREPARING, builderRepo.state)

    @Test
    override fun elements() = assertStateException("built") { builderRepo.elements }

    @Test
    override fun getById() = assertStateException("built") { builderRepo.getById(queryId()) }

    @Test
    override fun size() = assertStateException("built") { builderRepo.size }

    @Test
    override fun build() {
        builderRepo.build()

        assertEquals(RepositoryState.FINISHED, builderRepo.state)
        validateMetadata(builderRepo, expectedBaseSource(), "build")
        assertContentEquals(expectedElements(), builderRepo.elements.toList())
    }

    protected val reducedElements: List<TestEntity> = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 4, string = "test"),
    )

    @Test
    override fun reduce() {
        builderRepo.reduce("reduce1") { e -> e.rowIndex%2 == 0 }
        assertEquals(RepositoryState.PREPARING, builderRepo.state)

        builderRepo.build()
        assertEquals(RepositoryState.FINISHED, builderRepo.state)
        validateMetadata(builderRepo, expectedBaseSource(), "filter reduce1", "build")
        assertContentEquals(reducedElements, builderRepo.elements.toList())
    }

    protected val updatedElements: List<TestEntity> = listOf(
        TestEntity(rowIndex = 0, string = "a", int=1),
        TestEntity(rowIndex = 1, string = "Hello; World", int=12),
        TestEntity(rowIndex = 2, string = "42", int=2),
        TestEntity(rowIndex = 3, string = "exitProcess(1)", int=14),
        TestEntity(rowIndex = 4, string = "test", int=4),
    )

    @Test
    override fun update() {
        builderRepo.update("update1") { e -> e.also { e.int=e.string.length } }
        assertEquals(RepositoryState.PREPARING, builderRepo.state)

        builderRepo.build()
        assertEquals(RepositoryState.FINISHED, builderRepo.state)
        validateMetadata(builderRepo, expectedBaseSource(), "map update1", "build")
        assertContentEquals(updatedElements, builderRepo.elements.toList())
    }

    @Test
    override fun testToString() =
        assertEquals("Initialized BuilderRepository[$name] ($source)", builderRepo.toString())

    @Test
    override fun initialize() =
        assertStateException("started", already=true) { builderRepo.initialize(elementResource) }

    @Test
    override fun prepare() =
        assertStateException("started", already=true) { builderRepo.prepare(builderResource()) }

}

class ConstructorPreparedRepositoryTest: PreparedRepositoryTest() {
    override fun createRepo(): BuilderRepository<TestBuilder, TestEntity> {
        return BuilderRepository(builderResource()).also { builderRepo=it }
    }
}

open class FinishedBuilderRepositoryTest: BuilderRepositoryTest() {

    override fun createRepo(): BuilderRepository<TestBuilder, TestEntity> {
        return super.createRepo().also {
            it.prepare(builderResource())
            it.build()
        }
    }

    @Test
    override fun name() = assertEquals(expectedName(), builderRepo.name)

    @Test
    override fun source() = validateMetadata(builderRepo, expectedBaseSource(), "build")

    @Test
    override fun state() = assertEquals(RepositoryState.FINISHED, builderRepo.state)

    @Test
    override fun elements() = assertContentEquals(expectedElements(), builderRepo.elements.toList())

    @Test
    override fun getById() = assertEquals(expectedQueryResult(), builderRepo.getById(queryId()))

    @Test
    override fun size() = assertEquals(expectedSize(), builderRepo.size)

    @Test
    override fun update() = assertStateException(already = true) {
        builderRepo.update("invalidUpdate") { e -> e }
    }

    @Test
    override fun reduce() = assertStateException(already = true) {
        builderRepo.reduce("invalidUpdate") { _ -> true }
    }

    @Test
    override fun build() = assertStateException(already = true) {
        builderRepo.build()
    }

    @Test
    override fun initialize() = assertStateException(already = true) {
        builderRepo.initialize(elementResource)
    }

    @Test
    override fun prepare() = assertStateException(already = true) {
        builderRepo.prepare(builderResource())
    }

    @Test
    override fun testToString() =
        assertEquals(
            "Finished BuilderRepository[${expectedName()}] (${expectedBaseSource()} -> build)",
            builderRepo.toString()
        )

}

class FinalizedBuilderRepositoryTest: FinishedBuilderRepositoryTest() {

    override fun createRepo(): BuilderRepository<TestBuilder, TestEntity> {
        return BuilderRepository<TestBuilder, TestEntity>().also {
            builderRepo=it
            builderRepo.initialize(elementResource)
        }
    }

    @Test
    override fun source() = validateMetadata(builderRepo, expectedBaseSource())

    @Test
    override fun testToString() = assertEquals(
        "Finished BuilderRepository[${expectedName()}] (${expectedBaseSource()})",
        builderRepo.toString()
    )

}


