package core.modelsteps

import assertNotContains
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.SequenceResource
import core.modelsteps.resources.reusable
import core.modelsteps.steps.addCsvResourceStep
import core.modelsteps.steps.addResourceStep
import core.modelsteps.steps.filterIdsStep
import core.modelsteps.steps.filterStep
import core.modelsteps.steps.forEachStep
import core.modelsteps.steps.loadCsvStep
import core.modelsteps.steps.modelStep
import core.modelsteps.steps.seal
import core.modelsteps.steps.transformBulkStep
import core.modelsteps.steps.transformEachStep
import core.modelsteps.steps.updateEachStep
import core.modelsteps.validation.validateCondition
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
import utils.report.ReportBuilder
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelStepTest {

    private lateinit var context: Context

    private lateinit var repository: MutableRepository<TestEntity, TestId>
    private lateinit var readOnlyRepository: Repository<ImmutableEntity, TestId>
    private lateinit var resource: Resource<TestEntity>
    private lateinit var csvResource: CsvResource<TestEntity>

    private lateinit var addResourceStep: (Context) -> Unit
    private lateinit var addCsvStep: (Context) -> Unit
    private lateinit var loadCsvStepWrapper: (Context) -> Unit

    private lateinit var filterStep: (Context) -> Unit
    private lateinit var filterIdsStep: (Context) -> Unit

    private lateinit var updateEachStep: (Context) -> Unit
    private lateinit var transformEachStep: (Context) -> Unit
    private lateinit var transformAllStep: (Context) -> Unit

    private lateinit var forEachStep: (Context) -> Unit
    private lateinit var resultList: MutableList<String>

    private lateinit var sealStep: (Context) -> Unit

    private val csvFile = Path("src/test/resources/test_data.csv")

    @BeforeEach
    fun setUp() {
        context = createContext()
        repository = MapRepository<TestEntity, TestId>("test_repo")
        readOnlyRepository = repository

        resource = SequenceResource("seq_resource", "ModelStepTest", expectedElements.asSequence()).reusable()
        csvResource = CsvResource(csvFile, parser, reusable = true)

        addResourceStep = addResourceStep("test_add", resource, repository)
        addCsvStep = addCsvStep("test_add_csv", csvResource, repository)
        loadCsvStepWrapper = loadCsvStep(csvFile, parser, repository)

        filterStep = filterStep("test_filter", ::filterOddIndex, repository)
        filterIdsStep = filterIdStep("test_filter_ids", ::filterOddId, repository)
        updateEachStep = updateStep("test_update", ::updateIntAttToStringLength, repository)
        transformEachStep = transformStep("test_transform", ::transformOddIdSquared, repository)
        transformAllStep = transformAllStep("test_transform_all", ::transformAllCumSumStringLength, repository)

        resultList = mutableListOf()
        forEachStep = forEachStep("test_for_each", collectStringsInList(resultList), readOnlyRepository)
        sealStep = sealStep(repository)
    }

    @Test
    fun addResourcesToEmptyRepository() {
        addResourceStep(context)

        assertRepoContainsElements(expectedElements)
        assertRepoSource(resource, "add elements:", context.currentStep)
    }

    @Test
    fun addDuplicateIdElements() {
        addResourceStep(context)
        assertRepoContainsElements(expectedElements)

        addResourceStep(context)
        assertRepoContainsElements(expectedElements)
    }

    @Test
    fun addCsvResourceToEmptyRepository() {
        addCsvStep(context)

        assertRepoContainsElements(expectedElements)
        assertRepoSource(csvResource, "add elements:", context.currentStep)
    }

    @Test
    fun loadCsvToEmptyRepositoryWithWrapperStep() {
        loadCsvStepWrapper(context)

        assertRepoContainsElements(expectedElements)
        assertRepoSource(csvResource, "add elements:", context.currentStep)
    }

    @Test
    fun filterOddIndexElements() {
        initRepositoryForTest()

        filterStep(context)
        assertRepoContainsElements(filteredOddIndexElements)
        assertRepoSource(resource, "filter elements:", context.currentStep)
    }

    @Test
    fun filterOddIdElements() {
        initRepositoryForTest()

        filterIdsStep(context)
        assertRepoContainsElements(filteredOddIndexElements)
        assertRepoSource(resource, "filter ids:", context.currentStep)
    }

    @Test
    fun updateIntAttributeToStringLength() {
        initRepositoryForTest()

        updateEachStep(context)
        assertRepoContainsElements(expectedElementsMappedStringLength)
        assertRepoSource(resource, "update each element:", context.currentStep)
    }

    @Test
    fun transformOddIdSquared() {
        initRepositoryForTest()

        transformEachStep(context)
        assertRepoContainsElements(transformedOddIdSquared)
        assertRepoSource(resource, "replace each element:", context.currentStep)

        resource.elements.forEach {
            assertNotContains(repository.elements.toList(), it)
        }
    }

    @Test
    fun transformAllCumSumStringLength() {
        initRepositoryForTest()

        transformAllStep(context)
        assertRepoContainsElements(transformedCumSumStringLength)
        assertRepoSource(resource, "replace all elements:", context.currentStep)

        resource.elements.forEach {
            assertNotContains(repository.elements.toList(), it)
        }
    }

    @Test
    fun collectStringsForEachElement() {
        initRepositoryForTest()

        val originalSource = readOnlyRepository.source

        forEachStep(context)
        assertRepoContainsElements(expectedElements)
        assertEquals(readOnlyRepository.source, originalSource, "ForEachStep should not change source description!")

        assertEquals(expectedElementStrings, resultList)
    }

    private fun reportText(): String {
        val captor = ConsoleCaptor()
        context.report.printToConsole()
        return captor.getText()
    }

    @Test
    fun sealRepository() {
        initRepositoryForTest()

        sealStep(context)

        val consoleText = reportText()
        assertRepoContainsElements(expectedElements)
        assertRepoSource(resource, "seal", null)

        assertTrue(repository.sealed)
        assertContains(consoleText, "sealed ${repository.name}")
        assertContains(consoleText, "${repository.size} elements")
    }

    @Test
    fun modifySealedRepoThrowsException() {
        initRepositoryForTest()
        sealStep(context)

        assertTrue(repository.sealed)
        val exception = assertThrows<IllegalStateException> {
            addCsvStep(context)
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
        context.execMode.setValidate()

        val step = customValidationStep(repository)

        val warning = step(context)

        val consoleText = reportText()
        assertNotNull(warning)

        assertEquals(1, repository.size)

        assertFalse(context.report.hasErrors())
        assertTrue(context.report.hasWarnings())

        assertContains(consoleText, "(WARNING) CustomValidationStep_AddDummy - validation produced warnings.")
        assertContains(consoleText, " * CustomValidationStep_AddDummy")
        assertContains(consoleText, "    - CustomValidationStep_AddDummy_Warning")
    }

    @Test
    fun validateStepWithUnsealedDependentShouldProduceWarning() {
        context.execMode.setValidate()
        val unsealedDependentRepository = MapRepository<TestEntity, TestId>("UnsealedDependency")

        val step: (Context) -> Unit = {
            it.loadCsvStep(
                repository,
                csvFile,
                parser,
                name = "DummyCsvStepWithUnsealedDependent",
                dependentRepositories = setOf(unsealedDependentRepository),
                validation = listOf {
                    repository.addElements("add mock elements", expectedElements)
                    true
                },
            )
        }

        step(context)
        assert(context.report.hasWarnings())

        val validationText = reportText()

        assertContains(validationText, "Step ${context.currentStep} depends on unsealed repository:")
        assertContains(validationText, unsealedDependentRepository.name)

        assertRepoContainsElements(expectedElements)
    }

    @Test
    fun validateStepOnSealedRepositoryShouldProduceError() {
        context.execMode.setValidate()
        sealStep(context)
        loadCsvStepWrapper(context)

        val validationText = reportText()

        assertContains(validationText, "test_repo was sealed")
        assertContains(validationText, "load test_data.csv")
        assertTrue(context.report.hasErrors(), reportText())
    }

    @Test
    fun validateInvalidCsvFilePath() {
        context.execMode.setValidate()

        val step: (Context) -> Unit = {
            it.loadCsvStep(repository, Path("invalid_path.csv"), parser)
        }

        step(context)
        assertTrue(context.report.hasErrors())

        val consoleText = reportText()
        assertContains(consoleText, "(FAILURE) load invalid_path.csv - validation failed.")
        assertContains(consoleText, "File does not exist: invalid_path.csv!")
    }

    @Test
    fun validateInvalidCsvColumns() {
        context.execMode.setValidate()
        val step: (Context) -> Unit = {
            it.loadCsvStep(repository, csvFile, invalidParser)
        }

        step(context)

        val consoleText = reportText()

        assertTrue(context.report.hasErrors(), reportText())
        assertContains(consoleText, "Invalid column 'INVALID_COL' accessed in step 'load test_data.csv' ")
        assertContains(consoleText, "does not exist in the source csv file: $csvFile!")
        assertContains(consoleText, "Invalid column index '42' accessed in step 'load test_data.csv'")
        assertContains(consoleText, "higher than number of columns (5)")
    }

    @Test
    fun validateUnmockableCsvColumn() {
        context.execMode.setValidate()

        val step: (Context) -> Unit = {
            it.loadCsvStep(repository, csvFile, unmockableParser)
        }

        step(context)
        assertTrue(context.report.hasWarnings())
        assertFalse(context.report.hasErrors())

        val consoleText = reportText()

        assertContains(consoleText, "Value of column 'str' of CSV test_data.csv")
        assertContains(consoleText, "could not be mocked for parsing!")
        assertContains(consoleText, "Validation of columns in step 'load test_data.csv' may be incomplete!")
    }

    private fun initRepositoryForTest() {
        addResourceStep(context)
        assertRepoSource(resource, "add elements:", context.currentStep)
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

    private fun assertRepoSource(resource: Resource<TestEntity>, operation: String, step: String?) {
        assertContains(repository.source, operation)
        step?.also { assertContains(repository.source, it) }
        assertContains(repository.source, "${resource.name} [${resource.source}]")
    }
}

private fun createContext() = object : Context {
    override var currentStep: String = ""
    override val execMode: ExecutionMode = ExecutionMode()
    override val scenarioName: String = "ModelStepTests"
    override val report: ReportBuilder = initReport()
}

private fun addResourceStep(
    name: String,
    resource: Resource<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.addResourceStep(name, repository, resource)
}

private fun addCsvStep(
    name: String,
    resource: CsvResource<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.addCsvResourceStep(name, repository, resource)
}

private fun loadCsvStep(
    path: Path,
    parser: CsvParser<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.loadCsvStep(repository, path, parser)
}

private fun filterStep(
    name: String,
    filter: (TestEntity) -> Boolean,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = { context ->
    context.filterStep(
        name = name,
        repository = repository,
        check = filter,
    )
}

private fun filterIdStep(
    name: String,
    filter: (TestId) -> Boolean,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.filterIdsStep(name, repository, check = filter)
}

private fun updateStep(
    name: String,
    update: (TestEntity) -> Unit,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.updateEachStep(name, repository, update = update)
}

private fun transformStep(
    name: String,
    transform: (TestEntity) -> TestEntity?,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.transformEachStep(name, repository, transform = transform)
}

private fun transformAllStep(
    name: String,
    transformAll: (Collection<TestEntity>) -> Collection<TestEntity>,
    repository: MutableRepository<TestEntity, TestId>,
): (Context) -> Unit = {
    it.transformBulkStep(name, repository, transform = transformAll)
}

private fun forEachStep(
    name: String,
    process: (ImmutableEntity) -> Unit,
    repository: Repository<ImmutableEntity, TestId>,
): (Context) -> Unit = {
    it.forEachStep(name, repository, process = process)
}

private fun sealStep(repository: MutableRepository<TestEntity, TestId>): (Context) -> Unit = {
    it.seal(repository)
}

private val parser: CsvParser<TestEntity> = CsvParser<TestEntity> { row ->
    TestEntity(
        rowIndex = row.index,
        string = row(STR_COL),
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
    errorHandling = ErrorHandling.SILENT,
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
private fun transformOddIdSquared(element: TestEntity): TestEntity? = element.takeIf {
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
    name: String = "CustomValidationStep_AddDummy",
): (Context) -> Unit = {
    val check: Check<Context> = {
        // validation check produces warning and adds mock element to repo
        repository.addElements(
            "add_dummy",
            listOf(
                TestEntity(repository.size, string = "mock_dummy"),
            ),
        )
        validateCondition({ "${name}_Warning" }, isError = false, predicate = { false })
    }

    it.modelStep(name, validation = listOf(check)) {
        repository.addElements(
            "add_dummy",
            listOf(
                TestEntity(repository.size, string = "execute_dummy"),
            ),
        )
    }
}
