package modeling.synthesis

import ID
import Identifiable
import org.junit.jupiter.api.Test
import utils.csv.TestEntity
import kotlin.test.assertEquals

abstract class RepositoryTest<E>: ResourceMetadataTest<E>() where E: Identifiable<E> {
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

    //TODO map and filter?

}

open class MapRepositoryTest: RepositoryTest<TestEntity>() {
    protected val name: String = "TestEntityList"
    protected val source: String = "MapRepositoryTest#createRepository()"
    protected val elements: List<TestEntity> = listOf(
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
        MapRepository(elements, name, source)

    override fun expectedSize() = 10

    override fun queryId(): ID<TestEntity> = ID(7uL)

    override fun expectedQueryResult() = TestEntity(rowIndex = 7, string = "mobiTopp")

    override fun expectedName() = name

    override fun expectedBaseSource() = source

    override fun expectedToString() = "MapRepository[$name] ($source)"

    override fun expectedElements() = elements

}

open class MapRepositoryFromResourceTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        Repository.from(SequenceResource(name, source, elements.asSequence()))
}

open class MapRepositoryFromSequenceTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        Repository.from(name, source, elements.asSequence())
}

open class ResourceToMapRepositoryTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        SequenceResource(name, source, elements.asSequence()).asRepository()
}

open class SequenceToMapRepositoryTest: MapRepositoryTest() {
    override fun createRepo(): Repository<TestEntity> =
        elements.asSequence().asRepository(name, source)
}
