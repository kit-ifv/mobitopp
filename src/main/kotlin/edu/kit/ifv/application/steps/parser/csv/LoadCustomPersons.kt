package edu.kit.ifv.application.steps.parser.csv

import edu.kit.ifv.application.steps.HasPersonRepo
import edu.kit.ifv.application.steps.SourceFilesConfig
import edu.kit.ifv.binary.BinaryReader
import edu.kit.ifv.binary.BinaryWriter
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.resources.BinaryCacheConfig
import edu.kit.ifv.core.modelsteps.resources.CsvResource
import edu.kit.ifv.core.modelsteps.resources.MutableRepository
import edu.kit.ifv.core.modelsteps.resources.Repository
import edu.kit.ifv.core.modelsteps.resources.Resource
import edu.kit.ifv.core.modelsteps.resources.cachedCsv
import edu.kit.ifv.core.modelsteps.scopes.addResourceStep
import edu.kit.ifv.core.modelsteps.scopes.mutableRepositoryScope
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.utils.Identifiable
import edu.kit.ifv.utils.csv.CsvParser
import java.nio.file.Path
import kotlin.reflect.KClass

// object MyContext : Context, HasPersonRepo<MutablePerson, Person>, HasHouseholdRepo<MutableHousehold, Household>,
//    HasSharingProviderRepo<MutableSharingProvider, SharingProvider>,
//    HasDrtProviderRepo<MutableDrtProviderData, DrtProvider> {
//    override var currentStep: String = ""
//    override val execMode: ExecutionMode = ExecutionMode()
//    override val scenarioName: String = "Test"
//    override val report: ReportBuilder = ReportBuilder()
//    override val mutablePersonRepository: MutableRepository<MutablePerson, PersonId> = MapRepository("person")
//    override val mutableHouseholdRepository: MutableRepository<MutableHousehold, HouseholdId> =
//        MapRepository("household")
//    override val mutableSharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId> =
//        MapRepository("sharingProvider")
//    override val mutableDrtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId> =
//        MapRepository("drtProvider")
// }

/**
 * Provides a scope for configuring custom person repositories.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] and [Context].
 * @param P The person type.
 * @param M The mutable person type.
 * @param personClass The class of the mutable person.
 * @param sealed Whether the repository should be sealed after the scope finishes. Defaults to `false`.
 * @param scope The configuration scope.
 */
@Suppress("UnusedParameter")
fun <C, P : Identifiable<PersonId>, M : P> C.customPersons(
    personClass: KClass<M>,
    sealed: Boolean = false,
    scope: context(MutableRepository<M, PersonId>) C.() -> Unit,
) where C : HasPersonRepo<M, P>, C : Context = mutableRepositoryScope<C, M, PersonId>(
    getter = { mutablePersonRepository },
    sealed,
    scope,
)

/**
 * Loads custom persons into the repository from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo].
 * @param P The person type.
 * @param M The mutable person type.
 * @param repository The mutable repository of persons to populate. Provided via context.
 * @param resource The resource (e.g., CSV or binary file) to load persons from.
 * @param dependentRepositories Repositories that this loading step depends on.
 */
context(repository: MutableRepository<M, PersonId>)
fun <C : HasPersonRepo<M, P>, P : Identifiable<PersonId>, M : P> C.loadCustomPersons(
    resource: Resource<M>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
) = addResourceStep(
    name = "load persons from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

/**
 * Creates a CSV resource for custom persons.
 *
 * @param P The person type.
 * @param config The source files configuration. Provided via context.
 * @param parser The CSV parser for the person type.
 * @param path The path to the person CSV file. Defaults to [config.sourceFiles.personCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching.
 * @return A [Resource] representing the custom person CSV.
 */
context(config: SourceFilesConfig)
fun <P : Identifiable<PersonId>> customPersonCsv(
    parser: CsvParser<P>,
    path: Path = config.sourceFiles.personCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<P>? = null,
): Resource<P> = CsvResource(path, parser, delimiter).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

/**
 * Creates a binary cache configuration.
 *
 * @param M The entity type.
 * @param config The source files configuration. Provided via context.
 * @param reader The binary reader for type [M].
 * @param writer The binary writer for type [M].
 * @return A [BinaryCacheConfig] instance.
 */
context(config: SourceFilesConfig)
fun <M> binaryFormat(reader: BinaryReader<M>, writer: BinaryWriter<M>) =
    BinaryCacheConfig<M>(reader, writer, config.cachePath)

// // context requirements
// interface HasHouseholdId : Identifiable<HouseholdId>
// interface HasSharingProviderId : Identifiable<SharingProviderId>
// interface HasDrtProviderId : Identifiable<DrtProviderId>
