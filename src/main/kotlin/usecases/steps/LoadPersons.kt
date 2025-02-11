package usecases.steps

import domain.data.ChargingInfluence
import domain.data.Employment
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson
import domain.data.Person
import domain.data.PersonId
import domain.data.Sex
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.enums.ActivityType
import domain.resources.Subscribable
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
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
import java.io.File

interface LoadPersonsContext : Context {
    val personRepository: MutableRepository<MutablePerson, PersonId>
    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val sharingStationsRepository: Repository<SharingStation, SharingStationId>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>

    val homeActivityType: ActivityType

    val defaultPersonFile: File
        get() = File(demandFolder.path + "\\demand-data\\person.csv")

    fun getHousehold(
        row: Row,
        householdColumn: String
    ) = requireNotNull(
        householdRepository.getById(row.id(householdColumn))
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
fun <S, C> S.preparePersons(
    file: File = context.defaultPersonFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: PersonColumns = PersonColumns(),
    incomeUnit: CurrencyUnit = context.costUnit,
    filter: PersonColumns.(Row, C) -> Boolean = { _, _ -> true }

) where S : ModelExecution<C>, C : LoadPersonsContext {
    val providersByNameFunction: () -> Map<String, Subscribable<Person>> = {
        context.sharingStationsRepository.elements.map { it.owner }.distinct().associateBy { it.name.lowercase() }
    }

    val csvParser = CsvParser<MutablePerson>(errorHandling) { row ->

        MutablePerson(
            id = row.id(columns.idColumn),
            household = context.getHousehold(row, columns.householdColumn),
            context.simulationSeed,
        ) {
            age = row.int(columns.ageColumn)
            employment = row.decodeName(columns.employmentColumn, context.employmentCodes)
            sex = row.decodeName(columns.sexColumn, context.sexCodes)
            graduation = row.decode(columns.graduationColumn, context.graduationCodes)
            income = row.int().currency(columns.incomeColumn, incomeUnit)
            hasBike = row.boolean(columns.bikeColumn)
            hasCommuterTicket = row.boolean(columns.commuterTicketColumn)
            hasLicense = row.boolean(columns.licenseColumn)
            eMobilityAcceptance = row.unitShare(columns.eMobilityAcceptanceColumn)
            chargingInfluence = row.decodeName(columns.chargingInfluenceColumn, ChargingInfluence)
            memberships.putAll(
                parseMemberships(row, providersByNameFunction, household)
            )
        }
    }

    val internalFilter = { row: Row -> columns.filter(row, context) }
    this.preparePersonsFile(csvParser.withFilter(internalFilter), file, delimiter)
}

fun <S, C> S.preparePersonsFile(
    parser: CsvParser<MutablePerson>,
    file: File = context.defaultPersonFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadPersonsContext {
    this.addStep(
        LoadCsvStep<MutablePerson, PersonId>(
            file = file,
            name = "Load persons from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.personRepository,
            dependentRepositories = context.let {
                setOf(it.householdRepository, it.sharingStationsRepository)
            },
            validationMock = listOf() // TODO
        )
    )
}

fun <S, C> S.finishPersons() where S : ModelExecution<C>, C : LoadPersonsContext {
    this.addStep(SealStep(context.personRepository))
}

fun <S, C> S.loadPersons(
    file: File = context.defaultPersonFile,
    filter: PersonColumns.(Row, C) -> Boolean = { _, _ -> true }
) where S : ModelExecution<C>, C : LoadPersonsContext {
    this.preparePersons(file = file, filter = filter)
    this.finishPersons()
}

private fun parseMemberships(
    row: Row,
    providersByNameFunction: () -> Map<String, Subscribable<Person>>,
    requestedHousehold: Household
) = row("mobilityProviderCustomership")
    .replace("{", "")
    .replace("}", "")
    .split(", ")
    .map { it.split("=") }
    .filter { it[0].lowercase() in providersByNameFunction() }
    .associate { membership ->
        requireNotNull(
            providersByNameFunction()[membership[0].lowercase()]
        ) to membership[1].toBoolean()
    }.toMutableMap().apply {
        put(requestedHousehold, true)
    }
