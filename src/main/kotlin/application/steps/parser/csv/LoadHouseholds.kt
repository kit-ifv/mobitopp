package application.steps.parser.csv

import core.modelsteps.AddResourceStep
import core.modelsteps.FileBasedAddResourceStep
import core.modelsteps.FilterIdsStep
import core.modelsteps.GroupedStepBuilder
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
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import units.CurrencyUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.currency
import utils.csv.decode
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

/**
 * Build a DSL function call that wraps the operations on the household context in curly brackets, by operating on
 * a [HouseholdStepBuilder] object. collects all the steps created and finalizes the repository at the end of the
 * block.
 */
fun LoadHouseholdContext.households(lambda: HouseholdStepBuilder.() -> Unit) {
    val builder = HouseholdStepBuilder(simulationSeed, zoneRepository::getValue)
    builder.apply(lambda)
    builder.executeOn(this)
    finishHouseholds()
}

class HouseholdStepBuilder(val seed: Long, val converter: (ZoneId) -> Zone) :
    GroupedStepBuilder<MutableHousehold, HouseholdId>() {
    override val reader: BinaryReader<MutableHousehold> = BinaryHouseholdReader(converter, seed)
    override val writer: BinaryWriter<MutableHousehold> = BinaryHouseholdWriter()
    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutableHousehold, HouseholdId>,
    ): FileBasedAddResourceStep<MutableHousehold, HouseholdId> {
        return context(source) {
            FileBasedAddResourceStep(source, lambda(source))
        }
    }
}

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

fun LoadHouseholdContext.prepareHouseholds(
    householdCsvConfig: HouseholdCsvConfig,
) {
    val (_, step) = householdsFromCsvStep(householdCsvConfig)
    this@prepareHouseholds.runStep(step)
}

fun LoadHouseholdContext.spawnCsvParser(
    householdCsvConfig: HouseholdCsvConfig,
) = householdCsvConfig.run {
    val parser = CsvParser(errorHandling) { row ->

        MutableHousehold(
            id = HouseholdId(row.long(columns.hhIdColumn)),
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
    parser.withFilter(filterWrap)
}

fun LoadHouseholdContext.householdsFromCsvStep(
    path: Path = defaultHouseholdPath,
    lambda: HouseholdCsvConfig.() -> Unit = {},
): FileBasedAddResourceStep<MutableHousehold, HouseholdId> {
    val config = HouseholdCsvConfig(path = path, incomeUnit = costUnit)
    config.apply(lambda)
    return householdsFromCsvStep(config)
}

fun LoadHouseholdContext.householdsFromCsvStep(
    config: HouseholdCsvConfig,
): FileBasedAddResourceStep<MutableHousehold, HouseholdId> {
    return config.run {
        val parser = this@householdsFromCsvStep.spawnCsvParser(config)

        val step = LoadCsvStep(
            path = path,
            name = "Load households from csv",
            parser = parser,
            delimiter = delimiter,
            repository = householdRepository,
            dependentRepositories = setOf(zoneRepository),
            validationMock = listOf() // TODO
        )
        FileBasedAddResourceStep(config.path, step)
    }
}

fun LoadHouseholdContext.runStep(
    step: AddResourceStep<MutableHousehold, HouseholdId>,

) = runStep {
    step
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
