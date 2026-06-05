package edu.kit.ifv.application.steps.parser.csv
import edu.kit.ifv.application.steps.HasHouseholdRepo
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.application.steps.HouseholdCodesConfig
import edu.kit.ifv.application.steps.SimulationConfig
import edu.kit.ifv.application.steps.SourceFilesConfig
import edu.kit.ifv.application.steps.UnitConfig
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.resources.BinaryCacheConfig
import edu.kit.ifv.core.modelsteps.resources.CsvResource
import edu.kit.ifv.core.modelsteps.resources.MutableRepository
import edu.kit.ifv.core.modelsteps.resources.Resource
import edu.kit.ifv.core.modelsteps.resources.cachedCsv
import edu.kit.ifv.core.modelsteps.scopes.addResourceStep
import edu.kit.ifv.core.modelsteps.scopes.filterIdsStep
import edu.kit.ifv.core.modelsteps.scopes.filterStep
import edu.kit.ifv.core.modelsteps.scopes.mutableRepositoryScope
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.location.PointAndRoadPositionParser
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.parser.HouseholdColumns
import edu.kit.ifv.domain.simulation.parser.HouseholdCsvConfig
import edu.kit.ifv.domain.simulation.parser.binary.BinaryHouseholdReader
import edu.kit.ifv.domain.simulation.parser.binary.BinaryHouseholdWriter
import edu.kit.ifv.domain.simulation.parser.createHouseholdCsvParser
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.utils.csv.CsvParser
import java.nio.file.Path
import kotlin.math.roundToInt

/**
 * Provides a scope for configuring household repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasHouseholdRepo].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.households(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutableHousehold, HouseholdId>) C.() -> Unit,
) where C : HasHouseholdRepo<MutableHousehold, Household> = mutableRepositoryScope(
    getter = { mutableHouseholdRepository },
    sealed = sealed,
    scope = scope,
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
fun <C : Context> C.loadHouseholds(resource: Resource<MutableHousehold>) =
    addResourceStep<C, MutableHousehold, HouseholdId>(
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
    binaryCache: BinaryCacheConfig<MutableHousehold>? = binaryHouseholdFormat(),
): Resource<MutableHousehold>
        where C : HasZoneRepo<*, Zone<HasRegionType>>,
              CFG : SourceFilesConfig,
              CFG : UnitConfig,
              CFG : HouseholdCodesConfig =
    CsvResource(
        path,
        parser,
        delimiter,
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
        where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SourceFilesConfig =
    BinaryCacheConfig<MutableHousehold>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryHouseholdReader(
            zoneConverter = ::getZone,
            contextSimulationSeed = config.seed,
        ),
        binaryWriter = BinaryHouseholdWriter(),
    )

/**
 * Creates a CSV parser for households.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [HouseholdCodesConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [domain.simulation.parser.HouseholdCsvConfig].
 * @return A [CsvParser] for [MutableHousehold].
 */
context(config: CFG)
fun <C, CFG> C.householdCsvParser(
    customizeCsvConfig: HouseholdCsvConfig.() -> Unit = {},
): CsvParser<MutableHousehold>
        where C : HasZoneRepo<*, Zone<HasRegionType>>,
              CFG : SourceFilesConfig,
              CFG : UnitConfig,
              CFG : HouseholdCodesConfig =
    createHouseholdCsvParser(
        HouseholdCsvConfig(
            columns = HouseholdColumns(),
            getZone = ::getZone,
            roadPositionParser = PointAndRoadPositionParser.parseWGS,
            incomeUnit = config.currencyUnit,
            economicStatusCodes = config.economicStatusCodes,
            filter = { true },
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        },
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
@Suppress("MagicNumber")
context(
    repository: MutableRepository<MutableHousehold, HouseholdId>,
    config: SimulationConfig
)
fun <C : Context> C.filterFractionOfPopulation(fraction: UnitIntervalValue = config.fractionOfPopulation) {
    var counter = 0
    val acceptedIncrement = (1 / fraction.toDouble()).roundToInt()
    filterStep("filter ${(fraction.toDouble() * 100).toInt()}% of households") {
        (counter % acceptedIncrement == 0).also { counter++ }
    }
}
