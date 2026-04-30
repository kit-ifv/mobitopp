package application.steps.parser.csv

import application.steps.HasHouseholdRepo
import application.steps.HasZoneRepo
import application.steps.HouseholdCodesConfig
import application.steps.RegionCodesConfig
import application.steps.SimulationConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.Context
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.filterIdsStep
import core.modelsteps.scopes.filterStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasRoadAccess
import domain.shared.location.parseRoadPositionWGS
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.UnitIntervalValue
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.currency
import utils.csv.decode
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter
import java.nio.file.Path
import kotlin.math.roundToInt

context(config: CFG)
fun <CTXT, CFG> CTXT.households(
    scope: context(MutableRepository<MutableHousehold, HouseholdId>, CFG) CTXT.() -> Unit
) where CTXT : HasHouseholdRepo<Household> = mutableRepositoryScope(
    "households", this::householdRepository.setter, scope
)

context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C: Context> C.loadHouseholds(
    resource: Resource<MutableHousehold>,
) = addResourceStep<C, MutableHousehold, HouseholdId>(
    name = "load households from ${resource.name}",
    resource = resource,
)

context(config: CFG)
fun <C, CFG> C.householdCsv(
    parser: CsvParser<MutableHousehold> = householdCsvParser(),
    path: Path = config.sourceFiles.householdCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableHousehold>? = binaryHouseholdFormat()
): Resource<MutableHousehold>
where C: HasZoneRepo<Zone>, CFG: SourceFilesConfig, CFG: UnitConfig, CFG: HouseholdCodesConfig
    = CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

context(config: CFG)
fun <C, CFG> C.binaryHouseholdFormat(): BinaryCacheConfig<MutableHousehold>
where C: HasZoneRepo<Zone>, CFG: SourceFilesConfig
{
    return BinaryCacheConfig<MutableHousehold>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryHouseholdReader(
            zoneConverter = ::getZone,
            contextSimulationSeed = config.seed
        ),
        binaryWriter = BinaryHouseholdWriter()
    )
}

context(config: CFG)
fun <C, CFG> C.householdCsvParser(
    customizeCsvConfig: HouseholdCsvConfig.() -> Unit = {},
): CsvParser<MutableHousehold>
where C: HasZoneRepo<Zone>, CFG: SourceFilesConfig, CFG: UnitConfig, CFG: HouseholdCodesConfig
    = householdCsvParser(
        HouseholdCsvConfig(
            columns = HouseholdColumns(),
            getZone = ::getZone,
            roadPositionParser = String::parseRoadPositionWGS,
            incomeUnit = config.currencyUnit,
            economicStatusCodes = config.economicStatusCodes,
            filter = { true },
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        }
    )


context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C: Context> C.filterHouseholds(valid: Collection<HouseholdId>) =
    filterIdsStep("filter households by list of valid ids") {
        it in valid
    }

context(repository: MutableRepository<MutableHousehold, HouseholdId>, config: SimulationConfig)
fun <C: Context> C.filterFractionOfPopulation(
    fraction: UnitIntervalValue = config.fractionOfPopulation
) {
    var counter = 0
    val acceptedIncrement = (1 / fraction.toDouble()).roundToInt()
    filterStep("filter ${(fraction.toDouble() * 100).toInt()}% of households") {
        (counter % acceptedIncrement == 0).also { counter++ }
    }
}


//household csv parsers
data class HouseholdCsvConfig(
    var columns: HouseholdColumns = HouseholdColumns(),
    var getZone: (ZoneId) -> Zone, //TODO maybe Row.() -> Zone instead to be more flexible
    var roadPositionParser: (String) -> HasRoadAccess = String::parseRoadPositionWGS,
    var incomeUnit: CurrencyUnit,
    var economicStatusCodes: CodePlan<EconomicStatus>,
    var filter: HouseholdColumns.(Row) -> Boolean = { true },
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val seed: Long,
)


fun householdCsvParser(
    householdCsvConfig: HouseholdCsvConfig,
) = householdCsvConfig.run {
    val parser = CsvParser(errorHandling) { row ->

        MutableHousehold(
            id = HouseholdId(row.long(columns.hhIdColumn)),
            seed,
        ) {
            householdNumber = row.long(columns.hhNumberColumn)
            surveyYear = row.int(columns.yearColumn)
            domCode = row.int(columns.domCodeColumn)
            type = row.int(columns.typeColumn)
            incomePerMonth = row.currency(columns.incomeColumn, incomeUnit)
            economicStatus = row.decode(columns.economicalStatusColumn, economicStatusCodes)

            // Robin: I converted this builder call to the location as found in [Household]
            val temp = row(columns.locationColumn, roadPositionParser)
            location = StandardLocation(
                position = temp.position,
                zone = getZone(ZoneId(row.long(columns.zoneColumn))),
                roadAccess = temp.roadAccess
            )
        }
    }
    val filterWrap: (Row) -> Boolean = { columns.filter(it) }
    parser.withFilter(filterWrap)
}
