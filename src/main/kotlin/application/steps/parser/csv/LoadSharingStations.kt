@file:Suppress("TooManyFunctions")

package application.steps.parser.csv

import application.steps.HasImpedance
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.SharingModesConfig
import application.steps.SharingSourceFilesConfig
import application.steps.SourceFilesConfig
import core.modelsteps.Config
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
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.Zone
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.parser.GetZone
import domain.synthesis.parser.GlobalSharingProviderIdCounter
import domain.synthesis.parser.GlobalSharingStationIdCounter
import domain.synthesis.parser.SharingProviderByStationCsvColumns
import domain.synthesis.parser.SharingProviderByStationCsvConfig
import domain.synthesis.parser.allDay
import domain.synthesis.parser.createSharingProviderStationParser
import domain.synthesis.parser.onlySameZoneByFoot
import edu.kit.ifv.units.Distance
import utils.csv.CsvParser
import utils.csv.Row
import java.nio.file.Path

/**
 * Provides a scope for configuring sharing provider repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone] and [HasSharingProviderRepo]
 *             for [MutableSharingProvider].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.sharingProviders(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutableSharingProvider, SharingProviderId>) C.() -> Unit,
) where C : HasZoneRepo<*, Zone<*>>, C : HasSharingProviderRepo<MutableSharingProvider, *> =
    mutableRepositoryScope<C, MutableSharingProvider, SharingProviderId>(
        getter = { mutableSharingProviderRepository },
        sealed = sealed,
        scope,
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
    dependentRepositories: Set<Repository<*, *>> = setOf(zoneRepository),
) where C : HasZoneRepo<*, Zone<*>> = addResourceStep<C, MutableSharingProvider, SharingProviderId>(
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
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null, // TODO binarySharingProviderFormat()
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SourceFilesConfig =
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
fun <C, CFG> C.sharingProviderStationParser(
    sharingMode: Mode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {},
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : Config =
    createSharingProviderStationParser(
        SharingProviderByStationCsvConfig(
            columns = SharingProviderByStationCsvColumns(),
            sharingMode = sharingMode,
            getZone = ::getZone,
            zonesByFoot = onlySameZoneByFoot(),
            locationParser = { _, z -> z.centroidLocation },
            operatingHours = allDay,
            providerIdSource = GlobalSharingProviderIdCounter,
            stationIdSource = GlobalSharingStationIdCounter,
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        },
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
): (Row, Mode, StandardLocation, GetZone) -> List<Zone<*>>
    where C : HasZoneRepo<*, Zone<*>>, C : HasImpedance =
    { _, mode, stationLocation, getZone ->
        val zone = getZone(stationLocation.zoneId)
        zoneRepository.elements.filter {
            impedance.distance(zone, it, mode) <= threshold
        }.toList()
    }

// convenience function for different sharing systems

/**
 * Convenience function to create a CSV resource for bike-sharing providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the bike-sharing stations CSV file. Defaults to [config.bikeSharingStations].
 * @param parser The CSV parser. Defaults to [bikeSharingProviderStationParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the bike-sharing provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.bikeSharingProviderCsv(
    path: Path = config.bikeSharingStations,
    parser: CsvParser<MutableSharingProvider> = bikeSharingProviderStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingSourceFilesConfig, CFG : SourceFilesConfig, CFG : SharingModesConfig =
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
fun <C, CFG> C.bikeSharingProviderStationParser(
    sharingMode: Mode = config.bikeSharingMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {},
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingModesConfig =
    sharingProviderStationParser<C, CFG>(sharingMode, customizeCsvConfig)

/**
 * Convenience function to create a CSV resource for car-sharing station-based providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the car-sharing stations CSV file. Defaults to [config.carSharingStations].
 * @param parser The CSV parser. Defaults to [carSharingProviderStationParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the car-sharing provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.carSharingStationProviderCsv(
    path: Path = config.carSharingStations,
    parser: CsvParser<MutableSharingProvider> = carSharingProviderStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingSourceFilesConfig, CFG : SourceFilesConfig, CFG : SharingModesConfig =
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
fun <C, CFG> C.carSharingProviderStationParser(
    sharingMode: Mode = config.carSharingStationMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {},
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingModesConfig =
    sharingProviderStationParser<C, CFG>(sharingMode, customizeCsvConfig)

/**
 * Convenience function to create a CSV resource for car-sharing floating providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SharingSourceFilesConfig], [SourceFilesConfig],
 *            and [SharingModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the car-sharing floating area CSV file. Defaults to [config.carSharingFloatingArea].
 * @param parser The CSV parser. Defaults to [carSharingProviderFloatAreaParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the car-sharing floating provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.carSharingFloatingProviderCsv(
    path: Path = config.carSharingFloatingArea,
    parser: CsvParser<MutableSharingProvider> = carSharingProviderStationParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableSharingProvider>? = null,
): Resource<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingSourceFilesConfig, CFG : SourceFilesConfig, CFG : SharingModesConfig =
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
fun <C, CFG> C.carSharingProviderFloatAreaParser(
    sharingMode: Mode = config.carSharingFloatingMode,
    customizeCsvConfig: SharingProviderByStationCsvConfig.() -> Unit = {},
): CsvParser<MutableSharingProvider> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : SharingModesConfig =
    sharingProviderStationParser<C, CFG>(sharingMode, customizeCsvConfig)
