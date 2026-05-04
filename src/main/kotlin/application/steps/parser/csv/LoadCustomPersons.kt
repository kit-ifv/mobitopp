package application.steps.parser.csv

import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderRepo
import application.steps.SourceFilesConfig
import core.modelsteps.Context
import core.modelsteps.ExecutionMode
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableDrtProviderData
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.report.ReportBuilder
import java.nio.file.Path
import kotlin.reflect.KClass

object MyContext: Context, HasPersonRepo<MutablePerson, Person>, HasHouseholdRepo<MutableHousehold, Household>,
    HasSharingProviderRepo<MutableSharingProvider, SharingProvider>,
    HasDrtProviderRepo<MutableDrtProviderData, DrtProvider> {
    override val execMode: ExecutionMode = ExecutionMode()
    override val scenarioName: String = "Test"
    override val report: ReportBuilder = ReportBuilder()
    override val mutablePersonRepository: MutableRepository<MutablePerson, PersonId> = MapRepository("person")
    override val mutableHouseholdRepository: MutableRepository<MutableHousehold, HouseholdId> = MapRepository("household")
    override val mutableSharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId> = MapRepository("sharingProvider")
    override val mutableDrtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId> = MapRepository("drtProvider")
}

context(config: CFG)
fun <CTXT, CFG, P: Identifiable<PersonId>, M: P> CTXT.customPersons(
    personClass: KClass<M>,
    sealed: Boolean = false,
    scope: context(MutableRepository<M, PersonId>, CFG) CTXT.() -> Unit
) where CTXT: HasPersonRepo<M, P>, CTXT: Context = mutableRepositoryScope<CTXT, CFG, M, PersonId>(
    getter = { mutablePersonRepository }, sealed, scope
)

context(repository: MutableRepository<M, PersonId>)
fun <C: HasPersonRepo<M, P>, P: Identifiable<PersonId>, M: P> C.loadCustomPersons(
    resource: Resource<M>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
) = addResourceStep(
    name = "load persons from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

context(config: SourceFilesConfig)
fun <P: Identifiable<PersonId>> customPersonCsv(
    parser: CsvParser<P>,
    path: Path = config.sourceFiles.personCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<P>? = null,
): Resource<P> = CsvResource(path, parser, delimiter).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

context(config: SourceFilesConfig)
fun <M> binaryFormat(
    reader: BinaryReader<M>,
    writer: BinaryWriter<M>,
) = BinaryCacheConfig<M>(reader, writer, config.cachePath)


// context requirements
interface HasHouseholdId: Identifiable<HouseholdId>
interface HasSharingProviderId: Identifiable<SharingProviderId>
interface HasDrtProviderId: Identifiable<DrtProviderId>