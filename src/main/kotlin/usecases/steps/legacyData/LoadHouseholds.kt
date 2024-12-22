package usecases.steps.legacyData

import domain.data.EconomicStatus
import domain.data.HouseholdId
import domain.data.LegacyZone
import domain.data.MutableHousehold
import domain.data.Zone
import domain.data.ZoneId
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.FilterIdsStep
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import modeling.validation.Warning
import modeling.validation.validateScope
import units.CurrencyUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.currency
import utils.csv.decode
import utils.csv.id
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter
import java.io.File

interface LoadHouseholdContext : Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>

    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val economicalStatusCodes: CodePlan<EconomicStatus>

    val defaultHouseholdFile: File
        get() = File(demandFolder.path + "\\demand-data\\household.csv")

    fun getLegacyZone(
        matrixColumn: Int
    ) = requireNotNull(
        zoneColumnIndex[matrixColumn]
    ) {
        "Could not find zone with matrix column $matrixColumn " +
            "in index: ${zoneColumnIndex.keys}"
    }
}

/**
 * ROBIN: I think extracting a parameter object may be helpful to avoid the long parameter list, and makes the method
 * significantly more readable.
 */
data class HouseholdColumns(
    val hhNumberColumn: String = "householdNumber",
    val hhIdColumn: String = "householdId",
    val yearColumn: String = "year",
    val zoneColumn: String = "homeZone",
    val locationColumn: String = "homeLocation",
    val domCodeColumn: String = "domCode",
    val typeColumn: String = "type",
    val incomeColumn: String = "income",
    val economicalStatusColumn: String = "economicalStatus",
)

@Suppress("LongParameterList", "UnusedParameter")
fun <S, C> S.prepareHouseholds(
    file: File = context.defaultHouseholdFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: HouseholdColumns = HouseholdColumns(),
    roadPositionParser: (String) -> Location = String::parseRoadPosition,
    incomeUnit: CurrencyUnit = context.costUnit,
//    economicalStatusCodes: CodePlan<EconomicStatus>? = null, //to be consistent within project only define in context once!
    filter: HouseholdColumns.(Row) -> Boolean = { true }
) where S : ModelExecution<C>, C : LoadHouseholdContext {
    // TODO
    val parser = CsvParser<MutableHousehold>(errorHandling) { row ->

        MutableHousehold(
            id = row.id(columns.hhIdColumn),
            context.simulationSeed,
        ) {
            householdNumber = row.long(columns.hhNumberColumn)
            surveyYear = row.int(columns.yearColumn)
            domCode = row.int(columns.domCodeColumn)
            type = row.int(columns.typeColumn)
            incomePerMonth = row.currency(columns.incomeColumn, incomeUnit)
            economicStatus = row.decode(columns.economicalStatusColumn, context.economicalStatusCodes)
            name = "Household: $householdNumber"

            // Robin: I converted this builder call to the location as found in [Household]
            location = row(columns.locationColumn, roadPositionParser).withZone(
                context.getLegacyZone(row.int(columns.zoneColumn))
            )
        }
    }
    val filterWrap: (Row) -> Boolean = { columns.filter(it) }

    this.prepareHouseholdsFile(parser.withFilter(filterWrap), file, delimiter)
}

fun <S, C> S.prepareHouseholdsFile(
    parser: CsvParser<MutableHousehold>,
    file: File = context.defaultHouseholdFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadHouseholdContext {
    this.addStep(
        LoadCsvStep(
            file = file,
            name = "Load households from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.householdRepository,
            dependentRepositories = setOf(context.zoneRepository),
            validationMock = listOf() // TODO
        )
    )
}

fun <S, C> S.finishHouseholds() where S : ModelExecution<C>, C : LoadHouseholdContext {
    this.addStep(SealStep(context.householdRepository))
}

fun <S, C> S.loadHouseholds(
    file: File = context.defaultHouseholdFile,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    filter: HouseholdColumns.(Row) -> Boolean = {
        true
    }
) where S : ModelExecution<C>, C : Context, C : LoadHouseholdContext {
    this.prepareHouseholds(file = file, errorHandling = errorHandling, filter = filter)
    this.finishHouseholds()
}

fun <S, C> S.filterHouseholds(valid: Collection<HouseholdId>) where S : ModelExecution<C>, C : LoadHouseholdContext {
    this.addStep(
        FilterHouseholds(context, valid)
    )
}

class FilterHouseholds(
    context: LoadHouseholdContext,
    private val valid: Collection<HouseholdId>,
) : FilterIdsStep<MutableHousehold, HouseholdId>() {

    override val name: String = "filter household ids"
    override val repository: MutableRepository<MutableHousehold, HouseholdId> = context.householdRepository

    override fun check(id: HouseholdId): Boolean = id in valid

    override val dependentRepositories: Set<Repository<*, *>> = setOf()
    override fun verifyInput(): Warning? = validateScope { }
}
