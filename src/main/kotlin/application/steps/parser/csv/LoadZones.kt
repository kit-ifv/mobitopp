package application.steps.parser.csv

import application.steps.HasZoneRepo
import application.steps.RegionCodesConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.Context
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.shared.location.PointAndRoadPositionParser
import domain.shared.location.parser.BinaryZoneReader
import domain.shared.location.parser.BinaryZoneWriter
import domain.shared.location.parser.ZoneColumns
import domain.shared.location.parser.ZoneCsvConfig
import domain.shared.location.parser.createZoneCsvParser
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.ZoneId
import utils.csv.CsvParser
import java.nio.file.Path

/**
 * Provides a scope for configuring zone repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [MutableZone].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.zones(
    sealed: Boolean = false,
    scope: context(MutableRepository<MaximalZone, ZoneId>) C.() -> Unit,
) where C : HasZoneRepo<MaximalZone, MaximalZone> = mutableRepositoryScope<C, MaximalZone, ZoneId>(
    getter = { mutableZoneRepository },
    sealed = sealed,
    scope = scope,
)

/**
 * Loads zones from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of zones to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load zones from.
 */
context(repository: MutableRepository<MaximalZone, ZoneId>)
fun <C : Context> C.loadZones(resource: Resource<MaximalZone>) = addResourceStep<C, MaximalZone, ZoneId>(
    name = "load zones from ${resource.name}",
    resource = resource,
)

/**
 * Creates a CSV resource for zones.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [RegionCodesConfig].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for zones. Defaults to [zoneCsvParser].
 * @param path The path to the zone CSV file. Defaults to [config.sourceFiles.zonesCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryZoneFormat].
 * @return A [Resource] representing the zone CSV.
 */
context(config: CFG)
fun <C : Context, CFG> C.zoneCsv(
    parser: CsvParser<MaximalZone> = zoneCsvParser(),
    path: Path = config.sourceFiles.zonesCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MaximalZone>? = binaryZoneFormat(),
): Resource<MaximalZone>
    where CFG : SourceFilesConfig, CFG : UnitConfig, CFG : RegionCodesConfig = CsvResource(
    path,
    parser,
    delimiter,
).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

/**
 * Creates a binary cache configuration for zones.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param CFG The configuration type. Must implement [SourceFilesConfig] and [RegionCodesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C : Context, CFG> C.binaryZoneFormat(): BinaryCacheConfig<MaximalZone>
    where CFG : SourceFilesConfig, CFG : RegionCodesConfig =
    BinaryCacheConfig<MaximalZone>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryZoneReader(
            seed = config.seed,
            regionCode = config.regionTypeCodes,
        ),
        binaryWriter = BinaryZoneWriter(),
    )

/**
 * Creates a CSV parser for zones.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [RegionCodesConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [ZoneCsvConfig].
 * @return A [CsvParser] for [MutableZone].
 */
context(config: CFG)
fun <C : Context, CFG> C.zoneCsvParser(
    customizeCsvConfig: ZoneCsvConfig.() -> Unit = {},
): CsvParser<MaximalZone>
    where CFG : SourceFilesConfig, CFG : UnitConfig, CFG : RegionCodesConfig =
    createZoneCsvParser(
        ZoneCsvConfig(
            columns = ZoneColumns(),
            centroidParser = PointAndRoadPositionParser.parseWGS,
            reliefUnit = config.distanceUnit,
            regionTypeCodes = config.regionTypeCodes,
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        },
    )
