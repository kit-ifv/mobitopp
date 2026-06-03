package domain.synthesis.parser

import domain.synthesis.data.SharingProvider
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.person.ChargingInfluence
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.person.MutablePerson
import domain.synthesis.data.person.PersonId
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.euros
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.boolean
import utils.csv.currencyOrNull
import utils.csv.decodeName
import utils.csv.decodeOrNull
import utils.csv.int
import utils.csv.long
import utils.csv.unitShare
import utils.csv.withFilter

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

data class PersonCodePlans(
    val employmentCodes: CodePlan<Employment> = Employment,
    val graduationCodes: CodePlan<Graduation> = Graduation,
    val sexCodes: CodePlan<Sex> = Sex,
)

data class PersonCsvConfig(
    var columns: PersonColumns = PersonColumns(),
    var sharingProvidersByName: () -> Map<String, SharingProvider>,
    var drtProvidersByName: () -> Map<String, DrtProvider>,
    var householdProvider: (HouseholdId) -> MutableHousehold,
    var hasHousehold: (HouseholdId) -> Boolean,
    var incomeUnit: CurrencyUnit,
    var employmentCodes: CodePlan<Employment> = Employment,
    var graduationCodes: CodePlan<Graduation> = Graduation,
    var sexCodes: CodePlan<Sex> = Sex,
    var errorHandling: ErrorHandling,
    val seed: Long,
)

@Suppress("LongParameterList")
fun createPersonCsvParser(csvConfig: PersonCsvConfig): CsvParser<MutablePerson> = csvConfig.run {
    val csvParser = CsvParser<MutablePerson>(errorHandling) { row ->

        MutablePerson(
            id = PersonId(row.long(columns.idColumn)),
            household = householdProvider(HouseholdId(row.long(columns.householdColumn))),
            seed,
        ) {
            age = row.int(columns.ageColumn)
            employment = row.decodeName(columns.employmentColumn, employmentCodes)
            sex = row.decodeName(columns.sexColumn, sexCodes)
            graduation = row.decodeOrNull(columns.graduationColumn, graduationCodes) ?: Graduation.UNDEFINED
            income = row.int().currencyOrNull(columns.incomeColumn, incomeUnit) ?: (-1).euros // TODO warn on default
            hasBike = row.boolean(columns.bikeColumn)
            hasCommuterTicket = row.boolean(columns.commuterTicketColumn)
            hasLicense = row.boolean(columns.licenseColumn)
            eMobilityAcceptance = row.unitShare(columns.eMobilityAcceptanceColumn)
            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence.Companion)

            val sharingProviders = sharingProvidersByName()
            sharingMemberships.addAll(
                row(
                    columns.membershipColumn,
                ).parseMemberships(sharingProviders), // TODO lambda (Row) -> List<Provider> as csv parameter
            )

            val drtProviders = drtProvidersByName()
            drtMemberships.addAll(
                row(columns.membershipColumn).parseMemberships(drtProviders),
            )
        }
    }

    return csvParser.withFilter { row ->
        hasHousehold(
            HouseholdId(row.long(columns.householdColumn)),
        )
    }
}

fun <R> String.parseMemberships(providersByName: Map<String, R>) = this
    .replace("{", "")
    .replace("}", "")
    .split(", ")
    .map { it.split("=") }
    .filter { it[0].lowercase() in providersByName }
    .filter { it[1].toBoolean() }
    .mapNotNull { membership ->
        providersByName[membership[0].lowercase()]
    }
