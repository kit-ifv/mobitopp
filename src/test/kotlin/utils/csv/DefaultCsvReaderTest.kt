package utils.csv

import org.junit.jupiter.api.Test

import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals


class DefaultCsvReaderTest {
    private val file = File("src/test/resources/test_data.csv")
    private val reader: DefaultCsvReader = DefaultCsvReader(file)

    @Test
    fun read() {
        val otherReader = CsvReader.read(file)

        val columns = reader.columns()
        val otherColumns = otherReader.columns()

        columns.forEach { col -> assertContains(otherColumns, col) }
        otherColumns.forEach { col -> assertContains(columns, col) }

        assertEquals(10, reader.rows().count())
        assertEquals(10, otherReader.rows().count())
    }

    @Test
    fun columns() {
        val columns =  reader.columns()

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
    private val file = File("src/test/resources/test_data.csv")
    private val reader: DefaultCsvReader = CsvReader.read(file)

    @Test
    fun source() {
        reader.rows().forEach {
            row -> assertEquals("test_data.csv", row.source())
        }
    }

    @Test
    fun index() {
        reader.rows().forEachIndexed { index, row
            ->  assertEquals(index, row.index())
        }
    }

    @Test
    fun get() {
        val rows = reader.rows().toList()

        rows.forEachIndexed{index, row ->
            assertEquals((index+1).toString(), row.get(INDEX_COL))
        }

        //true;1.11;6;@$§!?;42
        assertEquals("true",    rows[5].get(BOOL_COL))
        assertEquals("1.11",    rows[5].get(FLOAT_COL))
        assertEquals("6",       rows[5].get(INDEX_COL))
        assertEquals("%&#)!?",   rows[5].get(STR_COL))
        assertEquals("42",      rows[5].get(INT_COL))

    }

}
