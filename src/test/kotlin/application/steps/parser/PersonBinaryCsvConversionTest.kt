package application.steps.parser

import TEST_ZONE
import domain.shared.enums.LegacyMode
import domain.shared.enums.household.EconomicStatus
import domain.shared.enums.person.ChargingInfluence
import domain.shared.enums.person.Employment
import domain.shared.enums.person.Graduation
import domain.shared.enums.person.Sex
import domain.simulation.data.sharing.MutableSharingProvider
import domain.simulation.data.sharing.SharingProvider
import domain.simulation.data.sharing.SharingProviderId
import domain.simulation.data.drt.DrtProvider
import domain.simulation.data.drt.DrtProviderId
import domain.simulation.data.drt.MutableDrtProviderData
import domain.simulation.data.person.MutablePerson
import domain.simulation.data.person.PersonId
import domain.simulation.parser.binary.BinaryPersonReader
import domain.simulation.parser.parseMemberships
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.euros
import generateHousehold
import org.junit.jupiter.api.Test
import utils.binary.CSVBinaryConverter
import utils.binary.DataType
import utils.binary.WriteStrategy
import utils.csv.CsvParser
import utils.csv.boolean
import utils.csv.currency
import utils.csv.decode
import utils.csv.decodeName
import utils.csv.int
import utils.csv.unitShare
import java.io.DataOutputStream
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersonBinaryCsvConversionTest {

    val columns = PersonColumns()
    val personCSVParser = CsvParser { row ->
        MutablePerson(
            id = PersonId(row.invoke(columns.idColumn).toLong()),
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
            sharingMemberships.addAll(
                row("mobilityProviderCustomership").parseMemberships(sharingMap),
            )
            drtMemberships.addAll(
                row("mobilityProviderCustomership").parseMemberships(drtMap),
            )
        }
    }

    @Suppress("LongMethod")
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
                },
            ),
            Pair(
                "gender",
                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
                    dataOutputStream.writeInt(Sex.valueOf(element).code)
                },
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
                },
            ),
            Pair("graduation", DataType().INT),
            Pair(
                "mobilityProviderCustomership",
                WriteStrategy { dataOutputStream: DataOutputStream, element: String, _ ->
                    if ("BIKESHARING=true" in element) {
                        println("sharing: $element")
                        dataOutputStream.writeInt(1)
                        dataOutputStream.writeLong(sharingProvider1.id.value)
                    } else {
                        dataOutputStream.writeInt(0)
                    }

                    if ("Stadtmobil=true" in element) {
                        println("drt: $element")
                        dataOutputStream.writeInt(1)
                        dataOutputStream.writeLong(drtProvider1.id.value)
                    } else {
                        dataOutputStream.writeInt(0)
                    }
                },
            ),
            // TODO extend test
        )

        val personConverter = BinaryPersonReader(
            { _ -> hh1 },
            sharingId::getValue,
            drtId::getValue,
            1,
        )

        val testBin = binaryConverter.makeCSVBinary(testData, dataTypeMap, maxStringLength, tempOutput)

        assertTrue { testData.exists() }
        assertTrue { testBin.exists() }

        val binConverted = personConverter.fromBinary(testBin)
        val directRead = personCSVParser.parse(testData).toList()

        assertEquals(directRead.size, binConverted.size)

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
            assertEquals(mutablePerson.sharingMemberships, directRead[index].sharingMemberships)
            assertEquals(mutablePerson.drtMemberships, directRead[index].drtMemberships)
        }

        assertTrue {
            binConverted.any {
                it.sharingMemberships.isNotEmpty()
            }
        }

        assertTrue {
            binConverted.any {
                it.drtMemberships.isNotEmpty()
            }
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

    private val sharingProvider1: SharingProvider = MutableSharingProvider(SharingProviderId(1L)) {
        name = "BIKESHARING"
        mode = LegacyMode.BIKESHARING
    }
    private val sharingMap = mapOf(sharingProvider1.name.lowercase() to sharingProvider1)
    private val sharingId = mapOf(sharingProvider1.id to sharingProvider1)

    private val drtProvider1: DrtProvider = MutableDrtProviderData(DrtProviderId(1L)) {
        name = "Stadtmobil"
        mode = LegacyMode.RIDE_POOLING
    }
    private val drtMap = mapOf(drtProvider1.name.lowercase() to drtProvider1)
    private val drtId = mapOf(drtProvider1.id to drtProvider1)

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
