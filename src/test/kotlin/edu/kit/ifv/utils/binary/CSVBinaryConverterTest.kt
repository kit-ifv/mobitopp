package edu.kit.ifv.utils.binary
import org.junit.jupiter.api.Test
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
            Pair("bool", DataType().BOOLEAN),
            Pair("float", DataType().FLOAT),
            Pair("index", DataType().INT),
            Pair("str", DataType().STRING),
            Pair("int", DataType().INT),
        )
        val stringLength = 20

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(0L, it.readLong())
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

    @Suppress("LongMethod")
    @Test
    fun allTypesTest() {
        val testData = Path("src/test/resources/binary/allDatatypes.csv")
        val datatypeMap = mapOf(
            Pair("String", DataType().STRING),
            Pair("Long", DataType().LONG),
            Pair("Int", DataType().INT),
            Pair("Double", DataType().DOUBLE),
            Pair("Float", DataType().FLOAT),
            Pair("Bool", DataType().BOOLEAN),
            Pair("Short", DataType().SHORT),
            Pair("Byte", DataType().BYTE),
            Pair("Char", DataType().CHAR),
        )
        val stringLength = 6

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(0L, it.readLong())
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
            Pair("String", DataType().STRING),
            Pair("Long", DataType().LONG),
            Pair("Int", DataType().INT),
            Pair("Double", DataType().DOUBLE),
            Pair("Float", DataType().FLOAT),
            Pair("Bool", DataType().BOOLEAN),
            Pair("Short", DataType().SHORT),
            Pair("Byte", DataType().BYTE),
            Pair("Char", DataType().CHAR),
        )
        val stringLength = 6

        val binary = CSVBinaryConverter().makeCSVBinary(testData, datatypeMap, stringLength, outputPath)
        binary.toFile().inputStream().use { fileInputStream ->
            DataInputStream(fileInputStream).use {
                assertEquals(0L, it.readLong())
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
//
//    val columns = PersonColumns()
//    val personCSVParser = CsvParser { row ->
//        MutablePerson(
//            id = row.id(columns.idColumn),
//            household = hh1,
//            1,
//        ) {
//            age = row.int(columns.ageColumn)
//            employment = row.decodeName(columns.employmentColumn, Employment)
//            sex = row.decodeName(columns.sexColumn, Sex)
//            graduation = row.decode(columns.graduationColumn, Graduation)
//            income = row.int().currency(columns.incomeColumn, CurrencyUnit.EUROS)
//            hasBike = row.boolean(columns.bikeColumn)
//            hasCommuterTicket = row.boolean(columns.commuterTicketColumn)
//            hasLicense = row.boolean(columns.licenseColumn)
//            eMobilityAcceptance = row.unitShare(columns.eMobilityAcceptanceColumn)
//            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence)
//        }
//    }
//
//    @Test
//    fun `binary vs CSV parsing`() {
//        val testData = Path("src/test/resources/testDemand/demand-data/person.csv")
//        val tempOutput = Path("src/test/resources/tempOutput/person.bin")
//        tempOutput.toFile().delete()
//        val binaryConverter = CSVBinaryConverter()
//
//        val maxStringLength = 200
//        val dataTypeMap = mapOf(
//            Pair("personId", DataType().LONG),
//            Pair("householdId", DataType().LONG),
//            Pair("age", DataType().INT),
//            Pair(
//                "employment",
//                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
//                    dataOutputStream.writeInt(Employment.valueOf(element).code)
//                }
//            ),
//            Pair(
//                "gender",
//                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
//                    dataOutputStream.writeInt(Sex.valueOf(element).code)
//                }
//            ),
//            Pair("income", DataType().DOUBLE),
//            Pair("hasBike", DataType().BOOLEAN),
//            Pair("hasCommuterTicket", DataType().BOOLEAN),
//            Pair("hasLicense", DataType().BOOLEAN),
//            Pair("eMobilityAcceptance", DataType().DOUBLE),
//            Pair(
//                "chargingInfluencesDestinationChoice",
//                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
//                    dataOutputStream.writeInt(ChargingInfluence.valueOf(element).code)
//                }
//            ),
//            Pair("graduation", DataType().INT),
//        )
//
//        val personConverter = BinaryPersonReader({ _ -> hh1 }, 1)
//
//        val testBin = binaryConverter.makeCSVBinary(testData, dataTypeMap, maxStringLength, tempOutput)
//
//        val binConverted = personConverter.fromBinary(testBin)
//        val directRead = personCSVParser.parse(testData).toList()
//
//        binConverted.forEachIndexed { index, mutablePerson ->
//            assertEquals(mutablePerson.id, directRead[index].id)
//            assertEquals(mutablePerson.household, directRead[index].household)
//            assertEquals(mutablePerson.age, directRead[index].age)
//            assertEquals(mutablePerson.hasBike, directRead[index].hasBike)
//            assertEquals(mutablePerson.sex, directRead[index].sex)
//            assertEquals(mutablePerson.chargingInfluence, directRead[index].chargingInfluence)
//            assertEquals(mutablePerson.employment, directRead[index].employment)
//            assertEquals(mutablePerson.graduation, directRead[index].graduation)
//            assertEquals(mutablePerson.income, directRead[index].income)
//            assertEquals(mutablePerson.hasCommuterTicket, directRead[index].hasCommuterTicket)
//            assertEquals(mutablePerson.hasLicense, directRead[index].hasLicense)
//            assertEquals(mutablePerson.eMobilityAcceptance, directRead[index].eMobilityAcceptance)
//        }
//        tempOutput.toFile().delete()
//    }
//
//    private val zone = TEST_ZONE
//    private val hh1 = zone.generateHousehold(1L) {
//        householdNumber = 2
//        type = 2
//        domCode = 42
//        surveyYear = 12025
//        economicStatus = EconomicStatus.MIDDLE
//        incomePerMonth = 2.euros
//    }
//
//    data class PersonColumns(
//        val idColumn: String = "personId",
//        val personIdColumn: String = "personNumber",
//        val householdColumn: String = "householdId",
//        val ageColumn: String = "age",
//        val employmentColumn: String = "employment",
//        val sexColumn: String = "gender",
//        val graduationColumn: String = "graduation",
//        val incomeColumn: String = "income",
//        val bikeColumn: String = "hasBike",
//        val commuterTicketColumn: String = "hasCommuterTicket",
//        val licenseColumn: String = "hasLicense",
//        val eMobilityAcceptanceColumn: String = "eMobilityAcceptance",
//        val chargingInfluenceColumn: String = "chargingInfluencesDestinationChoice",
//    )
}
