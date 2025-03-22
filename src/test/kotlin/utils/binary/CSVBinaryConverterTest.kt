package utils.binary

import org.junit.jupiter.api.Test
import usecases.steps.binary.CSVBinaryConverter
import usecases.steps.binary.DataType
import java.io.DataInputStream
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.test.assertEquals

class CSVBinaryConverterTest {
    @Test
    fun baseTypeTest() {
        val testData = Path("src/test/resources/test_data.csv")
        val datatypeMap = mapOf(
            Pair("bool", DataType.BOOLEAN),
            Pair("int", DataType.INT),
            Pair("float", DataType.FLOAT),
            Pair("str", DataType.STRING),
            Pair("index", DataType.INT),
        )
        val stringLength = 20
        val idColumnName = "index"

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength, idColumnName)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(10, it.readInt())
                assertEquals(20, it.readInt())
                assertEquals(1, it.readLong())
                assertEquals(2, it.readLong())
                assertEquals(3, it.readLong())
                assertEquals(4, it.readLong())
                assertEquals(5, it.readLong())
                assertEquals(6, it.readLong())
                assertEquals(7, it.readLong())
                assertEquals(8, it.readLong())
                assertEquals(9, it.readLong())
                assertEquals(10, it.readLong())

                assertEquals(true, it.readBoolean())
                assertEquals(24.7009.toFloat(), it.readFloat())
                assertEquals("a...................", it.readString(stringLength))
                assertEquals(1234, it.readInt())

                assertEquals(false, it.readBoolean())
                assertEquals(0.07.toFloat(), it.readFloat())
                assertEquals("Hello; World........", it.readString(stringLength))
                assertEquals(432, it.readInt())
            }
        }
        binary.toFile().delete()
    }

    @Test
    fun customPathTest() {
        val testData = Path("src/test/resources/testDemand/demand-data/activity.csv")
        val outputPath = Path("src/test/resources/tempOutput/activity.bin")
        val datatypeMap = mapOf(
            Pair("personId", DataType.DOUBLE),
            Pair("activityType", DataType.INT),
            Pair("observedTripDuration", DataType.INT),
            Pair("startTime", DataType.INT),
            Pair("duration", DataType.INT),
            Pair("tournr", DataType.INT),
            Pair("isMainActivity", DataType.BOOLEAN),
            Pair("isSupertour", DataType.BOOLEAN),
        )
        val stringLength = 20
        val idColumnName = "personId"

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength, idColumnName, outputPath)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use { stream ->
                assertEquals(19, stream.readInt()) // 19 Entries
                assertEquals(20, stream.readInt()) // String len
                repeat(19) {
                    assertEquals(1, stream.readLong())
                }
                assertEquals(7, stream.readInt())
            }
        }
        assertEquals("tempOutput", binary.parent.name)
        binary.toFile().delete()
    }

    private fun DataInputStream.readString(stringLength: Int): String {
        var output = ""
        repeat(stringLength) {
            output += readChar()
        }
        return output
    }
}
