package application.steps.parser.csv

import application.config.ShortTermConfig
import application.config.subconfigs.CoreCSVConfig
import application.steps.Config
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderRepo
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.Context
import core.modelsteps.ExecutionMode
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.filterIdsStep
import core.modelsteps.scopes.mutableRepositoryScope
import core.modelsteps.scopes.updateEachStep
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.Sex
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.parser.PersonColumns
import domain.synthesis.parser.PersonCsvConfig
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import domain.synthesis.parser.personCsvParser
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.report.ReportBuilder
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.reflect.KClass

object MyContext: Context, HasPersonRepo<Person>, HasHouseholdRepo<MutableHousehold>,
    HasSharingProviderRepo<SharingProvider>, HasDrtProviderRepo<DrtProvider> {
    override val execMode: ExecutionMode = ExecutionMode()
    override val scenarioName: String = "Test"
    override val report: ReportBuilder = ReportBuilder()
    override lateinit var personRepository: Repository<Person, PersonId>
    override lateinit var householdRepository: Repository<MutableHousehold, HouseholdId>
    override lateinit var sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
    override lateinit var drtProviderRepository: Repository<DrtProvider, DrtProviderId>
}

context(config: CFG)
fun <CTXT, CFG, P: Identifiable<PersonId>, M: P> CTXT.customPersons(
    personClass: KClass<M>,
    scope: context(MutableRepository<M, PersonId>, CFG) CTXT.() -> Unit
) where CTXT: HasPersonRepo<P>, CTXT: Context = mutableRepositoryScope<CTXT, CFG, M, PersonId>(
    "persons", this::personRepository.setter, scope
)

context(repository: MutableRepository<M, PersonId>)
fun <C: HasPersonRepo<P>, P: Identifiable<PersonId>, M: P> C.loadCustomPersons(
    resource: Resource<M>,
    dependentRepositories: Set<Repository<*,*>> = emptySet(),
) = addResourceStep(
    name = "load persons from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

context(config: ShortTermConfig<*>)
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

context(config: ShortTermConfig<*>)
fun <M> ShortTermConfig<*>.binaryFormat(
    reader: BinaryReader<M>,
    writer: BinaryWriter<M>,
) = BinaryCacheConfig<M>(reader, writer, config.cachePath!!)
//todo why is cache path nullable




// context requirements
interface HasHouseholdId: Identifiable<HouseholdId>
interface HasSharingProviderId: Identifiable<SharingProviderId>
interface HasDrtProviderId: Identifiable<DrtProviderId>


// functions specific to default person implementation
// todo: make person generic in hh clkass, sharing class, drt class


context(config: CFG)
fun <CTXT, CFG: Config> CTXT.persons(
    scope: context(MutableRepository<MutablePerson, PersonId>, CFG) CTXT.() -> Unit
) where CTXT : HasPersonRepo<Person> = mutableRepositoryScope<CTXT, CFG, MutablePerson, PersonId>(
    "persons", this::personRepository.setter, scope
)

context(repository: MutableRepository<MutablePerson, PersonId>)
fun <C> C.loadPersons(
    resource: Resource<MutablePerson>,
    dependentRepositories: Set<Repository<*,*>> = setOf(householdRepository, sharingProviderRepository, drtProviderRepository)
) where C: HasPersonRepo<Person>, C: HasHouseholdRepo<MutableHousehold>,
        C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>
= addResourceStep<C, MutablePerson, PersonId>(
    name = "load persons from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

context(config: CFG)
fun <C, CFG> C.personCsv(
    parser: CsvParser<MutablePerson> = personCsvParser(),
    path: Path = config.sourceFiles.personCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePerson>? = binaryPersonFormat()
): Resource<MutablePerson>
where C: HasPersonRepo<Person>, C: HasHouseholdRepo<MutableHousehold>,
      C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
      CFG: UnitConfig, CFG: SourceFilesConfig //TODO config as required upper bound type in context
    = CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

context(config: CFG)
fun <C, CFG> C.binaryPersonFormat(): BinaryCacheConfig<MutablePerson>
where C: HasPersonRepo<Person>, C: HasHouseholdRepo<MutableHousehold>,
      C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
      CFG: SourceFilesConfig
{
    return BinaryCacheConfig<MutablePerson>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryPersonReader(
            converter = this::getHousehold,
            sharingConverter = this::getSharingProvider,
            drtConverter = this::getDrtProvider,
            contextSimulationSeed = config.seed,
        ),

        binaryWriter = BinaryPersonWriter()
    )
}


context(config: CFG)
fun <C, CFG> C.personCsvParser(
    customizeCsvConfig: PersonCsvConfig.() -> Unit = {}
): CsvParser<MutablePerson>
    where C: HasPersonRepo<Person>, C: HasHouseholdRepo<MutableHousehold>,
          C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
          CFG: UnitConfig
 = personCsvParser(
    PersonCsvConfig(
        columns = PersonColumns(),
        employmentCodes = Employment,
        graduationCodes = Graduation,
        sexCodes = Sex,
        sharingProvidersByName = { sharingProviderRepository.elements.toList().associateBy { it.name } }, //TODO check if lazy still necessary
        drtProvidersByName = { drtProviderRepository.elements.toList().associateBy { it.name } },
        householdProvider = this::getHousehold,
        hasHousehold = householdRepository::contains,
        incomeUnit = config.currencyUnit,
        seed = config.seed,
        errorHandling = config.errorHandling,
    ).also {
        it.customizeCsvConfig()
    }
)





//experiment for nested entity definition scopes
context(repository: MutableRepository<MutablePerson, PersonId>, households: Repository<MutableHousehold, HouseholdId>)
fun <C> C.loadPersonsNested(
    resource: Resource<MutablePerson>,
    dependentRepositories: Set<Repository<*,*>> = setOf(households, sharingProviderRepository, drtProviderRepository)
)
        where C: HasPersonRepo<Person>,
              C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>
        = addResourceStep<C, MutablePerson, PersonId>(
    name = "load persons from ${resource.name}",
    resource = resource,
    dependentRepositories = dependentRepositories,
)

context(config: CFG, households: Repository<MutableHousehold, HouseholdId>)
fun <C, CFG> C.personCsvNested(
    parser: CsvParser<MutablePerson> = personCsvParserNested(),
    path: Path = config.sourceFiles.personCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePerson>? = binaryPersonFormatNested()
): Resource<MutablePerson>
        where C: HasPersonRepo<Person>,
              C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
              CFG: UnitConfig, CFG: SourceFilesConfig //TODO config as required upper bound type in context
        = CsvResource(path, parser, delimiter).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}




context(config: CFG, households: Repository<MutableHousehold, HouseholdId>)
fun <C, CFG> C.binaryPersonFormatNested(): BinaryCacheConfig<MutablePerson>
        where C: HasPersonRepo<Person>,
              C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
              CFG: SourceFilesConfig
{
    return BinaryCacheConfig<MutablePerson>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryPersonReader(
            converter = households::getValue,
            sharingConverter = this::getSharingProvider,
            drtConverter = this::getDrtProvider,
            contextSimulationSeed = config.seed,
        ),

        binaryWriter = BinaryPersonWriter()
    )
}


context(config: CFG, households: Repository<MutableHousehold, HouseholdId>)
fun <C, CFG> C.personCsvParserNested(
    customizeCsvConfig: PersonCsvConfig.() -> Unit = {}
): CsvParser<MutablePerson>
        where C: HasPersonRepo<Person>,
              C: HasSharingProviderRepo<SharingProvider>, C: HasDrtProviderRepo<DrtProvider>,
              CFG: UnitConfig
        = personCsvParser(
    PersonCsvConfig(
        columns = PersonColumns(),
        employmentCodes = Employment,
        graduationCodes = Graduation,
        sexCodes = Sex,
        sharingProvidersByName = { sharingProviderRepository.elements.toList().associateBy { it.name } }, //TODO check if lazy still necessary
        drtProvidersByName = { drtProviderRepository.elements.toList().associateBy { it.name } },
        householdProvider = households::getValue,
        hasHousehold = households::contains,
        incomeUnit = config.currencyUnit,
        seed = config.seed,
        errorHandling = config.errorHandling,
    ).also {
        it.customizeCsvConfig()
    }
)


//
//
//@Suppress("LongParameterList", "UnusedParameter")
//fun LoadPersonsContext.preparePersons(
//    path: Path = defaultPersonPath,
//    delimiter: String = SEMICOLON,
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    columns: PersonColumns = PersonColumns(),
//    incomeUnit: CurrencyUnit = costUnit,
//    addResourceStep: AbstractAddResourceStep<MutablePerson, PersonId> =
//        personsFromCsvStep(path) {
//            this.delimiter = delimiter
//            this.errorHandling = errorHandling
//            this.columns = columns
//            this.incomeUnit = incomeUnit
//        }.step,
//) {
//    this.preparePersonsFile(addResourceStep)
//}

//data class PersonCsvConfig(
//    var path: Path,
//    var columns: PersonColumns = PersonColumns(),
//    var delimiter: String = SEMICOLON,
//    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    var incomeUnit: CurrencyUnit,
//
//) {
//
//    /* Filtering should not be done on a resource step but afterward. For performance it doesnt matter because CSV
//    parser remains slow regardless of filter. And Resource wrapping is nigh impossible because each resource step could
//    define its own filtering logic that doesnt share any similarity with any other step.
//     */
//    val filter: PersonColumns.(Row, LoadPersonsContext) -> Boolean = { row, context ->
//
//        HouseholdId(row.long(this.householdColumn)) in context.householdRepository
//    }
//}
//
///**
// * Collects the instructions for initializing the person repository. Automatically sets the reader and writer
// * to the proper binary implementations.
// */
//class PersonStepBuilder(
//    val seed: Long,
//    val converter: (HouseholdId) -> MutableHousehold?,
//    val sharingConverter: (SharingProviderId) -> SharingProvider,
//    val drtConverter: (DrtProviderId) -> DrtProvider,
//) :
//    GroupedStepBuilder<MutablePerson, PersonId>() {
//    override val reader = BinaryPersonReader(converter, sharingConverter, drtConverter, seed)
//    override val writer: BinaryWriter<MutablePerson> = BinaryPersonWriter()
//
////    override fun fromCSV(
////        source: Path,
////        lambda: context(Path) () -> AbstractAddResourceStep<MutablePerson, PersonId>,
////    ): FileBasedAddResourceStep<MutablePerson, PersonId> {
////        return context(source) {
////            FileBasedAddResourceStep(source, lambda(source))
////        }
////    }
//}
//
///**
// * Operate on the context object. Apply the steps defined in the builder and then finalize the repository.
// */
//fun LoadPersonsContext.persons(lambda: PersonStepBuilder.() -> Unit) {
//    val lpcBuilder = PersonStepBuilder(
//        this.simulationSeed,
//        householdRepository::get,
//        sharingProviderRepository::getValue,
//        drtProviderRepository::getValue,
//    )
//    lambda(lpcBuilder)
//    lpcBuilder.executeOn(this)
//    finishPersons()
//}
//
//fun LoadPersonsContext.personsFromCsvStep(
//    path: Path = defaultPersonPath,
//    lambda: PersonCsvConfig.() -> Unit,
//): FileBasedAddResourceStep<MutablePerson, PersonId> {
//    val config = PersonCsvConfig(path = path, incomeUnit = costUnit)
//    config.apply(lambda)
//    return config.run {
//        val sharingProvidersByName: () -> Map<String, SharingProvider> = {
//            sharingProviderRepository.elements.associateBy { it.name.lowercase() }
//        }
//
//        val drtProvidersByName: () -> Map<String, DrtProvider> = {
//            drtProviderRepository.elements.associateBy { it.name.lowercase() }
//        }
//        val csvParser =
//            personCsvParser(errorHandling, columns, incomeUnit, sharingProvidersByName, drtProvidersByName) {
//                getHousehold(it)
//            }
//
//        val internalFilter = { row: Row -> columns.filter(row, this@personsFromCsvStep) }
//        val step = LoadCsvStep<MutablePerson, PersonId>(
//            path = path,
//            name = "Load Person from csv",
//            parser = csvParser.withFilter(internalFilter),
//            delimiter = delimiter,
//            repository = personRepository,
//            dependentRepositories = setOf(householdRepository, sharingProviderRepository),
//            validationMock = listOf() // TODO
//        )
//        FileBasedAddResourceStep(path, step)
//    }
//}


