package application.steps.parser.csv

import application.steps.HasHouseholdRepo
import application.steps.HasZoneRepo
import application.steps.HouseholdCodesConfig
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
import domain.shared.location.Zone
import domain.shared.location.parseRoadPositionWGS
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.parser.HouseholdColumns
import domain.synthesis.parser.HouseholdCsvConfig
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import domain.synthesis.parser.createHouseholdCsvParser
import edu.kit.ifv.units.UnitIntervalValue
import utils.csv.CsvParser
import java.nio.file.Path
import kotlin.math.roundToInt

/**
 * Provides a scope for configuring household repositories.
 *
 * @receiver The simulation context [CTXT].
 * @param CTXT The context type. Must implement [HasHouseholdRepo].
 * @param CFG The configuration type.
 * @param config The configuration. Provided via context.
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
context(config: CFG)
fun <CTXT, CFG> CTXT.households(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutableHousehold, HouseholdId>, CFG) CTXT.() -> Unit
) where CTXT : HasHouseholdRepo<MutableHousehold, Household> = mutableRepositoryScope(
    getter = { mutableHouseholdRepository },
    sealed = sealed,
    scope = scope
)

/**
 * Loads households from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of households to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load households from.
 */
context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C : Context> C.loadHouseholds(
    resource: Resource<MutableHousehold>,
) = addResourceStep<C, MutableHousehold, HouseholdId>(
    name = "load households from ${resource.name}",
    resource = resource,
)

/**
 * Creates a CSV resource for households.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [HouseholdCodesConfig].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for households. Defaults to [householdCsvParser].
 * @param path The path to the household CSV file. Defaults to [config.sourceFiles.householdCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryHouseholdFormat].
 * @return A [Resource] representing the household CSV.
 */
context(config: CFG)
fun <C, CFG> C.householdCsv(
    parser: CsvParser<MutableHousehold> = householdCsvParser(),
    path: Path = config.sourceFiles.householdCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableHousehold>? = binaryHouseholdFormat()
): Resource<MutableHousehold>
    where C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig, CFG : UnitConfig, CFG : HouseholdCodesConfig = CsvResource(
    path,
    parser,
    delimiter
).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

/**
 * Creates a binary cache configuration for households.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C, CFG> C.binaryHouseholdFormat(): BinaryCacheConfig<MutableHousehold>
    where C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig {
    return BinaryCacheConfig<MutableHousehold>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryHouseholdReader(
            zoneConverter = ::getZone,
            contextSimulationSeed = config.seed
        ),
        binaryWriter = BinaryHouseholdWriter()
    )
}

/**
 * Creates a CSV parser for households.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [HouseholdCodesConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [HouseholdCsvConfig].
 * @return A [CsvParser] for [MutableHousehold].
 */
context(config: CFG)
fun <C, CFG> C.householdCsvParser(
    customizeCsvConfig: HouseholdCsvConfig.() -> Unit = {},
): CsvParser<MutableHousehold>
    where C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig, CFG : UnitConfig, CFG : HouseholdCodesConfig =
    createHouseholdCsvParser(
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

/**
 * Filters households by a list of valid IDs.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of households to filter. Provided via context.
 * @param valid The collection of household IDs to keep.
 */
context(repository: MutableRepository<MutableHousehold, HouseholdId>)
fun <C : Context> C.filterHouseholds(valid: Collection<HouseholdId>) =
    filterIdsStep("filter households by list of valid ids") {
        it in valid
    }

/**
 * Filters a fraction of the population from the household repository.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of households to filter. Provided via context.
 * @param config The simulation configuration. Provided via context. Must implement [SimulationConfig].
 * @param fraction The fraction of households to keep. Defaults to [config.fractionOfPopulation].
 */
context(repository: MutableRepository<MutableHousehold, HouseholdId>, config: SimulationConfig)
fun <C : Context> C.filterFractionOfPopulation(
    fraction: UnitIntervalValue = config.fractionOfPopulation
) {
    var counter = 0
    val acceptedIncrement = (1 / fraction.toDouble()).roundToInt()
    filterStep("filter ${(fraction.toDouble() * 100).toInt()}% of households") {
        (counter % acceptedIncrement == 0).also { counter++ }
    }
}
