package utils.csv

import ConsoleCaptor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.ErrorHandling
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class Entity(
    var rowIndex: Int,
    var csvIndex: Int = -1,
    var string: String = "",
    var byte: Byte = 0,
    var short: Short = 0,
    var int: Int = 0,
    var long: Long = 0,
    var float: Float = 0.0f,
    var double: Double = 0.0,
    var bool: Boolean = false,
    var duration: Duration? = null,
)

class CsvParserTest {
    private val path: String = "src/test/resources/test_data.csv"
    private val file: File = File(path)
    val rowToEntity: (Row) -> Entity = { row ->
        Entity(
            rowIndex = row.index(),
            string = row[STR_COL],
            bool = row.boolean()[BOOL_COL],
            csvIndex = row[INDEX_COL].toInt(),
            int = row[INT_COL].toInt(),
            float = row[FLOAT_COL].toFloat()
        )
    }

    private val baseParser = DefaultRowCsvParser(mapping = rowToEntity)
    private val indexedParser = DefaultRowCsvParser { row ->
        row.index() to rowToEntity(row)
    }

    @Test
    fun parseValueColumn() {
        val parser = CsvValueParser(STR_COL, ErrorHandling.SILENT) { it }

        val values = parser.parse(path).toList()

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV", "fin"
        )

        values.forEachIndexed { index, value ->
            assertEquals(expected[index], value)
        }
    }

    @Test
    fun parsePairs() {
        val parser = CsvPairParser(
            keyParser = CsvValueParser(INDEX_COL, ErrorHandling.SILENT) { it.toInt() },
            valueParser = CsvValueParser(STR_COL, ErrorHandling.SILENT) { it }
        )

        val values = parser.parse(path).toList()
        val map = parser.asMapParser().parseMap(path)

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV", "fin"
        )

        values.forEachIndexed { index, value ->
            assertEquals(index + 1, value.first)
            assertEquals(expected[index], value.second)
            assertEquals(expected[index], map[index + 1])
        }
    }

    @Test
    fun parsePairsMerge() {
        val parser = CsvPairParser(
            keyParser = CsvValueParser(INDEX_COL, ErrorHandling.SILENT) { 0 },
            valueParser = CsvValueParser(STR_COL, ErrorHandling.SILENT) { it }
        )

        val values = parser.parse(path).toList()
        val map = parser.asMergeMapParser().parseMap(path)

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV", "fin"
        )

        values.forEachIndexed { index, value ->
            assertEquals(0, value.first)
            assertEquals(expected[index], value.second)
        }

        map[0]!!.forEachIndexed { index, value ->
            assertEquals(expected[index], value)
        }

    }

    @Test
    fun parseMap() {
        val mapParser = indexedParser.toMapParser()

        val map = mapParser.parseMap(path)

        (0 until 10).forEach {
            assertContains(map, it, "map does not contain $it")
        }

        map.keys.forEach { index ->
            assertEquals(index, map[index]!!.rowIndex)
        }
    }

    @Test
    fun parseMergeMap() {
        val mapParser = DefaultRowCsvParser { row ->
            0 to rowToEntity(row)
        }.toMapMergeParser()

        val map = mapParser.parseMap(path)

        assertEquals(1, map.size)

        val elements = map[0]!!
        assertEquals(10, elements.size)
    }

    @Test
    fun parseFileSuccess() {
        val entities = baseParser.parse(path).toList()

        entities.forEachIndexed { index, entity ->
            assertEquals(index, entity.rowIndex)
        }

        val expectedEntity5 = Entity(
            rowIndex = 5,
            csvIndex = 6,
            string = "%&#)!?",
            bool = true,
            int = 42,
            float = 1.11f
        )
        assertEquals(expectedEntity5, entities[5])
    }

    @Test
    fun parseString() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), string = row[STR_COL])
        }.parse(file)
            .toList()

        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV", "fin"
        )

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.string)
            assertEquals(Entity(index, string = expected[index]), entity)
        }
    }

    @Test
    fun parseByte() {
        val entities = DefaultRowCsvParser(ErrorHandling.SILENT) { row ->
            Entity(rowIndex = row.index(), byte = row[INT_COL].toByte())
        }
            .parse(file)
            .toList()

        assertEquals(Entity(2, byte = 23), entities[0])
        assertEquals(Entity(5, byte = 42), entities[1])
        assertEquals(Entity(6, byte = 17), entities[2])
        assertEquals(Entity(7, byte = 0), entities[3])
        assertEquals(Entity(9, byte = -77), entities[4])
    }

    @Test
    fun parseShort() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), short = row[INT_COL].toShort())
        }.parse(file)
            .toList()

        val expected = listOf<Short>(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.short)
            assertEquals(Entity(index, short = expected[index]), entity)
        }

    }

    @Test
    fun parseInt() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), int = row[INT_COL].toInt())
        }.parse(file).toList()

        val expected = listOf(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.int)
            assertEquals(Entity(index, int = expected[index]), entity)
        }

    }

    @Test
    fun parseLong() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), long = row[INT_COL].toLong())
        }.parse(file).toList()

        val expected = listOf<Long>(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.long)
            assertEquals(Entity(index, long = expected[index]), entity)
        }
    }

    @Test
    fun parseFloat() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), float = row[FLOAT_COL].toFloat())
        }.parse(file).toList()

        val expected = listOf(24.7009f, 0.07f, 4.2f, 3434.0f, -4.5f, 1.11f, 456.67890f, 22.2f, -23.2f, 5.9f)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.float)
            assertEquals(Entity(index, float = expected[index]), entity)
        }
    }

    @Test
    fun parseDouble() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), double = row[FLOAT_COL].toDouble())
        }.parse(file).toList()

        val expected = listOf(24.7009, 0.07, 4.2, 3434.0, -4.5, 1.11, 456.67890, 22.2, -23.2, 5.9)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.double)
            assertEquals(Entity(index, double = expected[index]), entity)
        }
    }

    @Test
    fun parseBool() {
        val entities = DefaultRowCsvParser { row ->
            Entity(rowIndex = row.index(), bool = row[BOOL_COL].toBoolean())
        }.parse(file).toList()

        val expected = listOf(true, false, false, true, true, true, false, true, false, false)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.bool)
            assertEquals(Entity(index, bool = expected[index]), entity)
        }
    }

    @Test
    fun parseDuration() {
        val entities = DefaultRowCsvParser { row ->
            Entity(
                rowIndex = row.index(),
                duration = row[INT_COL].toInt().minutes
            )
        }.parse(file).toList()

        val expected = listOf(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)
            .map { i -> i.toDuration(DurationUnit.MINUTES) }

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.duration)
            assertEquals(Entity(index, duration = expected[index]), entity)
        }

    }

    @Test
    fun `parse byte with silent drop should drop rows without warning`() {
        val console = ConsoleCaptor()

        val entities = DefaultRowCsvParser(ErrorHandling.SILENT) { row ->
            Entity(rowIndex = row.index(), byte = row[INT_COL].toByte())
        }.parse(file).toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertTrue { consoleText.isEmpty() }
        assertNoWarnings(consoleText)
    }

    @Test
    fun `parse byte with warning drop should drop rows with warning`() {
        val console = ConsoleCaptor()

        val entities = DefaultRowCsvParser(ErrorHandling.WARNING) { row ->
            Entity(rowIndex = row.index(), byte = row[INT_COL].toByte())
        }.parse(file).toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertWarnings(consoleText, error = false)
    }

    @Test
    fun `parse byte with error drop should drop rows with stack trace`() {
        val console = ConsoleCaptor()

        val entities = DefaultRowCsvParser(ErrorHandling.ERROR) { row ->
            Entity(rowIndex = row.index(), byte = row.byte()[INT_COL])
        }.parse(file).toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertWarnings(consoleText, error = true)
    }

    private fun assertWarnings(consoleText: String, error: Boolean) {
        val type = if (error) {
            "ERROR"
        } else {
            "WARNING"
        }
        val prefix = "$type (dropping row):"

        listOf(0, 1, 3, 4, 8).forEach { index ->
            assertContains(consoleText, "$prefix Could not parse row $index in test_data.csv: test_data.csv[$index]=[")
        }

        if (error) {
            assertStackTrace(consoleText)
        } else {
            assertNoStackTrace(consoleText)
        }
    }

    private fun assertNoWarnings(consoleText: String) {
        listOf(0, 1, 3, 4, 8).forEach { index ->
            assert("Could not parse column $INT_COL of row $index" !in consoleText)
        }

        assertNoStackTrace(consoleText)
    }

    private fun assertStackTrace(consoleText: String) {
        listOf(1234, 432, 1337, 3434, -2345).forEach { value ->
            assertContains(consoleText, "Value out of range. Value:\"$value\" Radix:10")
        }
    }

    private fun assertNoStackTrace(consoleText: String) {
        listOf(1234, 432, 1337, 3434, -2345).forEach { value ->
            assert("Value out of range. Value:\"$value\" Radix:10" !in consoleText)
        }
    }

    @Test
    fun `parse byte with throws should throw exception`() {
        val parser = DefaultRowCsvParser(ErrorHandling.THROW) { row ->
            Entity(rowIndex = row.index(), byte = row[INT_COL].toByte())
        }

        assertThrows<IllegalArgumentException> {
            parser.parse(file).toList()
        }
    }

}
