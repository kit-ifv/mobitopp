package application.steps.parser.csv

import application.steps.Config
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
import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.Sex
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

context(config: CFG)
fun <CTXT, CFG: Config> CTXT.persons(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutablePerson, PersonId>, CFG) CTXT.() -> Unit
) where CTXT : HasPersonRepo<MutablePerson, Person> = mutableRepositoryScope<CTXT, CFG, MutablePerson, PersonId>(
    getter = { mutablePersonRepository },
    sealed = sealed,
    scope
)

context(repository: MutableRepository<MutablePerson, PersonId>)
fun <C> C.loadPersons(
    resource: Resource<MutablePerson>,
    dependentRepositories: Set<Repository<*,*>> = setOf(householdRepository, sharingProviderRepository, drtProviderRepository)
) where C: HasPersonRepo<MutablePerson, *>, C: HasHouseholdRepo<MutableHousehold, *>,
        C: HasSharingProviderRepo<*, SharingProvider>, C: HasDrtProviderRepo<*, DrtProvider>
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
where C: HasPersonRepo<MutablePerson, *>, C: HasHouseholdRepo<MutableHousehold, *>,
      C: HasSharingProviderRepo<*, SharingProvider>, C: HasDrtProviderRepo<*, DrtProvider>,
      CFG: UnitConfig, CFG: SourceFilesConfig //TODO config as required upper bound type in context
    = CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    }

context(config: CFG)
fun <C, CFG> C.binaryPersonFormat(): BinaryCacheConfig<MutablePerson>
where C: HasPersonRepo<MutablePerson, *>, C: HasHouseholdRepo<MutableHousehold, *>,
      C: HasSharingProviderRepo<*, SharingProvider>, C: HasDrtProviderRepo<*, DrtProvider>,
      CFG: SourceFilesConfig
{
    return BinaryCacheConfig<MutablePerson>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryPersonReader(
            converter = this::getMutableHousehold,
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
    where C: HasPersonRepo<MutablePerson, *>, C: HasHouseholdRepo<MutableHousehold, *>,
          C: HasSharingProviderRepo<*, SharingProvider>, C: HasDrtProviderRepo<*, DrtProvider>,
          CFG: UnitConfig
 = createPersonCsvParser(
    PersonCsvConfig(
        columns = PersonColumns(),
        employmentCodes = Employment,
        graduationCodes = Graduation,
        sexCodes = Sex,
        sharingProvidersByName = { sharingProviderRepository.elements.toList().associateBy { it.name } }, //TODO check if lazy still necessary
        drtProvidersByName = { drtProviderRepository.elements.toList().associateBy { it.name } },
        householdProvider = this::getMutableHousehold,
        hasHousehold = householdRepository::contains,
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


