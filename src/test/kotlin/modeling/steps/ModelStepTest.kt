package modeling.steps

import assertNotContains
import modeling.validation.Warning
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ConsoleCaptor
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.ImmutableEntity
import utils.csv.STR_COL
import utils.csv.TestEntity
import utils.csv.TestId
import utils.csv.expectedElements
import utils.csv.expectedElementsMappedStringLength
import utils.csv.float
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelStepTest {

    private lateinit var repository: MutableRepository<TestEntity, TestId>
    private lateinit var readOnlyRepository: Repository<ImmutableEntity, TestId>
    private lateinit var resource: Resource<TestEntity>
    private lateinit var csvResource: CsvResource<TestEntity>

    private lateinit var addResourceStep: AddResourceStep<TestEntity, TestId>
    private lateinit var addCsvStep: AddCsvStep<TestEntity, TestId>
    private lateinit var loadCsvStepWrapper: LoadCsvStep<TestEntity, TestId>

    private lateinit var filterStep: FilterStep<TestEntity, TestId>
    private lateinit var filterIdsStep: FilterIdsStep<TestEntity, TestId>

    private lateinit var updateStep: UpdateStep<TestEntity, TestId>
    private lateinit var transformStep: TransformStep<TestEntity, TestId>
    private lateinit var transformAllStep: TransformAllStep<TestEntity, TestId>

    private lateinit var forEachStep: ForEachStep<ImmutableEntity, TestId>
    private lateinit var resultList: MutableList<String>

    private lateinit var sealStep: SealStep<TestEntity, TestId>

    private val csvFile = File("src/test/resources/test_data.csv")

    @BeforeEach
    fun setUp() {
        repository = MapRepository<TestEntity, TestId>("test_repo")
        readOnlyRepository = repository

        resource = SequenceResource("seq_resource", "ModelStepTest", expectedElements.asSequence()).reusable()
        csvResource = CsvResource(csvFile, parser, reusable = true)

        addResourceStep = addResourceStep("test_add", resource, repository)
        addCsvStep = addCsvStep("test_add_csv", csvResource, repository)
        loadCsvStepWrapper = LoadCsvStep(
            file = csvFile,
            parser = parser,
            repository = repository,
            dependentRepositories = emptySet(),
            validationMock = emptyList(),
        )

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
    fun loadCsvToEmptyRepositoryWithWrapperStep() {
        loadCsvStepWrapper.execute()

        assertRepoContainsElements(expectedElements)
        assertRepoSource(csvResource, "add elements:", loadCsvStepWrapper)
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

    @Test
    fun modifySealedRepoThrowsException() {
        initRepositoryForTest()
        sealStep.execute()

        assertTrue(repository.sealed)
        val exception = assertThrows<IllegalStateException> {
            addCsvStep.execute()
        }
        assertTrue(repository.sealed)
        assertNotNull(exception.message)

        val message = exception.message!!
        assertContains(message, "Repository ${repository.name}")
        assertContains(message, "has already been sealed")
        assertContains(message, "attempted mutating action: test_add_csv")
    }

    @Test
    fun validateAndMockCustomStep() {
        val step = customValidationStep(repository)

        val captor = ConsoleCaptor()
        val warning = step.validate {
            println("Prefix!")
        }
        val consoleText = captor.getText()

        assertNotNull(warning)
        assertContains(consoleText, "Prefix!")
        assertContains(consoleText, "Validation of CustomValidationStep_AddDummy")
        assertEquals(1, repository.size)
        assertTrue(step.isValid)

        val validationText = getValidationText(warning)

        assertContains(validationText, "WARNING: Validate step CustomValidationStep_AddDummy produced warnings:")
        assertContains(validationText, "WARNING: CustomValidationStep_AddDummy_Warning")
    }

    @Test
    fun validateStepWithUnsealedDependentShouldProduceWarning() {
        val unsealedDependentRepository = MapRepository<TestEntity, TestId>("UnsealedDependency")

        val step = LoadCsvStep<TestEntity, TestId>(
            file = csvFile,
            name = "DummyCsvStepWithUnsealedDependent",
            parser = parser,
            repository = repository,
            dependentRepositories = setOf(unsealedDependentRepository),
            validationMock = expectedElements
        )

        val warning = step.validate()
        assertNotNull(warning)

        val validationText = getValidationText(warning)

        assertContains(validationText, "Step ${step.name} depends on unsealed repository:")
        assertContains(validationText, unsealedDependentRepository.name)

        assertRepoContainsElements(expectedElements)
    }

    @Test
    fun validateStepOnSealedRepositoryShouldProduceError() {
        sealStep.validate()
        val warning = loadCsvStepWrapper.validate()

        val validationText = getValidationText(warning)

        assertContains(validationText, "test_repo was sealed")
        assertContains(validationText, "load test_data.csv")
    }

    @Test
    fun validateInvalidCsvFilePath() {
        val step = LoadCsvStep<TestEntity, TestId>(
            file = File("invalid_path.csv"),
            parser = parser,
            repository = repository,
            dependentRepositories = setOf(),
            validationMock = listOf(),
        )

        val warning = step.validate()
        assertNotNull(warning)

        val captor = ConsoleCaptor()
        warning!!.printTree()
        val consoleText = captor.getText()

        assertContains(consoleText, "Error while checking read access to file: 'invalid_path.csv'!")
        assertContains(consoleText, "File does not exist: invalid_path.csv!")
        assertFalse(step.isValid)
    }

    @Test
    fun validateInvalidCsvColumns() {
        val step = LoadCsvStep<TestEntity, TestId>(
            file = csvFile,
            parser = invalidParser,
            repository = repository,
            dependentRepositories = setOf(),
            validationMock = listOf(),
        )

        val warning = step.validate()

        val captor = ConsoleCaptor()
        warning!!.printTree()
        val consoleText = captor.getText()

        assertNotNull(warning)
        assertContains(consoleText, "Invalid column 'INVALID_COL' accessed in step 'load test_data.csv' ")
        assertContains(consoleText, "does not exist in the source csv file: ${csvFile.path}!")
        assertContains(consoleText, "Invalid column index '42' accessed in step 'load test_data.csv'")
        assertContains(consoleText, "higher than number of columns (5)")
    }

    @Test
    fun validateUnmockableCsvColumn() {
        val step = LoadCsvStep<TestEntity, TestId>(
            file = csvFile,
            parser = unmockableParser,
            repository = repository,
            dependentRepositories = setOf(),
            validationMock = listOf(),
        )

        val warning = step.validate()
        assertNotNull(warning)

        val captor = ConsoleCaptor()
        warning!!.printTree()
        val consoleText = captor.getText()

        assertContains(consoleText, "Value of column 'str' of CSV test_data.csv")
        assertContains(consoleText, "could not be mocked for parsing!")
        assertContains(consoleText, "Validation of columns in step 'load test_data.csv' may be incomplete!")
    }

    private fun getValidationText(warning: Warning?): String {
        val validationCaptor = ConsoleCaptor()
        warning!!.printTree()
        val validationText = validationCaptor.getText()
        return validationText
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
) = object : TransformStep<TestEntity, TestId>() {
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
) = object : TransformAllStep<TestEntity, TestId>() {
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
) = object : ForEachStep<ImmutableEntity, TestId>() {
    override val name = name
    override val repository = repository
    override fun process(element: ImmutableEntity) = process(element)
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun verifyInput(): Warning? = null
}

private val parser: CsvParser<TestEntity> = CsvParser<TestEntity> { row ->
    TestEntity(
        rowIndex = row.index,
        string = row(STR_COL)
    )
}

private val invalidParser: CsvParser<TestEntity> = CsvParser<TestEntity> { row ->
    TestEntity(
        rowIndex = row.index,
        float = row.float(42),
        string = row("INVALID_COL"),
    )
}

private val unmockableParser: CsvParser<TestEntity> = CsvParser<TestEntity>(
    errorHandling = ErrorHandling.SILENT
) { row ->
    TestEntity(
        rowIndex = row.index,
        double = row(STR_COL) { it.split("|")[1].toDouble() },
        string = row("INVALID_COL"),
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

private fun customValidationStep(
    repository: MutableRepository<TestEntity, TestId>,
    name: String = "CustomValidationStep_AddDummy"
) = object : ModelStep {
    override val name = name

    override fun execute() {
        repository.addElements(
            "add_dummy",
            listOf(
                TestEntity(repository.size, string = "execute_dummy")
            )
        )
    }

    override fun verifyInput(): Warning {
        println("Validation of ${this.name}")
        return Warning("${this.name}_Warning", false)
    }

    override fun mockBehavior(): Warning? {
        repository.addElements(
            "add_dummy",
            listOf(
                TestEntity(repository.size, string = "mock_dummy")
            )
        )
        return null
    }
}
