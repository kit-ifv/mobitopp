package core.modelsteps

import org.junit.jupiter.api.Test
import utils.Identifiable
import utils.collections.enforceIndent
import utils.csv.TestEntity
import utils.csv.TestId
import kotlin.test.assertEquals

abstract class RepositoryTest<E, I> : ResourceTest<E>() where E : Identifiable<I> {
    protected lateinit var repository: Repository<E, I>

    override fun init(): Resource<E> {
        repository = createRepo()
        return repository
    }

    abstract fun createRepo(): Repository<E, I>
    abstract fun expectedSize(): Int
    abstract fun queryId(): I
    abstract fun expectedQueryResult(): E

    @Test
    open fun size() {
        assertEquals(expectedSize(), repository.size)
    }

    @Test
    open fun getById() {
        val result = repository.getById(queryId())
        assertEquals(expectedQueryResult(), result)
    }
}

open class MapRepositoryTest : RepositoryTest<TestEntity, TestId>() {
    protected val name: String = "TestEntityList"
    protected val source: String = "TestEntityList\n" + "+ add elements: init\n"
    override fun expectedElements() = listOf(
        TestEntity(rowIndex = 0, string = "a"),
        TestEntity(rowIndex = 1, string = "Hello; World"),
        TestEntity(rowIndex = 2, string = "42"),
        TestEntity(rowIndex = 3, string = "exitProcess(1)"),
        TestEntity(rowIndex = 4, string = "test"),
        TestEntity(rowIndex = 5, string = "%&#)!?"),
        TestEntity(rowIndex = 6, string = "1+2*3"),
        TestEntity(rowIndex = 7, string = "mobiTopp"),
        TestEntity(rowIndex = 8, string = "IfV"),
        TestEntity(rowIndex = 9, string = "fin"),
    )

    override fun createRepo(): Repository<TestEntity, TestId> =
        MapRepository<TestEntity, TestId>(name).also { repo ->
            repo.addElements("init", expectedElements())
        }

    override fun expectedSize() = 10

    override fun queryId(): TestId = TestId(7L)

    override fun expectedQueryResult() = TestEntity(rowIndex = 7, string = "mobiTopp")

    override fun expectedName() = name

    override fun expectedBaseSource() = source

    override fun expectedToString() = "Repository '$name':\n" +
        "  source:\n" +
        "${source.enforceIndent(4)}\n" +
        "  elements (${expectedSize()}):\n" +
        "    ${expectedElements().joinToString(", ")}"
}
