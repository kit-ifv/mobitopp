package utils.binary

import org.jetbrains.kotlin.incremental.storage.readString
import org.junit.jupiter.api.Test
import usecases.steps.binary.CSVBinaryConverter
import usecases.steps.binary.DataType
import java.io.DataInputStream
import java.io.File
import kotlin.io.path.Path
import kotlin.test.assertEquals

class CSVBinaryConverterTest {
    @Test
    fun baseTypeTest() {
        val testData = Path("src/test/resources/test_data.csv")
        val datatypeMap = mapOf(
            Pair("bool", DataType.BOOLEAN ),
            Pair("int", DataType.INT ),
            Pair("float", DataType.FLOAT ),
            Pair("str", DataType.STRING ),
            Pair("index", DataType.INT ),
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
    }

    private fun DataInputStream.readString(stringLength: Int) : String {
        var output = ""
        for (i in 0 until stringLength) {
            output+= readChar()
        }
        return output
    }
}