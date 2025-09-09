package application.steps.parser.csv

import core.modelsteps.AddResourceStep
import core.modelsteps.FilterIdsStep
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.SealStep
import core.modelsteps.Warning
import core.modelsteps.validateScope
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.parseRoadPosition
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
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
import java.nio.file.Path

interface LoadHouseholdContext : DemandSimContext {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>

    val householdRepository: MutableRepository<MutableHousehold, HouseholdId>
    val economicalStatusCodes: CodePlan<EconomicStatus>

    val defaultHouseholdPath: Path
        get() = dataFolder.resolve("demand-data").resolve("household.csv")

    fun getLegacyZone(
        matrixColumn: Int,
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
    path: Path = defaultHouseholdPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: HouseholdColumns = HouseholdColumns(),
    roadPositionParser: (String) -> Location = String::parseRoadPosition,
    incomeUnit: CurrencyUnit = costUnit,
    filter: HouseholdColumns.(Row) -> Boolean = { true },
) = prepareHouseholds(
    HouseholdCsvConfig(
        path,
        delimiter,
        errorHandling,
        columns,
        roadPositionParser,
        incomeUnit,
        filter
    )
)


data class HouseholdCsvConfig(
    var path: Path,
    var delimiter: String = SEMICOLON,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var columns: HouseholdColumns = HouseholdColumns(),
    var roadPositionParser: (String) -> Location = String::parseRoadPosition,
    var incomeUnit: CurrencyUnit,
    var filter: HouseholdColumns.(Row) -> Boolean = { true },
)

@Suppress("LongParameterList", "UnusedParameter")
fun LoadHouseholdContext.prepareHouseholds(
    householdCsvConfig: HouseholdCsvConfig,
) {
    householdCsvConfig.run {
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

        this@prepareHouseholds.prepareHouseholdsFile(parser.withFilter(filterWrap), path, delimiter)
    }


}

context(source: Path)
fun LoadHouseholdContext.householdCsvConfig(lambda: HouseholdCsvConfig.() -> Unit): AddResourceStep<MutableHousehold, HouseholdId> {
    val config = HouseholdCsvConfig(path = source, incomeUnit = costUnit)
    config.apply(lambda)
    return config.run {
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

        LoadCsvStep(
            path = path,
            name = "Load households from csv",
            parser = parser.withFilter(filterWrap),
            delimiter = delimiter,
            repository = householdRepository,
            dependentRepositories = setOf(zoneRepository),
            validationMock = listOf() // TODO
        )
    }
}

fun LoadHouseholdContext.prepareHouseholdsFile(
    parser: CsvParser<MutableHousehold>,
    path: Path = defaultHouseholdPath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep(
        path = path,
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
    path: Path = defaultHouseholdPath,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    filter: HouseholdColumns.(Row) -> Boolean = {
        true
    },
) {
    this.prepareHouseholds(path = path, errorHandling = errorHandling, filter = filter)
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
