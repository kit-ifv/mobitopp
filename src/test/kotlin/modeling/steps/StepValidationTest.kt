package modeling.steps
//
// import assertEmpty
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import org.junit.jupiter.api.TestInstance
// import utils.ConsoleCaptor
// import utils.csv.CsvParser
// import utils.csv.ImmutableEntity
// import utils.csv.STR_COL
// import utils.csv.TestBuilder
// import utils.csv.TestEntity
// import utils.csv.TestId
// import utils.csv.expectedBuilders
// import utils.csv.expectedElements
// import java.io.File
// import kotlin.test.assertContains
// import kotlin.test.assertEquals
// import kotlin.test.assertNull
//
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// class StepValidationTest {
//
//    private lateinit var repository: MutableRepository<TestEntity, TestId>
//    private val readOnlyRepository: Repository<TestEntity, TestId> by lazy { repository }
//
//    private lateinit var elementResource: Resource<ImmutableEntity>
//    private lateinit var builderResource: Resource<TestEntity>
//    private lateinit var csvResource: CsvResource<TestEntity>
//    private lateinit var finalCsvResource: CsvResource<ImmutableEntity>
//
//
//
//    private lateinit var prepareStep: AddResourceStep<TestBuilder, TestEntity, TestId>
//    private lateinit var prepareCsvStep: AddCsvStep<TestBuilder, TestEntity, TestId>
//
//    private lateinit var updateStep: TransformStep<TestBuilder, TestEntity, TestId>
//    private lateinit var filterStep: FilterStep<TestBuilder, TestEntity, TestId>
//
//    private lateinit var buildStep: BuildStep<TestBuilder, TestEntity, TestId>
//
//    @BeforeEach
//    fun setUp() {
//        repository = RepositoryBuilder()
//        elementResource =
//            SequenceResource("ExpectedElements", "SynthesisValidation", expectedElements.asSequence())
//        builderResource =
//            SequenceResource("ExpectedBuilders", "SynthesisValidation", expectedBuilders.asSequence())
//        csvResource = CsvResource(
//            file = File("src/test/resources/test_data.csv"),
//            parser = CsvParser { row ->
//                TestBuilder(
//                    rowIndex = row.index,
//                    string = row(STR_COL)
//                )
//            }
//        )
//        finalCsvResource = CsvResource(
//            file = File("src/test/resources/test_data.csv"),
//            parser = CsvParser { row ->
//                TestEntity(
//                    rowIndex = row.index,
//                    string = row(STR_COL),
//                    int = row(STR_COL).length
//                )
//            }
//        )
//
//        prepareStep = AddResourceStep("prepare sequence", builderResource, repository)
//        prepareCsvStep = AddCsvStep("prepare csv", csvResource, repository)
//        updateStep = TransformStep("map 'int' to length of 'str'", repository) {
//                e ->
//            e.also { e.int = e.string.length }
//        }
//        filterStep = FilterStep("filter elements with even rowIndex", repository) {
//                e ->
//            e.rowIndex % 2 == 0
//        }
//        buildStep = BuildStep("build elements", repository)
//    }
//
//    private fun initPreparing() {
//        assertNull(prepareStep.validate())
//        assertEquals(RepositoryState.PREPARING, repository.state)
//    }
//
//    private fun initFinished() {
//        assertNull(prepareStep.validate())
//        assertNull(buildStep.validate())
//        assertEquals(RepositoryState.FINISHED, repository.state)
//    }
//
//    @Test
//    fun `valid PrepareResourceStep in UNINITIALIZED state`() {
//        testStep(prepareStep, expectValid = true, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `valid PrepareCsvStep in UNINITIALIZED state`() {
//        testStep(prepareCsvStep, expectValid = true, expectedState = RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid UpdateStep in UNINITIALIZED state`() {
//        testStep(updateStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid FilterStep in UNINITIALIZED state`() {
//        testStep(filterStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid BuildStep in UNINITIALIZED state`() {
//        testStep(buildStep, expectValid = false, RepositoryState.FINISHED)
//    }
//
//    @Test
//    fun `invalid PrepareResourceStep in PREPARING state`() {
//        initPreparing()
//        testStep(prepareStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid PrepareCsvStep in PREPARING state`() {
//        initPreparing()
//        testStep(prepareCsvStep, expectValid = false, expectedState = RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `valid UpdateStep in PREPARING state`() {
//        initPreparing()
//        testStep(updateStep, expectValid = true, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `valid FilterStep in PREPARING state`() {
//        initPreparing()
//        testStep(filterStep, expectValid = true, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `valid BuildStep in PREPARING state`() {
//        initPreparing()
//        testStep(buildStep, expectValid = true, RepositoryState.FINISHED)
//    }
//
//    @Test
//    fun `invalid PrepareResourceStep in FINISHED state`() {
//        initFinished()
//        testStep(prepareStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid PrepareCsvStep in FINISHED state`() {
//        initFinished()
//        testStep(prepareCsvStep, expectValid = false, expectedState = RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid UpdateStep in FINISHED state`() {
//        initFinished()
//        testStep(updateStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid FilterStep in FINISHED state`() {
//        initFinished()
//        testStep(filterStep, expectValid = false, RepositoryState.PREPARING)
//    }
//
//    @Test
//    fun `invalid BuildStep in FINISHED state`() {
//        initFinished()
//        testStep(buildStep, expectValid = false, RepositoryState.FINISHED)
//    }
//
//    private fun testStep(
//        step: ModelStep,
//        expectValid: Boolean,
//        expectedState: RepositoryState
//    ): String {
//        val console = ConsoleCaptor()
//
//        val check = if (expectValid) "valid" else "invalid"
//        val errorMessage = "Step '${step.name}' is expected to be $check in state: ${repository.state}!"
//
//        val res = MultiStep("TestMultiStep wrapper", step).validate()
//        res?.printTree()
//        val text: String = console.getText()
//
//        if (expectValid) {
//            assertNull(res, errorMessage.let { "$it:\n<$text>\n" })
//        }
//
//        assertEquals(expectedState, repository.state)
//
//        val message = "${step::class.simpleName} '${step.name}' is invalid!"
//        if (expectValid) {
//            assertEmpty(text)
//        } else {
//            assertContains(text, message)
//        }
//
//        return text
//    }
// }
