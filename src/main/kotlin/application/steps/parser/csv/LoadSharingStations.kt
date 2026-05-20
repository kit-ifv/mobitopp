package application.steps.parser.csv

import application.steps.HasImpedance
import core.modelsteps.Config
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.SharingModesConfig
import application.steps.SharingSourceFilesConfig
import application.steps.SourceFilesConfig
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.parser.GetZone
import domain.synthesis.parser.GlobalSharingProviderIdCounter
import domain.synthesis.parser.GlobalSharingStationIdCounter
import domain.synthesis.parser.SharingProviderByStationCsvColumns
import domain.synthesis.parser.SharingProviderByStationCsvConfig
import domain.synthesis.parser.allDay
import domain.synthesis.parser.createSharingProvidersByStationParser
import domain.synthesis.parser.onlySameZoneByFoot
import edu.kit.ifv.units.Distance
import utils.csv.CsvParser
import utils.csv.Row
import java.nio.file.Path

/**
 * Provides a scope for configuring sharing provider repositories.
 *
 * @receiver The simulation context [CTXT].
 * @param CTXT The context type. Must implement [HasZoneRepo] for [Zone] and [HasSharingProviderRepo]
 *             for [MutableSharingProvider].
 * @param CFG The configuration type. Must implement [Config].
 * @param config The configuration. Provided via context.
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
context(config: CFG)
fun <CTXT, CFG : Config> CTXT.sharingProviders(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutableSharingProvider, SharingProviderId>, CFG) CTXT.() -> Unit
) where CTXT : HasZoneRepo<*, Zone>, CTXT : HasSharingProviderRepo<MutableSharingProvider, *> =
    mutableRepositoryScope<CTXT, CFG, MutableSharingProvider, SharingProviderId>(
        getter = { mutableSharingProviderRepository },
        sealed = sealed,
        scope
    )

/**
 * Loads sharing providers from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param repository The mutable repository of sharing providers to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load sharing providers from.
 * @param dependentRepositories Repositories that this loading step depends on. Defaults to [zoneRepository].
 */
context(repository: MutableRepository<MutableSharingProvider, SharingProviderId>)
fun <C> C.loadSharingProviders(
    resource: Resource<MutableSharingProvider>,
    dependentRepositories: Set<Repository<*, *>> = setOf(zoneRepository)
) where C : HasZoneRepo<*, Zone> =
    addResourceStep<C, MutableSharingProvider, SharingProviderId>(
        name = "load sharing providers from ${resource.name}",
        resource = resource,
        dependentRepositories = dependentRepositories,
    )

/**
 * Creates a CSV resource for sharing providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the sharing provider CSV file.
 * @param parser The CSV parser for sharing providers.
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the sharing provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.sharingProviderCsv(
    path: Path,
    parser: CsvParser<MutableSharingProvider>,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null, // TODO: binarySharingProviderFormat()
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig =
    CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

/**
 * Creates a CSV parser for sharing providers defined by stations.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [Config].
 * @param config The configuration. Provided via context.
 * @param sharingMode The mode for the sharing service.
 * @param customizeCsvConfig Lambda to customize the [SharingProviderByStationCsvConfig].
 * @return A [CsvParser] for [MutableSharingProvider].
 */
context(config: CFG)
fun <C, CFG> C.sharingProvidersByStationParser(
    sharingMode: Mode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {}
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : Config =
    createSharingProvidersByStationParser(
        SharingProviderByStationCsvConfig(
            columns = SharingProviderByStationCsvColumns(),
            sharingMode = sharingMode,
            getZone = ::getZone,
            zonesByFoot = onlySameZoneByFoot(),
            locationParser = { _, z -> z.centroid },
            operatingHours = allDay,
            providerIdSource = GlobalSharingProviderIdCounter,
            stationIdSource = GlobalSharingStationIdCounter,
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        }
    )

/**
 * A zone filtering strategy that selects zones within a certain radius by foot.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone] and [HasImpedance].
 * @param threshold The maximum distance.
 * @return A function that filters zones based on the threshold distance from a station location.
 */
fun <C> C.zonesByFootInRadius(
    threshold: Distance,
): (Row, Mode, StandardLocation, GetZone) -> List<Zone>
where C : HasZoneRepo<*, Zone>, C : HasImpedance = { _, mode, stationLocation, getZone ->
    val zone = getZone(stationLocation.zoneID)
    zoneRepository.elements.filter {
        impedance.distance(zone.centroid, it.centroid, mode) <= threshold
    }.toList()
}


//convenience function for different sharing systems

/**
 * Convenience function to create a CSV resource for bike-sharing providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the bike-sharing stations CSV file. Defaults to [config.bikeSharingStations].
 * @param parser The CSV parser. Defaults to [bikeSharingProvidersByStationParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the bike-sharing provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.bikeSharingProviderCsv(
    path: Path = config.bikeSharingStations,
    parser: CsvParser<MutableSharingProvider> = bikeSharingProvidersByStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingSourceFilesConfig, CFG: SourceFilesConfig, CFG : SharingModesConfig =
    sharingProviderCsv(path, parser, delimiter, binaryCache)

/**
 * Convenience function to create a CSV parser for bike-sharing providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param sharingMode The mode for the bike-sharing service. Defaults to [config.bikeSharingMode].
 * @param customizeCsvConfig Lambda to customize the [SharingProviderByStationCsvConfig].
 * @return A [CsvParser] for [MutableSharingProvider].
 */
context(config: CFG)
fun <C, CFG> C.bikeSharingProvidersByStationParser(
    sharingMode: Mode = config.bikeSharingMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {}
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingModesConfig =
    sharingProvidersByStationParser<C, CFG>(sharingMode, customizeCsvConfig)

/**
 * Convenience function to create a CSV resource for car-sharing station-based providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the car-sharing stations CSV file. Defaults to [config.carSharingStations].
 * @param parser The CSV parser. Defaults to [carSharingStationProvidersByStationParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the car-sharing provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.carSharingStationProviderCsv(
    path: Path = config.carSharingStations,
    parser: CsvParser<MutableSharingProvider> = carSharingStationProvidersByStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingSourceFilesConfig, CFG: SourceFilesConfig, CFG : SharingModesConfig =
    sharingProviderCsv(path, parser, delimiter, binaryCache)

/**
 * Convenience function to create a CSV parser for car-sharing station-based providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param sharingMode The mode for the car-sharing service. Defaults to [config.carSharingStationMode].
 * @param customizeCsvConfig Lambda to customize the [SharingProviderByStationCsvConfig].
 * @return A [CsvParser] for [MutableSharingProvider].
 */
context(config: CFG)
fun <C, CFG> C.carSharingStationProvidersByStationParser(
    sharingMode: Mode = config.carSharingStationMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {}
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingModesConfig =
    sharingProvidersByStationParser<C, CFG>(sharingMode, customizeCsvConfig)

/**
 * Convenience function to create a CSV resource for car-sharing floating providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the car-sharing floating area CSV file. Defaults to [config.carSharingFloatingArea].
 * @param parser The CSV parser. Defaults to [carSharingFloatingProvidersByStationParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the car-sharing floating provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.carSharingFloatingProviderCsv(
    path: Path = config.carSharingFloatingArea,
    parser: CsvParser<MutableSharingProvider> = carSharingStationProvidersByStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingSourceFilesConfig, CFG: SourceFilesConfig, CFG : SharingModesConfig =
    sharingProviderCsv(path, parser, delimiter, binaryCache)

/**
 * Convenience function to create a CSV parser for car-sharing floating providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param sharingMode The mode for the car-sharing service. Defaults to [config.carSharingFloatingMode].
 * @param customizeCsvConfig Lambda to customize the [SharingProviderByStationCsvConfig].
 * @return A [CsvParser] for [MutableSharingProvider].
 */
context(config: CFG)
fun <C, CFG> C.carSharingFloatingProvidersByStationParser(
    sharingMode: Mode = config.carSharingFloatingMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {}
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone>, CFG : SharingModesConfig =
    sharingProvidersByStationParser<C, CFG>(sharingMode, customizeCsvConfig)


//
//
//interface LoadSharingProvidersContext : DemandSimContext {
//    val sharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId>
//    val zoneRepository: Repository<Zone, ZoneId>
//    val zoneColumnIndex: Map<Int, LegacyZone>
//
//    val defaultSharingStationPath: Path
//        get() = zoneFolder.resolve("sharing-stations.csv")
//}
//
//data class StationColumns(
//    val uidColumn: String = "uid",
//    val nameColumn: String = "name",
//    val coordinatesColumn: String = "coordinates",
//    val vehicleCountColumn: String = "vehicles",
//    val zoneColumn: String = "zone",
//    val zonesByFootColumn: String = "zone_avail",
//)
//
//private var providerIdCounter = 0L
//private var sharingIdCounter: Long = 0L
//
//@Suppress("LongParameterList", "UnusedParameter")
//fun LoadSharingProvidersContext.prepareSharingStations(
//    path: Path = defaultSharingStationPath,
//    columns: StationColumns = StationColumns(),
//    delimiter: String = SEMICOLON,
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    providerName: String,
//    mode: Mode,
//    coordinateParser: (String) -> Point = PointCreator::createUTM,
//) {
//    val sharingProvider: MutableSharingProvider = sharingProviderRepository.elements.find {
//        it.name == providerName
//    }?.also {
//        require(it.mode == mode) {
//            "Cannot add another sharing provider with same name: $providerName but different mode: ${it.mode}!=$mode"
//        }
//    } ?: MutableSharingProvider(
//        id = SharingProviderId(providerIdCounter++)
//    ) {
//        name = providerName
//        this.mode = mode
//    }
//
//    val csvParser = CsvParser<MutableSharingStation>(errorHandling) { row ->
//
//        MutableSharingStation(
//            id = SharingStationId(sharingIdCounter++),
//            owner = sharingProvider,
//        ) {
//            uid = row(columns.uidColumn)
//            name = row(columns.nameColumn)
//            zonesByFoot.addAll(
//                prepareZonesByFoot(row, columns.zonesByFootColumn).toMutableSet()
//            )
//            location = StandardLocation(
//                zone = getZone(row.long(columns.zoneColumn)),
//                position = coordinateParser(row(columns.coordinatesColumn)),
//                roadAccess = RoadAccess.INVALID
//            )
//            initialVehicleCount = row.int(columns.vehicleCountColumn)
//        }
//    }
//
//    this.prepareStationsFile(sharingProvider, csvParser, path, delimiter) // TODO
//}
//
//fun LoadSharingProvidersContext.prepareStationsFile(
//    sharingProvider: MutableSharingProvider,
//    parser: CsvParser<MutableSharingStation>,
//    path: Path = defaultSharingStationPath,
//    delimiter: String = SEMICOLON,
//) = runStep {
//    object : AbstractAddResourceStep<MutableSharingProvider, SharingProviderId>() {
//        override val name = "Add sharing provider ${sharingProvider.name} and parse stations from csv: ${path.fileName}"
//
//        private val csvResource = CsvResource(path, parser, delimiter)
//
//        override val resource = LazyResource<MutableSharingProvider>(
//            csvResource.name,
//            csvResource.source,
//        ) {
//            csvResource.elements.toList()
//
//            if (sharingProviderRepository.elements.none { it.name == sharingProvider.name }) {
//                listOf(sharingProvider).asSequence()
//            } else {
//                emptySequence()
//            }
//        }
//
//        override val repository = sharingProviderRepository
//        override val dependentRepositories = setOf(zoneRepository)
//
//        override fun verifyInput() = ValidateCsvMetadata(this, csvResource).validate()
//        override fun mockElementsForValidation(): List<MutableSharingProvider> = emptyList() // TODO
//    }
//}
//
//fun LoadSharingProvidersContext.finishSharingStations() = runStep {
//    SealStep(sharingProviderRepository)
//}
//
//fun LoadSharingProvidersContext.loadSharingStations(
//    providerName: String,
//    mode: Mode,
//) {
//    this.prepareSharingStations(providerName = providerName, mode = mode)
//    this.finishSharingStations()
//}
//
//fun String.parseCoordinate(): KCoordinate =
//    this.split(",")
//        .takeIf { it.size == 2 }
//        ?.let { it[0].toDouble() to it[1].toDouble() }
//        ?.toCoordinate()
//        ?: error(
//            "Malformed Coordinate: could not parse coordinate string '$this'." +
//                "Expected format: '<NUMBER>,<NUMBER>'!"
//        )

// fun MutableSharingProvider.prepareVehicles(count: Int, mode: Mode = this.mode): Set<SharingVehicle> {
//    return (numberOfVehicles until numberOfVehicles + count).map {
//        SharingVehicle(
//            id = SharingVehicleId(it.toLong()),
//            mode = mode,
//            owner = this,
//        )
//    }.toSet()
// }
//
//fun <C> C.prepareZonesByFoot(row: Row, column: String): Set<Zone> where C : LoadSharingProvidersContext {
//    return row(column).split(",").map { id ->
//
//        id.toLongOrNull()?.let {
//            getZone(it)
//        } ?: error(
//            "Could not parse ZoneId $id (expected value of type Long) " +
//                "in column $column row${row.index} of ${row.source}: ${row(column)}!"
//        )
//    }.toSet()
//}
//
//fun <C> C.getZone(id: Long): Zone where C : LoadSharingProvidersContext = requireNotNull(
//    this.zoneRepository[ZoneId(id)] ?: zoneColumnIndex[id.toInt()]
//) {
//    "Referenced ZoneId $id could not be found in zoneRepo:" +
//        " ${zoneRepository.elements.map { it.id }.toList()}"
//}
