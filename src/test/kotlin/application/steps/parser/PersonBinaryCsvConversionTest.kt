package application.steps.parser

import TEST_ZONE
import domain.synthesis.data.ChargingInfluence
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Sex
import domain.synthesis.parser.binary.BinaryPersonReader
import generateHousehold
import org.junit.jupiter.api.Test
import units.CurrencyUnit
import units.euros
import utils.binary.CSVBinaryConverter
import utils.binary.DataType
import utils.binary.WriteStrategy
import utils.csv.CsvParser
import utils.csv.boolean
import utils.csv.currency
import utils.csv.decode
import utils.csv.decodeName
import utils.csv.id
import utils.csv.int
import utils.csv.unitShare
import java.io.DataOutputStream
import kotlin.io.path.Path
import kotlin.test.assertEquals

class PersonBinaryCsvConversionTest {

    val columns = PersonColumns()
    val personCSVParser = CsvParser.Companion { row ->
        MutablePerson(
            id = row.id(columns.idColumn),
            household = hh1,
            1,
        ) {
            age = row.int(columns.ageColumn)
            employment = row.decodeName(columns.employmentColumn, Employment.Companion)
            sex = row.decodeName(columns.sexColumn, Sex.Companion)
            graduation = row.decode(columns.graduationColumn, Graduation.Companion)
            income = row.int().currency(columns.incomeColumn, CurrencyUnit.EUROS)
            hasBike = row.boolean(columns.bikeColumn)
            hasCommuterTicket = row.boolean(columns.commuterTicketColumn)
            hasLicense = row.boolean(columns.licenseColumn)
            eMobilityAcceptance = row.unitShare(columns.eMobilityAcceptanceColumn)
            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence.Companion)
        }
    }

    @Test
    fun `binary vs CSV parsing`() {
        val testData = Path("src/test/resources/testDemand/demand-data/person.csv")
        val tempOutput = Path("src/test/resources/tempOutput/person.bin")
        tempOutput.toFile().delete()
        val binaryConverter = CSVBinaryConverter()

        val maxStringLength = 200
        val dataTypeMap = mapOf(
            Pair("personId", DataType().LONG),
            Pair("householdId", DataType().LONG),
            Pair("age", DataType().INT),
            Pair(
                "employment",
                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
                    dataOutputStream.writeInt(Employment.valueOf(element).code)
                }
            ),
            Pair(
                "gender",
                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
                    dataOutputStream.writeInt(Sex.valueOf(element).code)
                }
            ),
            Pair("income", DataType().DOUBLE),
            Pair("hasBike", DataType().BOOLEAN),
            Pair("hasCommuterTicket", DataType().BOOLEAN),
            Pair("hasLicense", DataType().BOOLEAN),
            Pair("eMobilityAcceptance", DataType().DOUBLE),
            Pair(
                "chargingInfluencesDestinationChoice",
                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
                    dataOutputStream.writeInt(ChargingInfluence.valueOf(element).code)
                }
            ),
            Pair("graduation", DataType().INT),
        )

        val personConverter = BinaryPersonReader({ _ -> hh1 }, 1)

        val testBin = binaryConverter.makeCSVBinary(testData, dataTypeMap, maxStringLength, tempOutput)

        val binConverted = personConverter.fromBinary(testBin)
        val directRead = personCSVParser.parse(testData).toList()

        binConverted.forEachIndexed { index, mutablePerson ->
            assertEquals(mutablePerson.id, directRead[index].id)
            assertEquals(mutablePerson.household, directRead[index].household)
            assertEquals(mutablePerson.age, directRead[index].age)
            assertEquals(mutablePerson.hasBike, directRead[index].hasBike)
            assertEquals(mutablePerson.sex, directRead[index].sex)
            assertEquals(mutablePerson.chargingInfluence, directRead[index].chargingInfluence)
            assertEquals(mutablePerson.employment, directRead[index].employment)
            assertEquals(mutablePerson.graduation, directRead[index].graduation)
            assertEquals(mutablePerson.income, directRead[index].income)
            assertEquals(mutablePerson.hasCommuterTicket, directRead[index].hasCommuterTicket)
            assertEquals(mutablePerson.hasLicense, directRead[index].hasLicense)
            assertEquals(mutablePerson.eMobilityAcceptance, directRead[index].eMobilityAcceptance)
        }
        tempOutput.toFile().delete()
    }

    private val zone = TEST_ZONE
    private val hh1 = zone.generateHousehold(1L) {
        householdNumber = 2
        type = 2
        domCode = 42
        surveyYear = 12025
        economicStatus = EconomicStatus.MIDDLE
        incomePerMonth = 2.euros
    }

    data class PersonColumns(
        val idColumn: String = "personId",
        val personIdColumn: String = "personNumber",
        val householdColumn: String = "householdId",
        val ageColumn: String = "age",
        val employmentColumn: String = "employment",
        val sexColumn: String = "gender",
        val graduationColumn: String = "graduation",
        val incomeColumn: String = "income",
        val bikeColumn: String = "hasBike",
        val commuterTicketColumn: String = "hasCommuterTicket",
        val licenseColumn: String = "hasLicense",
        val eMobilityAcceptanceColumn: String = "eMobilityAcceptance",
        val chargingInfluenceColumn: String = "chargingInfluencesDestinationChoice",
    )
}
