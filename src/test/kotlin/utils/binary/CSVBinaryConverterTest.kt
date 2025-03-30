package utils.binary


import org.junit.jupiter.api.Test
import usecases.steps.binary.CSVBinaryConverter
import usecases.steps.binary.DataType
import usecases.steps.binary.readString
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CSVBinaryConverterTest {
    @Test
    fun baseTypeTest() {
        val testData = Path("src/test/resources/test_data.csv")
        val datatypeMap = mapOf(
            Pair("bool", DataType.BOOLEAN),
            Pair("float", DataType.FLOAT),
            Pair("index", DataType.INT),
            Pair("str", DataType.STRING),
            Pair("int", DataType.INT),
        )
        val stringLength = 20

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(10, it.readInt())
                assertEquals(20, it.readInt())

                assertEquals(true, it.readBoolean())
                assertEquals(24.7009.toFloat(), it.readFloat())
                assertEquals(1, it.readInt())
                assertEquals("a", it.readString(stringLength))
                assertEquals(1234, it.readInt())

                assertEquals(false, it.readBoolean())
                assertEquals(0.07.toFloat(), it.readFloat())
                assertEquals(2, it.readInt())
                assertEquals("Hello; World", it.readString(stringLength))
                assertEquals(432, it.readInt())
            }
        }
        binary.toFile().delete()
    }

    @Test
    fun allTypesTest() {
        val testData = Path("src/test/resources/binary/allDatatypes.csv")
        val datatypeMap = mapOf(
            Pair("String", DataType.STRING),
            Pair("Long", DataType.LONG),
            Pair("Int", DataType.INT),
            Pair("Double", DataType.DOUBLE),
            Pair("Float", DataType.FLOAT),
            Pair("Bool", DataType.BOOLEAN),
            Pair("Short", DataType.SHORT),
            Pair("Byte", DataType.BYTE),
            Pair("Char", DataType.CHAR),
        )
        val stringLength = 6

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(4, it.readInt())
                assertEquals(stringLength, it.readInt())

                assertEquals("Hello", it.readString(stringLength))
                assertEquals(9223372036854775807, it.readLong())
                assertEquals(2147483647, it.readInt())
                assertEquals(3.1415926535, it.readDouble())
                assertEquals(2.71.toFloat(), it.readFloat())
                assertEquals(true, it.readBoolean())
                assertEquals(32767, it.readShort())
                assertEquals(127, it.readByte())
                assertEquals('A', it.readChar())

                assertEquals("World", it.readString(stringLength))
                assertEquals(1234567890123456789, it.readLong())
                assertEquals(-42, it.readInt())
                assertEquals(1.6180339887, it.readDouble())
                assertEquals(0.333.toFloat(), it.readFloat())
                assertEquals(false, it.readBoolean())
                assertEquals(-32768, it.readShort())
                assertEquals(-128, it.readByte())
                assertEquals('Z', it.readChar())

                assertEquals("Test", it.readString(stringLength))
                assertEquals(0, it.readLong())
                assertEquals(0, it.readInt())
                assertEquals(0.0, it.readDouble())
                assertEquals(0.toFloat(), it.readFloat())
                assertEquals(true, it.readBoolean())
                assertEquals(0, it.readShort())
                assertEquals(0, it.readByte())
                assertEquals('X', it.readChar())

                assertEquals("Data", it.readString(stringLength))
                assertEquals(-9223372036854775807, it.readLong())
                assertEquals(-2147483648, it.readInt())
                assertEquals(-2.2250738585072014e-308, it.readDouble())
                assertEquals((-3.4e+38).toFloat(), it.readFloat())
                assertEquals(false, it.readBoolean())
                assertEquals(12345, it.readShort())
                assertEquals(42, it.readByte())
                assertEquals('b', it.readChar())
            }
        }
        binary.toFile().delete()
    }

    @Test
    fun customPathTest() {
        val testData = Path("src/test/resources/binary/allDatatypes.csv")
        val outputPath = Path("src/test/resources/tempOutput/allDatatypesCustom.csv")
        val datatypeMap = mapOf(
            Pair("String", DataType.STRING),
            Pair("Long", DataType.LONG),
            Pair("Int", DataType.INT),
            Pair("Double", DataType.DOUBLE),
            Pair("Float", DataType.FLOAT),
            Pair("Bool", DataType.BOOLEAN),
            Pair("Short", DataType.SHORT),
            Pair("Byte", DataType.BYTE),
            Pair("Char", DataType.CHAR),
        )
        val stringLength = 6

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength, outputPath)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(4, it.readInt())
                assertEquals(stringLength, it.readInt())

                assertEquals("Hello", it.readString(stringLength))
                assertEquals(9223372036854775807, it.readLong())
                assertEquals(2147483647, it.readInt())
                assertEquals(3.1415926535, it.readDouble())
                assertEquals(2.71.toFloat(), it.readFloat())
                assertEquals(true, it.readBoolean())
                assertEquals(32767, it.readShort())
                assertEquals(127, it.readByte())
                assertEquals('A', it.readChar())

                assertEquals("World", it.readString(stringLength))
                assertEquals(1234567890123456789, it.readLong())
                assertEquals(-42, it.readInt())
                assertEquals(1.6180339887, it.readDouble())
                assertEquals(0.333.toFloat(), it.readFloat())
                assertEquals(false, it.readBoolean())
                assertEquals(-32768, it.readShort())
                assertEquals(-128, it.readByte())
                assertEquals('Z', it.readChar())

                assertEquals("Test", it.readString(stringLength))
                assertEquals(0, it.readLong())
                assertEquals(0, it.readInt())
                assertEquals(0.0, it.readDouble())
                assertEquals(0.toFloat(), it.readFloat())
                assertEquals(true, it.readBoolean())
                assertEquals(0, it.readShort())
                assertEquals(0, it.readByte())
                assertEquals('X', it.readChar())
            }
        }
        assertEquals("tempOutput", binary.parent.name)
        binary.toFile().delete()
    }

    @Test
    fun paddingTest() {
        val test = Path("src/test/resources/tempOutput/test123.bin")
        val stringLen = 20
        test.toFile().outputStream().use { fileOutputStream ->
            DataOutputStream(fileOutputStream).use { dataOutputStream ->
                dataOutputStream.writeChars("Hello".padEnd(stringLen, '.'))
                dataOutputStream.writeChars("World. S".padEnd(stringLen, '.'))
            }
        }

        test.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use { dataInputStream ->
                assertEquals("Hello", dataInputStream.readString(stringLen))
                assertEquals("World. S", dataInputStream.readString(stringLen))
            }
        }
        assertTrue(test.toFile().delete())
    }
}
