package application.steps.parser.csv

import application.steps.CarCodesConfig
import application.steps.HasCarRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.SourceFilesConfig
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.synthesis.data.car.MutablePrivateCar
import domain.synthesis.data.car.CarId
import domain.synthesis.data.car.engine.CarEngineStatistics
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.person.MutablePerson
import domain.synthesis.data.person.Person
import domain.synthesis.data.person.PersonId
import domain.synthesis.parser.CarColumns
import domain.synthesis.parser.PrivateCarCsvConfig
import domain.synthesis.parser.binary.BinaryCarReader
import domain.synthesis.parser.binary.BinaryCarWriter
import domain.synthesis.parser.createPrivateCarCsvParser
import utils.csv.CsvParser
import utils.csv.long
import java.nio.file.Path

/**
 * Provides a scope for configuring private car repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasCarRepo] for [MutablePrivateCar].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.cars(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutablePrivateCar, CarId>) C.() -> Unit,
) where C : HasCarRepo<MutablePrivateCar, *> = mutableRepositoryScope<C, MutablePrivateCar, CarId>(
    getter = { mutableCarRepository },
    sealed = sealed,
    scope,
)

/**
 * Loads private cars from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [MutablePerson] and [HasHouseholdRepo]
 *          for [MutableHousehold].
 * @param repository The mutable repository of private cars to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load cars from.
 */
context(repository: MutableRepository<MutablePrivateCar, CarId>)
fun <C> C.loadCars(
    resource: Resource<MutablePrivateCar>,
) where C : HasPersonRepo<MutablePerson, *>, C : HasHouseholdRepo<MutableHousehold, *> =
    addResourceStep<C, MutablePrivateCar, CarId>(
        name = "load cars from ${resource.name}",
        resource = resource,
    )

/**
 * Creates a CSV resource for private cars.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [Person] and [HasHouseholdRepo]
 *          for [MutableHousehold].
 * @param CFG The configuration type. Must implement [SourceFilesConfig] and [CarCodesConfig].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for private cars. Defaults to [privateCarCsvParser].
 * @param path The path to the private cars CSV file. Defaults to [config.sourceFiles.privateCarsCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryPrivateCarFormat].
 * @return A [Resource] representing the private car CSV.
 */
context(config: CFG)
fun <C, CFG> C.carCsv(
    parser: CsvParser<MutablePrivateCar> = privateCarCsvParser(),
    path: Path = config.sourceFiles.privateCarsCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePrivateCar>? = binaryPrivateCarFormat(),
): Resource<MutablePrivateCar>
    where C : HasPersonRepo<*, Person>,
          C : HasHouseholdRepo<MutableHousehold, *>,
          CFG : SourceFilesConfig,
          CFG : CarCodesConfig =
    CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    } // TODO add csv validation

/**
 * Creates a CSV parser for private cars.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [Person] and [HasHouseholdRepo]
 *          for [MutableHousehold].
 * @param CFG The configuration type. Must implement [CarCodesConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [PrivateCarCsvConfig].
 * @return A [CsvParser] for [MutablePrivateCar].
 */
context(config: CFG)
fun <C, CFG> C.privateCarCsvParser(
    customizeCsvConfig: PrivateCarCsvConfig.() -> Unit = {},
): CsvParser<MutablePrivateCar>
    where C : HasPersonRepo<*, Person>, C : HasHouseholdRepo<MutableHousehold, *>, CFG : CarCodesConfig =
    createPrivateCarCsvParser(
        PrivateCarCsvConfig(
            columns = CarColumns(),
            householdExists = mutableHouseholdRepository::contains,
            getOwnerHousehold = { row, col -> mutableHouseholdRepository.getValue(HouseholdId(row.long(col))) },
            getMainUser = { row, col -> personRepository.getValue(PersonId(row.long(col))) },
            carEngineStatistics = CarEngineStatistics(),
            carSegmentCodes = config.carSegmentCodes,
            errorHandling = config.errorHandling,
        ).also {
            it.customizeCsvConfig()
        },
    )

/**
 * Creates a binary cache configuration for private cars.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [Person] and [HasHouseholdRepo]
 *          for [MutableHousehold].
 * @param CFG The configuration type. Must implement [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C, CFG> C.binaryPrivateCarFormat(): BinaryCacheConfig<MutablePrivateCar>
    where C : HasPersonRepo<*, Person>, C : HasHouseholdRepo<MutableHousehold, *>,
          CFG : SourceFilesConfig =
    BinaryCacheConfig<MutablePrivateCar>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryCarReader(
            householdConverter = this.mutableHouseholdRepository::get,
            personConverter = this.personRepository::get,
            carEngineStatistics = CarEngineStatistics(),
        ),

        binaryWriter = BinaryCarWriter(),
    )
