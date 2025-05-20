package usecases.steps

import domain.data.ChargingInfluence
import domain.data.Employment
import domain.data.Graduation
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson
import domain.data.PersonId
import domain.data.Sex
import domain.data.SharingProvider
import domain.data.SharingProviderId
import domain.enums.ActivityType
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import units.CurrencyUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.currency
import utils.csv.decode
import utils.csv.decodeName
import utils.csv.id
import utils.csv.int
import utils.csv.unitShare
import utils.csv.withFilter
import java.nio.file.Path

interface LoadPersonsContext : Context {
    val personRepository: MutableRepository<MutablePerson, PersonId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>

    val homeActivityType: ActivityType

    val defaultPersonPath: Path
        get() = demandFolder.resolve("demand-data").resolve("person.csv")

    fun getHousehold(
        row: Row,
        householdColumn: String
    ) = requireNotNull(
        householdRepository[row.id(householdColumn)]
    ) {
        "Referenced household id ${row(householdColumn)} could not be found in householdRepo:" +
            " ${householdRepository.elements.map { it.id }.toList()}"
    }
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

@Suppress("LongParameterList", "UnusedParameter")
fun LoadPersonsContext.preparePersons(
    path: Path = defaultPersonPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: PersonColumns = PersonColumns(),
    incomeUnit: CurrencyUnit = costUnit,
    filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { _, _ -> true }
) {
    val providersByNameFunction: () -> Map<String, SharingProvider> = {
        sharingProviderRepository.elements.associateBy { it.name.lowercase() }
    }

    val csvParser = CsvParser<MutablePerson>(errorHandling) { row ->

        MutablePerson(
            id = row.id(columns.idColumn),
            household = getHousehold(row, columns.householdColumn),
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
            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence)
            sharingMemberships.addAll(
                parseMemberships(row, providersByNameFunction)
            )
        }
    }

    val internalFilter = { row: Row -> columns.filter(row, this) }
    this.preparePersonsFile(csvParser.withFilter(internalFilter), path, delimiter)
}

fun LoadPersonsContext.preparePersonsFile(
    parser: CsvParser<MutablePerson>,
    path: Path = defaultPersonPath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<MutablePerson, PersonId>(
        path = path,
        name = "Load persons from csv",
        parser = parser,
        delimiter = delimiter,
        repository = personRepository,
        dependentRepositories = setOf(householdRepository, sharingProviderRepository),
        validationMock = listOf() // TODO
    )
}

fun LoadPersonsContext.finishPersons() = runStep {
    SealStep(personRepository)
}

fun LoadPersonsContext.loadPersons(
    path: Path = defaultPersonPath,
    filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { _, _ -> true }
) {
    this.preparePersons(path = path, filter = filter)
    this.finishPersons()
}

private fun parseMemberships(
    row: Row,
    providersByNameFunction: () -> Map<String, SharingProvider>,
) = row("mobilityProviderCustomership")
    .replace("{", "")
    .replace("}", "")
    .split(", ")
    .map { it.split("=") }
    .filter { it[0].lowercase() in providersByNameFunction() }
    .filter { it[1].toBoolean() }
    .map { membership ->
        requireNotNull(
            providersByNameFunction()[membership[0].lowercase()]
        ) {
            "Could not find sharing provider named ${membership[0]}"
        }
    }
