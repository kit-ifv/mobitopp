package application.steps.parser.csv

import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderRepo
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.person.Person
import domain.synthesis.data.person.PersonId
import domain.synthesis.data.person.Sex
import domain.synthesis.data.SharingProvider
import domain.synthesis.parser.PersonColumns
import domain.synthesis.parser.PersonCsvConfig
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import domain.synthesis.parser.createPersonCsvParser
import utils.csv.CsvParser
import java.nio.file.Path

// functions specific to default person implementation
// todo: make person generic in hh class, sharing class, drt class

/**
 * Provides a scope for configuring person repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [MutablePerson].
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
fun <C> C.persons(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutablePerson, PersonId>) C.() -> Unit,
) where C : HasPersonRepo<MutablePerson, Person> = mutableRepositoryScope<C, MutablePerson, PersonId>(
    getter = { mutablePersonRepository },
    sealed = sealed,
    scope,
)

/**
 * Loads persons from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [MutablePerson]
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 * @param repository The mutable repository of persons to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load persons from.
 * @param dependentRepositories Repositories that this loading step depends on.
 *                              Defaults to household, sharing provider, and DRT provider repositories.
 */
context(repository: MutableRepository<MutablePerson, PersonId>)
fun <C> C.loadPersons(
    resource: Resource<MutablePerson>,
    dependentRepositories: Set<Repository<*, *>> = setOf(
        householdRepository,
        sharingProviderRepository,
        drtProviderRepository,
    ),
) where C : HasPersonRepo<MutablePerson, *>, C : HasHouseholdRepo<MutableHousehold, *>,
        C : HasSharingProviderRepo<*, SharingProvider>, C : HasDrtProviderRepo<*, DrtProvider> =
    addResourceStep<C, MutablePerson, PersonId>(
        name = "load persons from ${resource.name}",
        resource = resource,
        dependentRepositories = dependentRepositories,
    )

/**
 * Creates a CSV resource for persons.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [MutablePerson]
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 * @param CFG The configuration type. Must implement [UnitConfig] and [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for persons. Defaults to [personCsvParser].
 * @param path The path to the person CSV file. Defaults to [config.sourceFiles.personCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryPersonFormat].
 * @return A [Resource] representing the person CSV.
 */
context(config: CFG)
fun <C, CFG> C.personCsv(
    parser: CsvParser<MutablePerson> = personCsvParser(),
    path: Path = config.sourceFiles.personCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePerson>? = binaryPersonFormat(),
): Resource<MutablePerson>
    where C : HasPersonRepo<MutablePerson, *>, C : HasHouseholdRepo<MutableHousehold, *>,
          C : HasSharingProviderRepo<*, SharingProvider>, C : HasDrtProviderRepo<*, DrtProvider>,
          CFG : UnitConfig, CFG : SourceFilesConfig =
    // TODO config as required upper bound type in context
    CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

/**
 * Creates a binary cache configuration for persons.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [MutablePerson]
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 * @param CFG The configuration type. Must implement [SourceFilesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C, CFG> C.binaryPersonFormat(): BinaryCacheConfig<MutablePerson>
    where C : HasPersonRepo<MutablePerson, *>, C : HasHouseholdRepo<MutableHousehold, *>,
          C : HasSharingProviderRepo<*, SharingProvider>, C : HasDrtProviderRepo<*, DrtProvider>,
          CFG : SourceFilesConfig =
    BinaryCacheConfig<MutablePerson>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryPersonReader(
            converter = this::getMutableHousehold,
            sharingConverter = this::getSharingProvider,
            drtConverter = this::getDrtProvider,
            contextSimulationSeed = config.seed,
        ),

        binaryWriter = BinaryPersonWriter(),
    )

/**
 * Creates a CSV parser for persons.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [MutablePerson]
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 * @param CFG The configuration type. Must implement [UnitConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [PersonCsvConfig].
 * @return A [CsvParser] for [MutablePerson].
 */
context(config: CFG)
fun <C, CFG> C.personCsvParser(
    customizeCsvConfig: PersonCsvConfig.() -> Unit = {},
): CsvParser<MutablePerson>
    where C : HasPersonRepo<MutablePerson, *>, C : HasHouseholdRepo<MutableHousehold, *>,
          C : HasSharingProviderRepo<*, SharingProvider>, C : HasDrtProviderRepo<*, DrtProvider>,
          CFG : UnitConfig =
    createPersonCsvParser(
        PersonCsvConfig(
            columns = PersonColumns(),
            employmentCodes = Employment,
            graduationCodes = Graduation,
            sexCodes = Sex,
            sharingProvidersByName = {
                sharingProviderRepository.elements.toList().associateBy { it.name }
            }, // TODO check if lazy still necessary
            drtProvidersByName = { drtProviderRepository.elements.toList().associateBy { it.name } },
            householdProvider = this::getMutableHousehold,
            hasHousehold = householdRepository::contains,
            incomeUnit = config.currencyUnit,
            seed = config.seed,
            errorHandling = config.errorHandling,
        ).also {
            it.customizeCsvConfig()
        },
    )
