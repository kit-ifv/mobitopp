package domain.synthesis.parser

import domain.shared.config.SynthesisContext
import domain.synthesis.data.ChargingInfluence
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.PersonId
import domain.synthesis.data.Sex
import domain.synthesis.data.SharingProvider
import edu.kit.ifv.units.CurrencyUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultCsvParser
import utils.csv.boolean
import utils.csv.currency
import utils.csv.decode
import utils.csv.decodeName
import utils.csv.int
import utils.csv.long
import utils.csv.unitShare

@Suppress("LongParameterList")
fun PersonCsvContext.personCsvParser(
    errorHandling: ErrorHandling,
    columns: PersonColumns,
    incomeUnit: CurrencyUnit,
    sharingProvidersByName: () -> Map<String, SharingProvider>,
    drtProvidersByName: () -> Map<String, DrtProvider>,
    householdProvider: (HouseholdId) -> MutableHousehold,
): DefaultCsvParser<MutablePerson> {
    val csvParser = CsvParser.Companion<MutablePerson>(errorHandling) { row ->

        MutablePerson(
            id = PersonId(row.long(columns.idColumn)),
            household = householdProvider(HouseholdId(row.long(columns.householdColumn))),
            simulationSeed,
        ) {
            age = row.int(columns.ageColumn)
            employment = row.decodeName(columns.employmentColumn, employmentCodes)
            sex = row.decodeName(columns.sexColumn, sexCodes)
            graduation = row.decode(columns.graduationColumn, graduationCodes)
            income = row.int().currency(columns.incomeColumn, incomeUnit)
            hasBike = row.boolean(columns.bikeColumn)
            hasCommuterTicket = row.boolean(columns.commuterTicketColumn)
            hasLicense = row.boolean(columns.licenseColumn)
            eMobilityAcceptance = row.unitShare(columns.eMobilityAcceptanceColumn)
            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence.Companion)

            val sharingProviders = sharingProvidersByName()
            sharingMemberships.addAll(
                row(
                    columns.membershipColumn
                ).parseMemberships(sharingProviders) // TODO lambda (Row) -> List<Provider> as csv parameter
            )

            val drtProviders = drtProvidersByName()
            drtMemberships.addAll(
                row(columns.membershipColumn).parseMemberships(drtProviders)
            )
        }
    }

    return csvParser
}

fun <R> String.parseMemberships(
    providersByName: Map<String, R>,
) = this
    .replace("{", "")
    .replace("}", "")
    .split(", ")
    .map { it.split("=") }
    .filter { it[0].lowercase() in providersByName }
    .filter { it[1].toBoolean() }
    .mapNotNull { membership ->
        providersByName[membership[0].lowercase()]
    }

interface PersonCsvContext : SynthesisContext {
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>
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
    val membershipColumn: String = "mobilityProviderCustomership",
)
