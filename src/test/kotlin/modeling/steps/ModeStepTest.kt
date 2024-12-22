package modeling.steps

import assertNotContains
import modeling.validation.Warning
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ConsoleCaptor
import utils.csv.CsvParser
import utils.csv.ImmutableEntity
import utils.csv.STR_COL
import utils.csv.TestEntity
import utils.csv.TestId
import utils.csv.expectedElements
import utils.csv.expectedElementsMappedStringLength
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModeStepTest {

    private lateinit var repository: MutableRepository<TestEntity, TestId>
    private lateinit var readOnlyRepository: Repository<ImmutableEntity, TestId>
    private lateinit var resource: Resource<TestEntity>
    private lateinit var csvResource: CsvResource<TestEntity>

    private lateinit var addResourceStep: AddResourceStep<TestEntity, TestId>
    private lateinit var addCsvStep: AddCsvStep<TestEntity, TestId>

    private lateinit var filterStep: FilterStep<TestEntity, TestId>
    private lateinit var filterIdsStep: FilterIdsStep<TestEntity, TestId>

    private lateinit var updateStep: UpdateStep<TestEntity, TestId>
    private lateinit var transformStep: TransformStep<TestEntity, TestId>
    private lateinit var transformAllStep: TransformAllStep<TestEntity, TestId>

    private lateinit var forEachStep: ForEachStep<ImmutableEntity, TestId>
    private lateinit var resultList: MutableList<String>

    private lateinit var sealStep: SealStep<TestEntity, TestId>



    @BeforeEach
    fun setUp() {
        repository = MapRepository<TestEntity, TestId>("test_repo")
        readOnlyRepository = repository

        resource = SequenceResource("seq_resource", "ModelStepTest", expectedElements.asSequence()).reusable()
        csvResource = CsvResource(File("src/test/resources/test_data.csv"), parser, reusable = true)

        addResourceStep = addResourceStep("test_add", resource, repository)
        addCsvStep = addCsvStep("test_add_csv", csvResource, repository)
        filterStep = filterStep("test_filter", ::filterOddIndex, repository)
        filterIdsStep = filterIdStep("test_filter_ids", ::filterOddId, repository)
        updateStep = updateStep("test_update", ::updateIntAttToStringLength, repository)
        transformStep = transformStep("test_transform", ::transformOddIdSquared, repository)
        transformAllStep = transformAllStep("test_transform_all", ::transformAllCumSumStringLength, repository)

        resultList = mutableListOf()
        forEachStep = forEachStep("test_for_each", collectStringsInList(resultList), readOnlyRepository)
        sealStep = SealStep(repository)

    }

    @Test
    fun addResourcesToEmptyRepository() {
        addResourceStep.execute()

        assertRepoContainsElements(expectedElements)
        assertRepoSource(resource, "add elements:", addResourceStep)
    }

    @Test
    fun addDuplicateIdElements() {
        addResourceStep.execute()
        assertRepoContainsElements(expectedElements)

        addResourceStep.execute()
        assertRepoContainsElements(expectedElements)
    }

    @Test
    fun addCsvResourceToEmptyRepository() {
        addCsvStep.execute()

        assertRepoContainsElements(expectedElements)
        assertRepoSource(csvResource, "add elements:", addResourceStep)
    }

    @Test
    fun filterOddIndexElements() {
        initRepositoryForTest()

        filterStep.execute()
        assertRepoContainsElements(filteredOddIndexElements)
        assertRepoSource(resource, "filter elements:", filterStep)
    }

    @Test
    fun filterOddIdElements() {
        initRepositoryForTest()

        filterIdsStep.execute()
        assertRepoContainsElements(filteredOddIndexElements)
        assertRepoSource(resource, "filter ids:", filterIdsStep)
    }

    @Test
    fun updateIntAttributeToStringLength() {
        initRepositoryForTest()

        updateStep.execute()
        assertRepoContainsElements(expectedElementsMappedStringLength)
        assertRepoSource(resource, "update each element:", updateStep)
    }

    @Test
    fun transformOddIdSquared() {
        initRepositoryForTest()

        transformStep.execute()
        assertRepoContainsElements(transformedOddIdSquared)
        assertRepoSource(resource, "replace each element:", transformStep)

        resource.elements.forEach {
            assertNotContains(repository.elements.toList(), it)
        }

    }

    @Test
    fun transformAllCumSumStringLength() {
        initRepositoryForTest()

        transformAllStep.execute()
        assertRepoContainsElements(transformedCumSumStringLength)
        assertRepoSource(resource, "replace all elements:", transformAllStep)

        resource.elements.forEach {
            assertNotContains(repository.elements.toList(), it)
        }
    }

    @Test
    fun collectStringsForEachElement() {
        initRepositoryForTest()

        val originalSource = readOnlyRepository.source

        forEachStep.execute()
        assertRepoContainsElements(expectedElements)
        assertEquals(readOnlyRepository.source, originalSource, "ForEachStep should not change source description!")

        assertEquals(expectedElementStrings, resultList)
    }

    @Test
    fun sealRepository() {
        initRepositoryForTest()

        val captor = ConsoleCaptor()
        sealStep.execute()
        val consoleText = captor.getText()

        assertRepoContainsElements(expectedElements)
        assertRepoSource(resource, "seal", null)

        assertTrue(repository.sealed)
        assertContains(consoleText, "Sealed ${repository.name}")
        assertContains(consoleText, "${repository.size} elements")
    }




    private fun initRepositoryForTest() {
        addResourceStep.execute()
        assertRepoSource(resource, "add elements:", addResourceStep)
    }

    private fun assertRepoContainsElements(expected: List<TestEntity>, onlySubset: Boolean = false) {

        if (!onlySubset) {
            assertEquals(expected.size, repository.size)

            repository.elements.forEach {
                assertContains(expected, it, "Repository has element missing in expected!")
            }
        }

        expected.forEach {
            assertContains(repository.elements.toList(), it, "Expected has element missing in repository!")
        }

    }

    private fun assertRepoSource(resource: Resource<TestEntity>, operation: String, step: ModelStep?) {
        assertContains(repository.source, operation)
        step?.also { assertContains(repository.source, it.name) }
        assertContains(repository.source, "${resource.name} [${resource.source}]")
    }


}

private fun addResourceStep(
    name: String,
    resource: Resource<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
) = object : AddResourceStep<TestEntity, TestId>() {
    override val name = name
    override val resource = resource
    override val repository = repository
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun mockElementsForValidation(): List<TestEntity> = emptyList()
    override fun verifyInput(): Warning? = null
}

private fun addCsvStep(
    name: String,
    resource: CsvResource<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
) = object : AddCsvStep<TestEntity, TestId>() {
    override val name = name
    override val resource = resource
    override val repository = repository
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun mockElementsForValidation(): List<TestEntity> = emptyList()
}

private fun filterStep(
    name: String,
    filter: (TestEntity) -> Boolean,
    repository: MutableRepository<TestEntity, TestId>,
) = object : FilterStep<TestEntity, TestId>() {
    override val name = name
    override val repository = repository
    override fun check(element: TestEntity): Boolean = filter(element)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private fun filterIdStep(
    name: String,
    filter: (TestId) -> Boolean,
    repository: MutableRepository<TestEntity, TestId>,
) = object : FilterIdsStep<TestEntity, TestId>() {
    override val name = name
    override val repository = repository
    override fun check(id: TestId): Boolean = filter(id)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private fun updateStep(
    name: String,
    update: (TestEntity) -> Unit,
    repository: MutableRepository<TestEntity, TestId>,
) = object : UpdateStep<TestEntity, TestId>() {
    override val name = name
    override val repository: MutableRepository<TestEntity, TestId> = repository
    override fun update(element: TestEntity) = update(element)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private fun transformStep(
    name: String,
    transform: (TestEntity) -> TestEntity?,
    repository: MutableRepository<TestEntity, TestId>,
) = object: TransformStep<TestEntity, TestId>() {
    override val name = name
    override val repository: MutableRepository<TestEntity, TestId> = repository
    override fun transform(element: TestEntity) = transform(element)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private fun transformAllStep(
    name: String,
    transformAll: (Collection<TestEntity>) -> Collection<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
) = object: TransformAllStep<TestEntity, TestId>() {
    override val name = name
    override val repository = repository
    override fun transformAll(elements: Collection<TestEntity>) = transformAll(elements)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private fun forEachStep(
    name: String,
    process: (ImmutableEntity) -> Unit,
    repository: Repository<ImmutableEntity, TestId>,
) = object: ForEachStep<ImmutableEntity, TestId>() {
    override val name = name
    override val repository = repository
    override fun process(element: ImmutableEntity) = process(element)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private val parser: CsvParser<TestEntity> = CsvParser<TestEntity>() { row ->
    TestEntity(
        rowIndex = row.index,
        string = row(STR_COL)
    )
}

private fun filterOddIndex(element: TestEntity): Boolean = (element.rowIndex % 2 != 0)
private fun filterOddId(id: TestId): Boolean = (id.value.toInt() % 2 != 0)
private val filteredOddIndexElements = expectedElements.filter { filterOddIndex((it)) }

private fun updateIntAttToStringLength(element: TestEntity) {
    element.int = element.string.length
}

private val transformedOddIdSquared = expectedElements.mapNotNull { transformOddIdSquared(it) }
private fun transformOddIdSquared(element: TestEntity): TestEntity? =
    element.takeIf {
        it.id.value >= 2 &&
        filterOddIndex(it)
    }?.let {
        it.copy(rowIndex = it.rowIndex * it.rowIndex)
    }

private val transformedCumSumStringLength = transformAllCumSumStringLength(expectedElements)
private fun transformAllCumSumStringLength(elements: Collection<TestEntity>): List<TestEntity> {
    var cumSum = 0
    return elements.sortedBy { it.rowIndex }.map {
        cumSum += it.string.length
        it.copy(int = cumSum)
    }
}

private fun collectStringsInList(list: MutableList<String>): (ImmutableEntity) -> Unit = { element: ImmutableEntity ->
    list.add(element.string)
}
private val expectedElementStrings = expectedElements.map { it.string }

