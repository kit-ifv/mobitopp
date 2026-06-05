package application.steps.parser.csv

import application.steps.DrtModesConfig
import application.steps.DrtSourceFilesConfig
import application.steps.HasDrtProviderRepo
import application.steps.HasZoneRepo
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
import domain.shared.location.zone.Zone
import domain.shared.location.zone.attributes.HasRegionType
import domain.simulation.data.drt.MutableDrtProviderData
import domain.simulation.data.drt.DrtProviderId
import domain.simulation.parser.DrtProviderByAreaCsvColumns
import domain.simulation.parser.DrtProviderByAreaCsvConfig
import domain.simulation.parser.GlobalDrtProviderIdCounter
import domain.simulation.parser.allDay
import domain.simulation.parser.createDrtProvidersByAreaParser
import utils.csv.CsvParser
import java.nio.file.Path

/**
 * Provides a scope for configuring DRT provider repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone] and [HasDrtProviderRepo]
 *             for [MutableDrtProviderData].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.drtProviders(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutableDrtProviderData, DrtProviderId>) C.() -> Unit,
) where C : HasZoneRepo<*, Zone<*>>, C : HasDrtProviderRepo<MutableDrtProviderData, *> =
    mutableRepositoryScope<C, MutableDrtProviderData, DrtProviderId>(
        getter = { mutableDrtProviderRepository },
        sealed = sealed,
        scope,
    )

/**
 * Loads DRT providers from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param repository The mutable repository of DRT providers to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load DRT providers from.
 * @param dependentRepositories Repositories that this loading step depends on. Defaults to [zoneRepository].
 */
context(repository: MutableRepository<MutableDrtProviderData, DrtProviderId>)
fun <C> C.loadDrtProviders(
    resource: Resource<MutableDrtProviderData>,
    dependentRepositories: Set<Repository<*, *>> = setOf(zoneRepository),
) where C : HasZoneRepo<*, Zone<*>> = addResourceStep<C, MutableDrtProviderData, DrtProviderId>(
    name = "load drt providers from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

/**
 * Creates a CSV resource for DRT providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the DRT provider CSV file.
 * @param parser The CSV parser for DRT providers.
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the DRT provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.drtProviderCsv(
    path: Path,
    parser: CsvParser<MutableDrtProviderData>,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableDrtProviderData>? = null, // TODO binaryDrtProviderFormat()?
): Resource<MutableDrtProviderData> where C : HasZoneRepo<*, Zone<*>>, CFG : SourceFilesConfig =
    CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

/**
 * Creates a CSV parser for DRT providers defined by service areas.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [Config].
 * @param config The configuration. Provided via context.
 * @param drtMode The mode for the DRT service.
 * @param customizeCsvConfig Lambda to customize the [DrtProviderByAreaCsvConfig].
 * @return A [CsvParser] for [MutableDrtProviderData].
 */
context(config: CFG)
fun <C, CFG> C.drtProviderServiceAreaParser(
    drtMode: Mode,
    customizeCsvConfig: DrtProviderByAreaCsvConfig.() -> Unit = {},
): CsvParser<MutableDrtProviderData> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : Config =
    createDrtProvidersByAreaParser(
        DrtProviderByAreaCsvConfig(
            columns = DrtProviderByAreaCsvColumns(),
            drtMode = drtMode,
            getZone = ::getZone,
            operatingHours = allDay,
            providerIdSource = GlobalDrtProviderIdCounter,
            errorHandling = config.errorHandling,
            seed = config.seed,
        ).also {
            it.customizeCsvConfig()
        },
    )

/**
 * Convenience function to create a CSV resource for ride-pooling providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [DrtSourceFilesConfig], [SourceFilesConfig],
 *            and [DrtModesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the ride-pooling CSV file. Defaults to [config.ridePoolingServiceAreas].
 * @param parser The CSV parser. Defaults to [poolingProviderServiceAreaParser].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the ride-pooling provider CSV.
 */
context(config: CFG)
fun <C, CFG> C.ridePoolingProviderCsv(
    path: Path = config.ridePoolingServiceAreas,
    parser: CsvParser<MutableDrtProviderData> = poolingProviderServiceAreaParser(),
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutableDrtProviderData>? = null,
): Resource<MutableDrtProviderData>
where C : HasZoneRepo<*, Zone<HasRegionType>>,
      CFG : DrtSourceFilesConfig,
      CFG : SourceFilesConfig,
      CFG : DrtModesConfig =
    drtProviderCsv(path, parser, delimiter, binaryCache)

/**
 * Convenience function to create a CSV parser for ride-pooling providers.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [DrtModesConfig].
 * @param config The configuration. Provided via context.
 * @param sharingMode The mode for the ride-pooling service. Defaults to [config.ridePoolingMode].
 * @param customizeCsvConfig Lambda to customize the [DrtProviderByAreaCsvConfig].
 * @return A [CsvParser] for [MutableDrtProviderData].
 */
context(config: CFG)
fun <C, CFG> C.poolingProviderServiceAreaParser(
    sharingMode: Mode = config.ridePoolingMode,
    customizeCsvConfig: DrtProviderByAreaCsvConfig.() -> Unit = {},
): CsvParser<MutableDrtProviderData> where C : HasZoneRepo<*, Zone<HasRegionType>>, CFG : DrtModesConfig =
    drtProviderServiceAreaParser<C, CFG>(
        sharingMode,
        customizeCsvConfig,
    )
