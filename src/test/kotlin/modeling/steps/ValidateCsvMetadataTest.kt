package modeling.steps

import assertNotContains
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.ConsoleCaptor
import utils.collections.muteProgressBars
import utils.collections.unmuteProgressBars
import utils.csv.CsvParser
import utils.csv.INT_COL
import utils.csv.STR_COL
import utils.csv.TestBuilder
import utils.csv.TestEntity
import utils.csv.TestId
import utils.csv.int
import utils.csv.long
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertTrue

/**
 * Test the validation of csv resources!
 */
class ValidateCsvMetadataTest {
    private val invalidColumnName = "invalid_column_name"
    private val invalidTypecastColumnName = "invalid_typecast_column_name"
    private val file = File("src/test/resources/test_data.csv")
    private lateinit var repository: RepositoryBuilder<TestBuilder, TestEntity, TestId>
    private lateinit var csvResource: CsvResource<TestBuilder>
    private lateinit var invalidResource: CsvResource<TestBuilder>
    private lateinit var complexInvalidResource: CsvResource<TestBuilder>
    private lateinit var complexValidResource: CsvResource<TestBuilder>

    private val step = object : ModelStep {
        override val name = "TestStep"

        override fun execute() { /**/ }

        override fun validate() = true
    }

    @BeforeEach
    fun muteProgress() {
        muteProgressBars()
    }

    @AfterEach
    fun unmuteProgress() {
        unmuteProgressBars()
    }

    /** Set up csv resources! */
    @BeforeEach
    fun setUp() {
        repository = RepositoryBuilder()

        csvResource = CsvResource(
            file = file,
            parser = CsvParser { row ->
                TestBuilder(
                    rowIndex = row.index,
                    string = row(STR_COL)
                )
            }
        )

        invalidResource = CsvResource(
            file = file,
            parser = CsvParser { row ->
                TestBuilder(
                    rowIndex = row.index,
                    string = row(invalidColumnName),
                    int = row.int(INT_COL),
                    long = row.long(invalidTypecastColumnName)
                )
            }
        )

        complexInvalidResource = CsvResource(
            file = file,
            parser = CsvParser { row ->
                TestBuilder(
                    rowIndex = row.index,
                    long = row.long(invalidTypecastColumnName),
                    int = row.int(INT_COL),
                    double = row(STR_COL) { s -> s.split(":")[1].toDouble() },
                    string = row(invalidColumnName),
                )
            }
        )

        complexValidResource = CsvResource(
            file = file,
            parser = CsvParser { row ->
                TestBuilder(
                    rowIndex = row.index,
                    long = row.long(INT_COL),
                    int = row.int(INT_COL),
                    double = row(STR_COL) { s -> s.split(":")[1].toDouble() },
                    string = row(STR_COL),
                )
            }
        )
    }

    /** Validating a valid csv resource should produce no console output! */
    @Test
    fun validCsv() {
        val console = ConsoleCaptor()

        assertTrue(ValidateCsvMetadata(step, csvResource).validate())

        val text = console.getText()
        assertTrue(text.isEmpty())
    }

    /** Validating an invalid csv resource should print invalid columns on the console! */
    @Test
    fun invalidCsv() {
        val console = ConsoleCaptor()
        val res = ValidateCsvMetadata(step, invalidResource).validate()
        val text = console.getText()

        assertFalse(res)
        assertContains(text, "ERROR: Invalid column '$invalidColumnName'")
        assertContains(text, "ERROR: Invalid column '$invalidTypecastColumnName'")
        assertContains(text, "'${step.name}'")
        assertContains(text, file.path)

        assertNotContains(text, "WARNING: ")
        assertNotContains(text, "could not be mocked")
        assertNotContains(text, "may be incomplete!")
    }

    /**
     * Validating a complex invalid csv resource should indicate that validation could not be completed.
     * Also, all invalid columns found before the complex parse operation should be printed to the console!
     */
    @Test
    fun complexInvalidCsv() {
        val console = ConsoleCaptor()
        val res = ValidateCsvMetadata(step, complexInvalidResource).validate()
        val text = console.getText()

        assertFalse(res)
        assertContains(text, "ERROR: Invalid column '$invalidTypecastColumnName'")
        assertContains(text, "'${step.name}'")
        assertContains(text, file.path)

        assertNotContains(text, "ERROR: Invalid column '$invalidColumnName'")

        assertContains(
            text,
            "WARNING: Value of column '$STR_COL' of $complexInvalidResource could not be mocked for parsing!"
        )
        assertContains(text, "Validation of columns in step '${step.name}' may be incomplete!")
    }

    /**
     * Validating a complex valid csv resource should indicate that validation could not be completed.
     * However, no other errors should be printed to the console.
     */
    @Test
    fun complexValidCsv() {
        val console = ConsoleCaptor()
        val res = ValidateCsvMetadata(step, complexValidResource).validate()
        val text = console.getText()

        assertTrue(res)
        assertNotContains(text, "ERROR: Invalid column ")

        assertContains(
            text,
            "WARNING: Value of column '$STR_COL' of $complexValidResource could not be mocked for parsing!"
        )
        assertContains(text, "Validation of columns in step '${step.name}' may be incomplete!")
    }
}
