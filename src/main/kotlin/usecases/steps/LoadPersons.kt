package usecases.steps

import domain.data.Employment
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.Person
import domain.data.PersonId
import domain.data.Sex
import domain.data.SharingStation
import domain.data.SharingStationId
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
import utils.csv.id
import utils.csv.withFilter
import java.io.File

interface LoadPersonsContext : Context {
    val personRepository: MutableRepository<Person, PersonId>
    val householdRepository: Repository<Household, HouseholdId>
    val sharingStationsRepository: Repository<SharingStation, SharingStationId>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>

    val defaultPersonFile: File
        get() = File(demandFolder.path + "\\demand-data\\person.csv")
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
    personColumns: PersonColumns = PersonColumns(),
    // employmentCode: CodePlan<Employment>? = null, codes should be consistent within project, only specify once
    // sexCode: CodePlan<Sex>? = null,
    // graduationCode: CodePlan<Graduation>? = null,
    incomeUnit: CurrencyUnit? = null,
    filter: PersonColumns.(Row, C) -> Boolean = { _, _ -> true }

) where S : ModelExecution<C>, C : LoadPersonsContext {
//    val employmentCodePlan = this.context.employmentCodes
//    val sexCodePlan = this.context.sexCodes
//    val graduationCodePlan = this.context.graduationCodes
//    val currencyUnit = incomeUnit ?: this.context.costUnit
//
//    val householdRepo = { context.householdRepository }
//    val providersByNameFunction: () -> Map<String, Subscribable<Person>> = {
//        context.sharingStationsRepository.elements.map { it.owner }.distinct().associateBy { it.name.lowercase() }
//    }

    val csvParser = CsvParser<Person>(errorHandling) { row ->
        null
//        PersonBuilder().apply {
//            personId = row.long(personColumns.personIdColumn)
//            // TODO someone should verify which column is which "personId, personNumber" needs to map to id, personId
//            // Robin: I ran into an issue where planned activities could not be read , so I swapped the column order
//            id = PersonId(row.long(personColumns.idColumn))
//
//            val requestedHousehold = getHousehold(householdRepo, row, personColumns.householdColumn)
//            household = requestedHousehold
//            age = row.int(personColumns.ageColumn)
//            employment = row.decodeName(personColumns.employmentColumn, employmentCodePlan)
//            sex = row.decodeName(personColumns.sexColumn, sexCodePlan)
//            graduation = row.decode(personColumns.graduationColumn, graduationCodePlan)
//            income = row.int().currency(personColumns.incomeColumn, currencyUnit)
//            hasBike = row.boolean(personColumns.bikeColumn)
//            hasCommuterTicket = row.boolean(personColumns.commuterTicketColumn)
//            hasLicense = row.boolean(personColumns.licenseColumn)
//            eMobilityAcceptance = row.unitShare(personColumns.eMobilityAcceptanceColumn)
//            chargingInfluence = row.decodeName(personColumns.chargingInfluenceColumn, ChargingInfluence)
//            random = Random(seed = personId!! + context.simulationSeed)
//
//            memberships = parserMemberships(row, providersByNameFunction, requestedHousehold)
//        }
    }
    val internalFilter = { row: Row -> personColumns.filter(row, context) }

    this.preparePersonsFile(csvParser.withFilter(internalFilter), file, delimiter) // TODO
}

fun <S, C> S.preparePersonsFile(
    parser: CsvParser<Person>,
    file: File = context.defaultPersonFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadPersonsContext {
    this.addStep(
        LoadCsvStep<Person, PersonId>(
            file = file,
            name = "Load persons from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.personRepository,
            dependentRepositories = context.let {
                setOf(it.householdRepository, it.sharingStationsRepository,)
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

internal fun getHousehold(
    householdRepo: () -> Repository<Household, HouseholdId>,
    row: Row,
    householdColumn: String
) = requireNotNull(
    householdRepo().getById(row.id(householdColumn))
) {
    "Referenced household id ${row(householdColumn)} could not be found in householdRepo:" +
        " ${householdRepo().elements.map { it.id }.toList()}"
}

// private fun parserMemberships(
//    row: Row,
//    providersByNameFunction: () -> Map<String, Subscribable<Person>>,
//    requestedHousehold: Household
// ) = row("mobilityProviderCustomership")
//    .replace("{", "")
//    .replace("}", "")
//    .split(", ")
//    .map { it.split("=") }
//    .filter { it[0].lowercase() in providersByNameFunction() }
//    .associate { membership ->
//        requireNotNull(
//            providersByNameFunction()[membership[0].lowercase()]
//        ) to membership[1].toBoolean()
//    }.toMutableMap().apply {
//        put(requestedHousehold, true)
//    }
