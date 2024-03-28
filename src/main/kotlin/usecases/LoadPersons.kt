package usecases

import CodePlan
import domain.data.ChargingInfluence
import domain.data.EMobilityPersonDataBuilder
import domain.data.Employment
import domain.data.Graduation
import domain.data.Sex
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.EMobilityPersonContext
import modeling.steps.HouseholdContext
import modeling.steps.ModelExecution
import modeling.steps.PrepareCsvStep
import units.CurrencyUnit
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.currency
import utils.csv.decode
import utils.csv.decodeName
import utils.csv.id
import utils.csv.int
import utils.csv.long
import utils.csv.unitShare
import java.io.File

@Suppress("LongParameterList")
fun <S, C> S.prepareEmobilityPersons(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    idColumn: String = "personId",
    householdColumn: String = "householdId",
    ageColumn: String = "age",
    employmentCode: CodePlan<Employment>? = null,
    employmentColumn: String = "employment",
    sexCode: CodePlan<Sex>? = null,
    sexColumn: String = "gender",
    graduationCode: CodePlan<Graduation>? = null,
    graduationColumn: String = "graduation",
    incomeUnit: CurrencyUnit? = null,
    incomeColumn: String = "income",
    bikeColumn: String = "hasBike",
    commuterTicketColumn: String = "hasCommuterTicket",
    licenseColumn: String = "hasLicense",
    eMobilityAcceptanceColumn: String = "eMobilityAcceptance",
    chargingInfluenceColumn: String = "chargingInfluencesDestinationChoice",
) where S: ModelExecution<C>, C: Context, C: HouseholdContext, C: EMobilityPersonContext  {

    val employmentCodePlan = employmentCode ?: this.context.employmentCodes
    val sexCodePlan = sexCode ?: this.context.sexCodes
    val graduationCodePlan = graduationCode ?: this.context.graduationCodes
    val currencyUnit = incomeUnit ?: this.context.currencyUnit

    val householdRepo = { context.householdRepository }

    val csvParser = CsvParser(errorHandling) { row ->
        EMobilityPersonDataBuilder(
            personId = row.long(idColumn),
            householdData = requireNotNull(
                householdRepo().getById(row.id(householdColumn))
            ) {"Referenced household id ${row(householdColumn)} could not be found in householdRepo:" +
                " ${householdRepo().elements.map { it.id }.toList()}"
              },
            age = row.int(ageColumn),
            employment = row.decodeName(employmentColumn, employmentCodePlan),
            sex = row.decodeName(sexColumn, sexCodePlan),
            graduation = row.decode(graduationColumn, graduationCodePlan),
            income = row.int().currency(incomeColumn, currencyUnit),
            hasBike = row.boolean(bikeColumn),
            hasCommuterTicket = row.boolean(commuterTicketColumn),
            hasLicense = row.boolean(licenseColumn),
            eMobilityAcceptance = row.unitShare(eMobilityAcceptanceColumn),
            chargingInfluence = row.decodeName(chargingInfluenceColumn, ChargingInfluence)
        )
    }

    this.preparePersonsFile(csvParser, file, delimiter)
}

fun <S, C> S.preparePersonsFile(
    parser: CsvParser<EMobilityPersonDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S: ModelExecution<C>, C: Context, C: EMobilityPersonContext {
    val personFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\person.csv")

    val resource = CsvResource(personFile, parser, delimiter)

    this.addStep(
        PrepareCsvStep(
            name = "load person csv",
            csv=resource,
            repository = context.personRepository
        )
    )
}

fun <S, C> S.finishPersons() where S: ModelExecution<C>, C: Context, C: EMobilityPersonContext {
    this.addStep(BuildStep("finish persons", context.personRepository))
}

fun <S, C> S.loadPersons() where S: ModelExecution<C>, C: Context, C: HouseholdContext, C: EMobilityPersonContext {
    this.prepareEmobilityPersons()
    this.finishPersons()
}
