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
import domain.shared.location.MutableZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.parseRoadPositionWGS
import domain.synthesis.parser.ZoneColumns
import domain.synthesis.parser.ZoneCsvConfig
import domain.synthesis.parser.binary.BinaryZoneReader
import domain.synthesis.parser.binary.BinaryZoneWriter
import domain.synthesis.parser.createZoneCsvParser
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
    scope: context(MutableRepository<MutableZone, ZoneId>) C.() -> Unit
) where C : HasZoneRepo<MutableZone, Zone> = mutableRepositoryScope<C,  MutableZone, ZoneId>(
    getter = { mutableZoneRepository },
    sealed = sealed,
    scope = scope
)

/**
 * Loads zones from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of zones to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load zones from.
 */
context(repository: MutableRepository<MutableZone, ZoneId>)
fun <C : Context> C.loadZones(
    resource: Resource<MutableZone>,
) = addResourceStep<C, MutableZone, ZoneId>(
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
    parser: CsvParser<MutableZone> = zoneCsvParser(),
    path: Path = config.sourceFiles.zonesCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableZone>? = binaryZoneFormat()
): Resource<MutableZone>
    where CFG : SourceFilesConfig, CFG : UnitConfig, CFG : RegionCodesConfig =
    CsvResource(path, parser, delimiter).let { csv ->
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
fun <C : Context, CFG> C.binaryZoneFormat(): BinaryCacheConfig<MutableZone>
    where CFG : SourceFilesConfig, CFG : RegionCodesConfig {
    return BinaryCacheConfig<MutableZone>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryZoneReader(
            seed = config.seed,
            regionCode = config.regionTypeCodes
        ),
        binaryWriter = BinaryZoneWriter()
    )
}

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
    customizeCsvConfig: ZoneCsvConfig.() -> Unit = {}
): CsvParser<MutableZone>
    where CFG : SourceFilesConfig, CFG : UnitConfig, CFG : RegionCodesConfig =
    createZoneCsvParser(
        ZoneCsvConfig(
            columns = ZoneColumns(),
            centroidParser = String::parseRoadPositionWGS,
            reliefUnit = config.distanceUnit,
            regionTypeCodes = config.regionTypeCodes,
            errorHandling = config.errorHandling,
            seed = config.seed
        ).also {
            it.customizeCsvConfig()
        }
    )

// fun cheatyDefaultCsvParser(
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    seed: Long = 1L
// ): DefaultCsvParser<MutableZone> {
//    val csvParser = CsvParser(errorHandling) { row ->
//        MutableZone(
//            id = ZoneId(row.long("id")),
//            centroid = Location.BIELEFELD,
//            seed = seed
//        ) {
//            visumId = row.long("id")
//            name = row("zone_name")
//            regionType = RegioStaR17.REGIOPOLE
//            classification = ZoneClassification.STUDY_AREA
//            parkingPlaces = 0
//            isDestination = true
//            relief = 0.meters
//        }
//    }
//
//    return csvParser
// }
