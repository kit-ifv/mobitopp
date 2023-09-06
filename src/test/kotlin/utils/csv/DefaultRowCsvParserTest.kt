package utils.csv

import ConsoleCaptor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
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

class DefaultRowCsvParserTest {
    private val path: String = "src/test/resources/test_data.csv"
    private val file: File = File(path)
    private val builder: CsvParserBuilder<Entity> = CsvParserBuilder { index -> Entity(rowIndex = index) }

    private fun initBuilder(): CsvParserBuilder<Entity> {
        return builder.addStringColumn(STR_COL) { e, s -> e.string = s }
            .addBooleanColumn(BOOL_COL) { e, b -> e.bool = b }
            .addIntColumn(INDEX_COL) { e, i -> e.csvIndex = i }
            .addIntColumn(INT_COL) { e, i -> e.int = i }
            .addFloatColumn(FLOAT_COL) { e, f -> e.float = f }
    }

    @Test
    fun parseFileSuccess() {
        val entities = initBuilder().build()
            .parse(path).toList()

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
        val entities = builder
            .addStringColumn(STR_COL) { e, s -> e.string = s }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(
            "a", "Hello World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
        )

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.string)
            assertEquals(Entity(index, string = expected[index]), entity)
        }
    }

    @Test
    fun parseByte() {
        val entities = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowSilently()
            .build()
            .parse(file)
            .toList()

        assertEquals(Entity(2, byte=23), entities[0])
        assertEquals(Entity(5, byte=42), entities[1])
        assertEquals(Entity(6, byte=17), entities[2])
        assertEquals(Entity(7, byte=0), entities[3])
        assertEquals(Entity(9, byte=-77), entities[4])
    }

    @Test
    fun parseShort() {
        val entities = builder
            .addShortColumn(INT_COL) { e, s -> e.short = s }
            .build()
            .parse(file)
            .toList()

        val expected = listOf<Short>(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.short)
            assertEquals(Entity(index, short = expected[index]), entity)
        }

    }

    @Test
    fun parseLong() {
        val entities = builder
            .addLongColumn(INT_COL) { e, l -> e.long = l }
            .build()
            .parse(file)
            .toList()

        val expected = listOf<Long>(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.long)
            assertEquals(Entity(index, long = expected[index]), entity)
        }

    }

    @Test
    fun parseFloat() {
        val entities = builder
            .addFloatColumn(FLOAT_COL) { e, f -> e.float = f }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(24.7009f, 0.07f, 4.2f, 3434.0f, -4.5f, 1.11f, 456.67890f, 22.2f, -23.2f, 5.9f)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.float)
            assertEquals(Entity(index, float = expected[index]), entity)
        }

    }

    @Test
    fun parseDouble() {
        val entities = builder
            .addDoubleColumn(FLOAT_COL) { e, d -> e.double = d }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(24.7009, 0.07, 4.2, 3434.0, -4.5, 1.11, 456.67890, 22.2, -23.2, 5.9)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.double)
            assertEquals(Entity(index, double = expected[index]), entity)
        }

    }

    @Test
    fun parseBool() {
        val entities = builder
            .addBooleanColumn(BOOL_COL) { e, b -> e.bool = b }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(true, false, false, true, true, true, false, true, false ,false)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.bool)
            assertEquals(Entity(index, bool = expected[index]), entity)
        }
    }

    @Test
    fun parseDuration() {
        val entities = builder
            .addTypedColumn(
                INT_COL,
                convert = {s -> s.toInt().toDuration(DurationUnit.MINUTES)},
                setter = {entity, d -> entity.duration = d  }
            )
            .build()
            .parse(file)
            .toList()

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

        val entities = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowSilently()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertTrue { consoleText.isEmpty() }
    }

    @Test
    fun `parse byte with silent keep should keep rows without warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorKeepRowSilently()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()

        assertKeptRowsAreUninitialized(entities)
        assertEquals(10, entities.size)
        assertTrue { consoleText.isEmpty() }
    }

    @Test
    fun `parse byte with warning drop should drop rows with warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowWithWarning()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertWarnings(consoleText)

    }

    @Test
    fun `parse byte with warning keep should keep rows with warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorKeepRowWithWarning()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()

        assertKeptRowsAreUninitialized(entities)
        assertEquals(10, entities.size)
        assertWarnings(consoleText)
    }

    private fun assertKeptRowsAreUninitialized(entities: List<Entity>) {
        assertEquals(Entity(0), entities[0])
        assertEquals(Entity(1), entities[1])
        assertEquals(Entity(2, byte = 23), entities[2])
        assertEquals(Entity(3), entities[3])
        assertEquals(Entity(4), entities[4])
        assertEquals(Entity(5, byte = 42), entities[5])
        assertEquals(Entity(6, byte = 17), entities[6])
        assertEquals(Entity(7, byte = 0), entities[7])
        assertEquals(Entity(8), entities[8])
        assertEquals(Entity(9, byte = -77), entities[9])
    }

    private fun assertWarnings(consoleText: String) {
        listOf(0, 1, 3, 4, 8).forEach { index ->
            assertContains(consoleText, "Could not parse column $INT_COL of row $index")
        }
    }

    @Test
    fun `parse byte with throws should throw exception`() {
        val parser = builder
            .addByteColumn(INT_COL) { e, b -> e.byte = b }
            .onParseErrorThrowException()
            .build()

        assertThrows<IllegalArgumentException> {
            parser.parse(file).toList()
        }
    }

}
