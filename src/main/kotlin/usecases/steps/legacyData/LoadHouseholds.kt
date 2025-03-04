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
fun LoadHouseholdContext.prepareHouseholds(
    file: File = defaultHouseholdFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: HouseholdColumns = HouseholdColumns(),
    roadPositionParser: (String) -> Location = String::parseRoadPosition,
    incomeUnit: CurrencyUnit = costUnit,
    filter: HouseholdColumns.(Row) -> Boolean = { true }
) {
    val parser = CsvParser<MutableHousehold>(errorHandling) { row ->

        MutableHousehold(
            id = row.id(columns.hhIdColumn),
            simulationSeed,
        ) {
            householdNumber = row.long(columns.hhNumberColumn)
            surveyYear = row.int(columns.yearColumn)
            domCode = row.int(columns.domCodeColumn)
            type = row.int(columns.typeColumn)
            incomePerMonth = row.currency(columns.incomeColumn, incomeUnit)
            economicStatus = row.decode(columns.economicalStatusColumn, economicalStatusCodes)

            // Robin: I converted this builder call to the location as found in [Household]
            location = row(columns.locationColumn, roadPositionParser).withZone(
                getLegacyZone(row.int(columns.zoneColumn))
            )
        }
    }
    val filterWrap: (Row) -> Boolean = { columns.filter(it) }

    this.prepareHouseholdsFile(parser.withFilter(filterWrap), file, delimiter)
}

fun LoadHouseholdContext.prepareHouseholdsFile(
    parser: CsvParser<MutableHousehold>,
    file: File = defaultHouseholdFile,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep(
        file = file,
        name = "Load households from csv",
        parser = parser,
        delimiter = delimiter,
        repository = householdRepository,
        dependentRepositories = setOf(zoneRepository),
        validationMock = listOf() // TODO
    )
}

fun LoadHouseholdContext.finishHouseholds() = runStep {
    SealStep(householdRepository)
}

fun LoadHouseholdContext.loadHouseholds(
    file: File = defaultHouseholdFile,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    filter: HouseholdColumns.(Row) -> Boolean = {
        true
    }
) {
    this.prepareHouseholds(file = file, errorHandling = errorHandling, filter = filter)
    this.finishHouseholds()
}

fun LoadHouseholdContext.filterHouseholds(valid: Collection<HouseholdId>) = runStep {
    FilterHouseholds(this, valid)
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
