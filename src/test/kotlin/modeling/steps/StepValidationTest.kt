package modeling.steps

import assertEmpty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.ConsoleCaptor
import utils.csv.CsvParser
import utils.csv.STR_COL
import utils.csv.TestBuilder
import utils.csv.TestEntity
import utils.csv.TestId
import utils.csv.expectedBuilders
import utils.csv.expectedElements
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StepValidationTest {

    private lateinit var repository: RepositoryBuilder<TestBuilder, TestEntity, TestId>

    private lateinit var elementResource: Resource<TestEntity>
    private lateinit var builderResource: Resource<TestBuilder>
    private lateinit var csvResource: CsvResource<TestBuilder>
    private lateinit var finalCsvResource: CsvResource<TestEntity>

    private lateinit var prepareStep: AddResourceStep<TestBuilder, TestEntity, TestId>
    private lateinit var prepareCsvStep: AddCsvStep<TestBuilder, TestEntity, TestId>

    private lateinit var updateStep: UpdateStep<TestBuilder, TestEntity, TestId>
    private lateinit var filterStep: FilterStep<TestBuilder, TestEntity, TestId>

    private lateinit var buildStep: BuildStep<TestBuilder, TestEntity, TestId>

    @BeforeEach
    fun setUp() {
        repository = RepositoryBuilder()
        elementResource =
            SequenceResource("ExpectedElements", "SynthesisValidation", expectedElements.asSequence())
        builderResource =
            SequenceResource("ExpectedBuilders", "SynthesisValidation", expectedBuilders.asSequence())
        csvResource = CsvResource(
            file = File("src/test/resources/test_data.csv"),
            parser = CsvParser { row ->
                TestBuilder(
                    rowIndex = row.index,
                    string = row(STR_COL)
                )
            }
        )
        finalCsvResource = CsvResource(
            file = File("src/test/resources/test_data.csv"),
            parser = CsvParser { row ->
                TestEntity(
                    rowIndex = row.index,
                    string = row(STR_COL),
                    int = row(STR_COL).length
                )
            }
        )

        prepareStep = AddResourceStep("prepare sequence", builderResource, repository)
        prepareCsvStep = AddCsvStep("prepare csv", csvResource, repository)
        updateStep = UpdateStep("map 'int' to length of 'str'", repository) {
                e ->
            e.also { e.int = e.string.length }
        }
        filterStep = FilterStep("filter elements with even rowIndex", repository) {
                e ->
            e.rowIndex % 2 == 0
        }
        buildStep = BuildStep("build elements", repository)
    }

    private fun initPreparing() {
        assertTrue(prepareStep.validate())
        assertEquals(RepositoryState.PREPARING, repository.state)
    }

    private fun initFinished() {
        assertTrue(prepareStep.validate())
        assertTrue(buildStep.validate())
        assertEquals(RepositoryState.FINISHED, repository.state)
    }

    @Test
    fun `valid PrepareResourceStep in UNINITIALIZED state`() {
        testStep(prepareStep, expectValid = true, RepositoryState.PREPARING)
    }

    @Test
    fun `valid PrepareCsvStep in UNINITIALIZED state`() {
        testStep(prepareCsvStep, expectValid = true, expectedState = RepositoryState.PREPARING)
    }

    @Test
    fun `invalid UpdateStep in UNINITIALIZED state`() {
        testStep(updateStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid FilterStep in UNINITIALIZED state`() {
        testStep(filterStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid BuildStep in UNINITIALIZED state`() {
        testStep(buildStep, expectValid = false, RepositoryState.FINISHED)
    }

    @Test
    fun `invalid PrepareResourceStep in PREPARING state`() {
        initPreparing()
        testStep(prepareStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid PrepareCsvStep in PREPARING state`() {
        initPreparing()
        testStep(prepareCsvStep, expectValid = false, expectedState = RepositoryState.PREPARING)
    }

    @Test
    fun `valid UpdateStep in PREPARING state`() {
        initPreparing()
        testStep(updateStep, expectValid = true, RepositoryState.PREPARING)
    }

    @Test
    fun `valid FilterStep in PREPARING state`() {
        initPreparing()
        testStep(filterStep, expectValid = true, RepositoryState.PREPARING)
    }

    @Test
    fun `valid BuildStep in PREPARING state`() {
        initPreparing()
        testStep(buildStep, expectValid = true, RepositoryState.FINISHED)
    }

    @Test
    fun `invalid PrepareResourceStep in FINISHED state`() {
        initFinished()
        testStep(prepareStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid PrepareCsvStep in FINISHED state`() {
        initFinished()
        testStep(prepareCsvStep, expectValid = false, expectedState = RepositoryState.PREPARING)
    }

    @Test
    fun `invalid UpdateStep in FINISHED state`() {
        initFinished()
        testStep(updateStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid FilterStep in FINISHED state`() {
        initFinished()
        testStep(filterStep, expectValid = false, RepositoryState.PREPARING)
    }

    @Test
    fun `invalid BuildStep in FINISHED state`() {
        initFinished()
        testStep(buildStep, expectValid = false, RepositoryState.FINISHED)
    }

    private fun testStep(
        step: ModelStep,
        expectValid: Boolean,
        expectedState: RepositoryState
    ): String {
        val console = ConsoleCaptor()

        val check = if (expectValid) "valid" else "invalid"
        val errorMessage = "Step '${step.name}' is expected to be $check in state: ${repository.state}!"
        var text: String

        assertEquals(
            expectValid,
            step.validate(),
            errorMessage.also { text = console.getText() }.let { "$it:\n<$text>\n" }
        )
        assertEquals(expectedState, repository.state)

        val message = "WARNING: ${step::class.simpleName} '${step.name}' is invalid!"
        if (expectValid) {
            assertEmpty(text)
        } else {
            assertContains(text, message)
        }

        return text
    }
}
