package modeling.synthesis

import ID
import Identifiable
import org.junit.jupiter.api.Test
import utils.csv.TestEntity
import kotlin.test.assertEquals

abstract class RepositoryTest<E>: ResourceTest<E>() where E: Identifiable<E> {
    protected lateinit var repository: Repository<E>

    override fun init(): Resource<E> {
        repository = createRepo()
        return repository
    }

    abstract fun createRepo(): Repository<E>
    abstract fun expectedSize(): Int
    abstract fun queryId(): ID<E>
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

open class MapRepositoryTest: RepositoryTest<TestEntity>() {
    protected val name: String = "TestEntityList"
    protected val source: String = "MapRepositoryTest#createRepository()"
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

    override fun createRepo(): Repository<TestEntity> =
        MapRepository(expectedElements(), name, source)

    override fun expectedSize() = 10

    override fun queryId(): ID<TestEntity> = ID(7L)

    override fun expectedQueryResult() = TestEntity(rowIndex = 7, string = "mobiTopp")

    override fun expectedName() = name

    override fun expectedBaseSource() = source

    override fun expectedToString() = "MapRepository[$name] ($source)"



    override fun mapping1() = { te: TestEntity ->
        te.copy(string="hi")
    }

    override fun expectedMapping1Results() = expectedElements().map { it.copy(string="hi") }

    override fun filter1() = { te:TestEntity -> te.rowIndex%2 == 0 }

    override fun expectedFilter1Results() = expectedElements().filter { it.rowIndex%2 == 0 }

    override fun expectedFilter1Map1Results() =
        expectedElements().map { it.copy(string="hi")}.filter { it.rowIndex%2 == 0 }

    override fun expectedMergeResults() = listOf(
        expectedElements(),
        expectedElements().mapIndexed { index, te ->
            te.copy(rowIndex=index+expectedSize())
        }
    ).flatten()

    override fun mergeResource() = SequenceResource(
        resourceName="csv merge resource",
        description="csv merge resource",
        sequence = expectedElements().mapIndexed{ index, te ->
            te.copy(rowIndex = index+expectedElements().size )
        }.asSequence()
    )

}

open class MapRepositoryFromResourceTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        Repository.from(SequenceResource(name, source, expectedElements().asSequence()))
}

open class MapRepositoryFromSequenceTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        Repository.from(name, source, expectedElements().asSequence())
}

open class ResourceToMapRepositoryTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        SequenceResource(name, source, expectedElements().asSequence()).asRepository()
}

open class SequenceToMapRepositoryTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        expectedElements().asSequence().asRepository(name, source)
}
