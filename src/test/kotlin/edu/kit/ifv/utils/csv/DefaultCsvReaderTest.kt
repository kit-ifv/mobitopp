package edu.kit.ifv.utils.csv
import edu.kit.ifv.utils.collections.muteProgressBars
import edu.kit.ifv.utils.collections.unmuteProgressBars
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DefaultCsvReaderTest {
    private val file = Path("src/test/resources/test_data.csv")
    private val reader: DefaultCsvReader = CsvReader.of(file)

    @BeforeEach
    fun muteProgress() {
        muteProgressBars()
    }

    @AfterEach
    fun unmuteProgress() {
        unmuteProgressBars()
    }

    @Test
    fun read() {
        val otherReader = CsvReader.of(file)

        val columns = reader.columns
        val otherColumns = otherReader.columns

        columns.forEach { col -> assertContains(otherColumns, col) }
        otherColumns.forEach { col -> assertContains(columns, col) }

        assertEquals(10, reader.rows().count())
        assertEquals(10, otherReader.rows().count())
    }

    @Test
    fun columns() {
        val columns = reader.columns

        COLUMNS.forEach { column ->
            assertContains(columns, column)
        }

        assertEquals(COLUMNS.size, columns.size)
    }

    @Test
    fun rows() {
        assertEquals(10, reader.rows().count())
    }
}

class DefaultRowTest {
    private val file = Path("src/test/resources/test_data.csv")
    private val reader: DefaultCsvReader = CsvReader.of(file)

    @Test
    fun source() {
        reader.rows().forEach { row ->
            assertEquals("test_data.csv", row.source)
        }
    }

    @Test
    fun index() {
        reader.rows().forEachIndexed { index, row ->
            assertEquals(index, row.index)
        }
    }

    @Test
    fun invoke() {
        val rows = reader.rows().toList()

        rows.forEachIndexed { index, row ->
            assertEquals((index + 1).toString(), row(INDEX_COL))
        }

        // true;1.11;6;@$§!?;42
        assertEquals("true", rows[5](BOOL_COL))
        assertEquals("1.11", rows[5](FLOAT_COL))
        assertEquals("6", rows[5](INDEX_COL))
        assertEquals("%&#)!?", rows[5](STR_COL))
        assertEquals("42", rows[5](INT_COL))
    }

    @Test
    fun `invoke with converter`() {
        val rows = reader.rows().toList()

        rows.forEachIndexed { index, row ->
            assertEquals(index + 1, row(INDEX_COL, String::toInt))
        }

        // true;1.11;6;@$§!?;42
        assertEquals(true, rows[5](BOOL_COL, String::toBoolean))
        assertEquals(1.11f, rows[5](FLOAT_COL, String::toFloat))
        assertEquals(6, rows[5](INDEX_COL, String::toInt))
        assertEquals("%&#)!?", rows[5](STR_COL))
        assertEquals(42, rows[5](INT_COL, String::toInt))
    }

    @Test
    fun valueAt() {
        val rows = reader.rows().toList()

        rows.forEachIndexed { index, row ->
            assertEquals((index + 1).toString(), row.valueAt(2))
        }

        // true;1.11;6;@$§!?;42
        assertEquals("true", rows[5].valueAt(0))
        assertEquals("1.11", rows[5].valueAt(1))
        assertEquals("6", rows[5].valueAt(2))
        assertEquals("%&#)!?", rows[5].valueAt(3))
        assertEquals("42", rows[5].valueAt(4))
    }

    @Test
    fun `valueAt with converter`() {
        val rows = reader.rows().toList()

        rows.forEachIndexed { index, row ->
            assertEquals(index + 1, row.valueAt(2, String::toInt))
        }

        // true;1.11;6;@$§!?;42
        assertEquals(true, rows[5].valueAt(0, String::toBoolean))
        assertEquals(1.11f, rows[5].valueAt(1, String::toFloat))
        assertEquals(6, rows[5].valueAt(2, String::toInt))
        assertEquals("%&#)!?", rows[5].valueAt(3))
        assertEquals(42, rows[5].valueAt(4, String::toInt))
    }
}
