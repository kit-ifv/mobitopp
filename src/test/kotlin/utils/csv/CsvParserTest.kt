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

class CsvParserTest {
    private val path: String = "src/test/resources/test_data.csv"
    private val file: File = File(path)
    private val builder: CsvParserBuilder<Entity> = CsvParserBuilder { index -> Entity(rowIndex = index) }

    private fun initBuilder(): CsvParserBuilder<Entity> {
        return builder
            .string.property(STR_COL) { e, s -> e.string = s }
            .boolean.property(BOOL_COL) { e, b -> e.bool = b }
            .int.property(INDEX_COL) { e, i -> e.csvIndex = i }
            .int.property(INT_COL) { e, i -> e.int = i }
            .float.property(FLOAT_COL) { e, f -> e.float = f }
    }

    @Test
    fun parseValueColumn() {
        val parser = CsvValueParser(STR_COL, ParserErrorHandling.SILENT_DROP) { it }

        val values = parser.parse(path).toList()

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
        )

        values.forEachIndexed { index, value ->
            assertEquals(expected[index], value )
        }
    }

    @Test
    fun parsePairs() {
        val parser = CsvPairParser(
            keyColumn = INDEX_COL,
            valueColumn = STR_COL,
            ParserErrorHandling.SILENT_DROP,
            keyParser = { it.toInt() },
            valueParser = {it}
        )

        val values = parser.parse(path).toList()
        val map = parser.asMapParser().parseMap(path)

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
        )

        values.forEachIndexed { index, value ->
            assertEquals(index+1, value.first)
            assertEquals(expected[index], value.second)
            assertEquals(expected[index], map[index+1])
        }
    }

    @Test
    fun parsePairsMerge() {
        val parser = CsvPairParser(
            keyColumn = INDEX_COL,
            valueColumn = STR_COL,
            ParserErrorHandling.SILENT_DROP,
            keyParser = { 0 },
            valueParser = {it}
        )

        val values = parser.parse(path).toList()
        val map = parser.asMergeMapParser().parseMap(path)

        assertEquals(10, values.size)
        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
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
        val mapParser = CsvParserBuilder { index -> index to Entity(rowIndex = index) }
            .string.property(STR_COL) { e, s -> e.second.string = s }
            .boolean.property(BOOL_COL) { e, b -> e.second.bool = b }
            .int.property(INDEX_COL) { e, i -> e.second.csvIndex = i }
            .int.property(INT_COL) { e, i -> e.second.int = i }
            .float.property(FLOAT_COL) { e, f -> e.second.float = f }
            .buildMapParser()

        val map = mapParser.parseMap(path)

        (0 until 10).forEach{
            assertContains(map, it, "map does not contain $it")
        }

        map.keys.forEach { index ->
            assertEquals(index, map[index]!!.rowIndex)
        }
    }

    @Test
    fun parseMergeMap() {
        val mapParser = CsvParserBuilder { index -> 0 to Entity(rowIndex = index) }
            .string.property(STR_COL) { e, s -> e.second.string = s }
            .boolean.property(BOOL_COL) { e, b -> e.second.bool = b }
            .int.property(INDEX_COL) { e, i -> e.second.csvIndex = i }
            .int.property(INT_COL) { e, i -> e.second.int = i }
            .float.property(FLOAT_COL) { e, f -> e.second.float = f }
            .buildMergeMapParser()

        val map = mapParser.parseMap(path)

        assertEquals(1, map.size)

        val elements = map[0]!!
        assertEquals(10, elements.size)
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
    fun parseStringProperty() {
        val entities = builder
            .string.property(STR_COL) { e, s -> e.string = s }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
        )

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.string)
            assertEquals(Entity(index, string = expected[index]), entity)
        }
    }

    @Test
    fun parseString() {
        val entities = builder
            .string.column(STR_COL) { e, s -> e.also{ e.string = s} }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(
            "a", "Hello; World", "42", "exitProcess(1)", "test", "%&#)!?", "1+2*3", "mobiTopp", "IfV" ,"fin"
        )

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.string)
            assertEquals(Entity(index, string = expected[index]), entity)
        }
    }

    @Test
    fun parseByteProperty() {
        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
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
    fun parseByte() {
        val entities = builder
            .byte.column(INT_COL) { e, b -> e.also{ e.byte = b } }
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
    fun parseShortProperty() {
        val entities = builder
            .short.property(INT_COL) { e, s -> e.short = s }
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
    fun parseShort() {
        val entities = builder
            .short.column(INT_COL) { e, s -> e.also{ e.short = s } }
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
    fun parseIntProperty() {
        val entities = builder
            .int.property(INT_COL) { e, i -> e.int = i }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.int)
            assertEquals(Entity(index, int = expected[index]), entity)
        }

    }

    @Test
    fun parseInt() {
        val entities = builder
            .int.column(INT_COL) { e, i -> e.also{ e.int = i } }
            .build()
            .parse(file)
            .toList()

        val expected = listOf(1234, 432, 23, 1337, 3434, 42, 17, 0, -2345, -77)

        entities.forEachIndexed { index, entity ->
            assertEquals(expected[index], entity.int)
            assertEquals(Entity(index, int = expected[index]), entity)
        }

    }

    @Test
    fun parseLongProperty() {
        val entities = builder
            .long.property(INT_COL) { e, l -> e.long = l }
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
    fun parseLong() {
        val entities = builder
            .long.column(INT_COL) { e, l -> e.also{ e.long = l } }
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
    fun parseFloatProperty() {
        val entities = builder
            .float.property(FLOAT_COL) { e, f -> e.float = f }
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
    fun parseFloat() {
        val entities = builder
            .float.column(FLOAT_COL) { e, f -> e.also{ e.float = f } }
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
    fun parseDoubleProperty() {
        val entities = builder
            .double.property(FLOAT_COL) { e, d -> e.double = d }
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
    fun parseDouble() {
        val entities = builder
            .double.column(FLOAT_COL) { e, d -> e.also{ e.double = d } }
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
    fun parseBoolProperty() {
        val entities = builder
            .boolean.property(BOOL_COL) { e, b -> e.bool = b }
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
    fun parseBool() {
        val entities = builder
            .boolean.column(BOOL_COL) { e, b -> e.also{ e.bool = b } }
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
    fun parseDurationProperty() {
        val entities = builder
            .addProperty(
                INT_COL,
                setter = {entity, s -> entity.duration = s.toInt().toDuration(DurationUnit.MINUTES) }
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
    fun parseDuration() {
        val entities = builder
            .addColumn(
                INT_COL,
                transform = { entity, s -> entity.also{ entity.duration = s.toInt().toDuration(DurationUnit.MINUTES) } }
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
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowSilently()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertTrue { consoleText.isEmpty() }
        assertNoWarnings(consoleText)
    }

    @Test
    fun `parse byte with silent keep should keep rows without warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorKeepRowSilently()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()

        assertKeptRowsAreUninitialized(entities)
        assertEquals(10, entities.size)
        assertTrue { consoleText.isEmpty() }
        assertNoWarnings(consoleText)
    }

    @Test
    fun `parse byte with warning drop should drop rows with warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowWithWarning()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertWarnings(consoleText,drop=true,error=false)
    }

    @Test
    fun `parse byte with warning keep should keep rows with warning`() {
        val console = ConsoleCaptor()

        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorKeepRowWithWarning()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()

        assertKeptRowsAreUninitialized(entities)
        assertEquals(10, entities.size)
        assertWarnings(consoleText, drop=false,error=false)
    }

    @Test
    fun `parse byte with error drop should drop rows with stack trace`() {
        val console = ConsoleCaptor()

        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorDropRowWithError()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()
        assertEquals(5, entities.size)
        assertWarnings(consoleText,drop=true,error=true)
    }

    @Test
    fun `parse byte with error keep should keep rows with stack trace`() {
        val console = ConsoleCaptor()

        val entities = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorKeepRowWithError()
            .build()
            .parse(file)
            .toList()

        val consoleText = console.getText()

        assertKeptRowsAreUninitialized(entities)
        assertEquals(10, entities.size)
        assertWarnings(consoleText, drop=false, error=true)
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

    private fun assertWarnings(consoleText: String, drop: Boolean, error: Boolean) {
        val type = if (error) { "ERROR" } else { "WARNING" }
        val action = if (drop) { "dropping" } else { "keeping" }
        val prefix = "$type ($action row):"

        listOf(0, 1, 3, 4, 8).forEach { index ->
            assertContains(consoleText, "$prefix Could not parse column $INT_COL of row $index")
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
        val parser = builder
            .byte.property(INT_COL) { e, b -> e.byte = b }
            .onParseErrorThrowException()
            .build()

        assertThrows<IllegalArgumentException> {
            parser.parse(file).toList()
        }
    }

}
