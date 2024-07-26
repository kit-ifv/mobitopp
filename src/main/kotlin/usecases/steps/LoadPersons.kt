package usecases.steps

import domain.data.ChargingInfluence
import domain.data.Employment
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.Sex
import domain.enums.LegacyActivityType
import modeling.steps.AddCsvStep
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.Repository
import modeling.steps.RepositoryState
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
import utils.csv.long
import utils.csv.unitShare
import utils.csv.withFilter
import java.io.File
import kotlin.random.Random

@Suppress("LongParameterList")
fun <S, C> S.preparePersons(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    personColumns: PersonColumns = PersonColumns(),
    employmentCode: CodePlan<Employment>? = null,
    sexCode: CodePlan<Sex>? = null,
    graduationCode: CodePlan<Graduation>? = null,
    incomeUnit: CurrencyUnit? = null,
    filter: PersonColumns.(Row) -> Boolean = { true }
) where S : ModelExecution<C>, C : Context, C : HouseholdContext, C : PersonContext {
    val employmentCodePlan = employmentCode ?: this.context.employmentCodes
    val sexCodePlan = sexCode ?: this.context.sexCodes
    val graduationCodePlan = graduationCode ?: this.context.graduationCodes
    val currencyUnit = incomeUnit ?: this.context.costUnit

    val householdRepo = { context.householdRepository }

    val csvParser = CsvParser(errorHandling) { row ->
        PersonBuilder().apply {
            personId = row.long(personColumns.personIdColumn)
            // TODO someone should verify which column is which "personId, personNumber" needs to map to id, personId
            // Robin: I ran into an issue where planned activities could not be read , so I swapped the column order
            id = PersonId(row.long(personColumns.idColumn))
            household = getHousehold(householdRepo, row, personColumns.householdColumn)
            age = row.int(personColumns.ageColumn)
            employment = row.decodeName(personColumns.employmentColumn, employmentCodePlan)
            sex = row.decodeName(personColumns.sexColumn, sexCodePlan)
            graduation = row.decode(personColumns.graduationColumn, graduationCodePlan)
            income = row.int().currency(personColumns.incomeColumn, currencyUnit)
            hasBike = row.boolean(personColumns.bikeColumn)
            hasCommuterTicket = row.boolean(personColumns.commuterTicketColumn)
            hasLicense = row.boolean(personColumns.licenseColumn)
            eMobilityAcceptance = row.unitShare(personColumns.eMobilityAcceptanceColumn)
            chargingInfluence = row.decodeName(personColumns.chargingInfluenceColumn, ChargingInfluence)
            random = Random(seed = personId!! + context.simulationSeed)
        }
    }
    val internalFilter = { row: Row -> personColumns.filter(row) }
    this.preparePersonsFile(csvParser.withFilter(internalFilter), file, delimiter)
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

fun <S, C> S.preparePersonsFile(
    parser: CsvParser<PersonBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : PersonContext {
    val path = this.context.demandFolder.path + "\\demand-data\\person.csv"
    val personFile = file ?: File(path)

    val resource = CsvResource(personFile, parser, delimiter)

    this.addStep(
        AddCsvStep(
            name = "load person csv",
            csv = resource,
            repository = context.personRepository
        )
    )
}

fun <S, C> S.finishPersons() where S : ModelExecution<C>, C : Context, C : PersonContext {
    this.addStep(BuildStep("finish persons", context.personRepository))
}

fun <S, C> S.loadPersons(
    file: File? = null,
    filter: PersonColumns.(Row) -> Boolean = {
        true
    }
) where S : ModelExecution<C>, C : Context, C : HouseholdContext, C : PersonContext {
    this.preparePersons(file = file, filter = filter)
    this.finishPersons()
}

fun <S, C> S.assignHomeLocations() where S : ModelExecution<C>, C : Context, C : LegacyContext, C : ActivityContext {
    this.addStep(
        CustomStep(
            "assign HOME to Household",
            validation = {
                context.personRepository.state == RepositoryState.FINISHED &&
                    context.plannedActivityRepository.state == RepositoryState.FINISHED
            },
            exec = {
                context.personRepository.elements.forEach {
                    it.schedule.activities().filter { act -> act.type == LegacyActivityType.HOME }
                        .forEach { home -> home.location = it.household.location }
                    println(it)
                }
            }
        )
    )
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
