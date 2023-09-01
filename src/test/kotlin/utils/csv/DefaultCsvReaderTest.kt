package utils.csv

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import kotlin.test.assertContains

class DefaultCsvReaderTest {
    private val file = File("src/test/resources/test_data.csv")
    private val reader: DefaultCsvReader = DefaultCsvReader(file)

    @Test
    fun columns() {
        assertContains(reader.columns(), "index")
        assertContains(reader.columns(), "bool")
        assertContains(reader.columns(), "int")
        assertContains(reader.columns(), "float")
        assertContains(reader.columns(), "str")
        assertEquals(5, reader.columns().size)
    }

    @Test
    fun rows() {
        assertEquals(10, reader.rows().count())
    }

}