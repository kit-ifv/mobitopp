package application.steps.parser.csv

import application.steps.CarCodesConfig
import application.steps.Config
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
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.parser.CarColumns
import domain.synthesis.parser.PrivateCarCsvConfig
import domain.synthesis.parser.binary.BinaryCarReader
import domain.synthesis.parser.binary.BinaryCarWriter
import domain.synthesis.parser.createPrivateCarCsvParser
import utils.csv.CsvParser
import utils.csv.long
import java.nio.file.Path


context(config: CFG)
fun <CTXT, CFG: Config> CTXT.cars(
    sealed: Boolean = false,
    scope: context(MutableRepository<MutablePrivateCar, CarId>, CFG) CTXT.() -> Unit
) where CTXT : HasCarRepo<MutablePrivateCar, *> = mutableRepositoryScope<CTXT, CFG, MutablePrivateCar, CarId>(
    getter = { mutableCarRepository },
    sealed = sealed,
    scope
)


context(repository: MutableRepository<MutablePrivateCar, CarId>)
fun <C> C.loadCars(
    resource: Resource<MutablePrivateCar>,
) where C: HasPersonRepo<MutablePerson, *>, C: HasHouseholdRepo<MutableHousehold, *>
        = addResourceStep<C, MutablePrivateCar, CarId>(
    name = "load cars from ${resource.name}",
    resource = resource,
)

context(config: CFG)
fun <C, CFG> C.carCsv(
    parser: CsvParser<MutablePrivateCar> = privateCarCsvParser(),
    path: Path = config.sourceFiles.privateCarsCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePrivateCar>? = binaryPrivateCarFormat() //TODO move binary format to load level?
): Resource<MutablePrivateCar>
where C: HasPersonRepo<*, Person>, C: HasHouseholdRepo<MutableHousehold, *>, CFG: SourceFilesConfig, CFG: CarCodesConfig
= CsvResource(path, parser, delimiter).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

context(config: CFG)
fun <C, CFG> C.privateCarCsvParser(
    customizeCsvConfig: PrivateCarCsvConfig.() -> Unit = {}
): CsvParser<MutablePrivateCar>
where C: HasPersonRepo<*, Person>, C: HasHouseholdRepo<MutableHousehold, *>, CFG: CarCodesConfig
= createPrivateCarCsvParser(
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
    }
)


context(config: CFG)
fun <C, CFG> C.binaryPrivateCarFormat(): BinaryCacheConfig<MutablePrivateCar>
where C: HasPersonRepo<*, Person>, C: HasHouseholdRepo<MutableHousehold, *>,
      CFG: SourceFilesConfig
{
    return BinaryCacheConfig<MutablePrivateCar>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryCarReader(
            householdConverter = this.mutableHouseholdRepository::get,
            personConverter = this.personRepository::get,
            carEngineStatistics = CarEngineStatistics()
        ),

        binaryWriter = BinaryCarWriter()
    )
}




private const val ERROR_OUTPUT_SIZE = 5 //TODO use in context requirements?

//interface LoadPrivateCarsContext : DemandSimContext {
//    val carRepository: MutableRepository<MutablePrivateCar, CarId>
//    val engineCodes: CodePlan<EngineType>
//    val carSegmentCodes: CodePlan<CarSegment>
//
//    val householdRepository: Repository<MutableHousehold, HouseholdId>
//    val personRepository: Repository<Person, PersonId>
//
//    val defaultCarPath: Path
//        get() = dataFolder.resolve("demand-data").resolve("car.csv")
//
//    fun getOwnerHousehold(
//        row: Row,
//        ownerColumn: String
//    ) = requireNotNull(
//        householdRepository[HouseholdId(row.long(ownerColumn))]
//    ) {
//        "Referenced household id ${row(ownerColumn)} could not be found in householdRepo:" +
//            " ${householdRepository.elements.map { it.id }.toList()
//                .sortedBy{abs(it.value - row(ownerColumn).toLong())}.take(ERROR_OUTPUT_SIZE)}"
//    }
//
//    fun getMainUser(
//        row: Row,
//        mainUserColumn: String
//    ) = requireNotNull(
//        personRepository[PersonId(row.long(mainUserColumn))]
//    ) {
//        "Referenced person id ${row(mainUserColumn)} could not be found in personRepo:" +
//            " ${personRepository.elements.map { it.id }.toList()
//                .sortedBy{abs(it.value - row(mainUserColumn).toLong())}.take(ERROR_OUTPUT_SIZE)}"
//    }
//}
//fun LoadPrivateCarsContext.privateCars(lambda: PrivateCarStepBuilder.() -> Unit) {
//    val lpcBuilder = PrivateCarStepBuilder(householdRepository::get, personRepository::get)
//    lambda(lpcBuilder)
//    lpcBuilder.executeOn(this)
//    finishPrivateCars()
//}

//class PrivateCarStepBuilder(
//    val householdConverter: (HouseholdId) -> MutableHousehold?,
//    val personConverter: (PersonId) -> Person?,
//) : GroupedStepBuilder<MutablePrivateCar, CarId>() {
//    override val reader: BinaryReader<MutablePrivateCar> = BinaryCarReader(householdConverter, personConverter)
//    override val writer: BinaryWriter<MutablePrivateCar> = BinaryCarWriter()
//
////    override fun fromCSV(
////        source: Path,
////        lambda: context(Path) () -> AbstractAddResourceStep<MutablePrivateCar, CarId>,
////    ): FileBasedAddResourceStep<MutablePrivateCar, CarId> {
////        return context(source) {
////            FileBasedAddResourceStep(source, lambda(source))
////        }
////    }
//}

//fun LoadPrivateCarsContext.runStep(
//    step: AbstractAddResourceStep<MutablePrivateCar, CarId>
//) = runStep {
//    step
//}
//fun LoadPrivateCarsContext.privateCarsFromCsvStep(
//    path: Path = defaultCarPath,
//    lambda: PrivateCarCsvConfig.() -> Unit
//): FileBasedAddResourceStep<MutablePrivateCar, CarId> {
//    val config = PrivateCarCsvConfig(path)
//    config.apply(lambda)
//    return config.run {
//        val rawParser = privateCarCsvParser(errorHandling, columns, carEngineStatistics)
//        val parser = rawParser.withFilter { columns.filter(it, this@privateCarsFromCsvStep) }
//        val step = LoadCsvStep<MutablePrivateCar, CarId>(
//            path = path,
//            name = "Load private cars from csv",
//            parser = parser,
//            delimiter = delimiter,
//            repository = carRepository,
//            dependentRepositories = setOf(
//                householdRepository,
//                personRepository
//            ),
//            validationMock = listOf() // TODO
//        )
//        FileBasedAddResourceStep(path, step)
//    }
//}
//
//
//val defaultFilter: CarColumns.(Row, LoadPrivateCarsContext) -> Boolean = { row, context ->
//    HouseholdId(row.long(this.ownerColumn)) in context.householdRepository
//}
//
//@Suppress("LongParameterList", "UnusedParameter")
//fun LoadPrivateCarsContext.preparePrivateCars(
//    path: Path = defaultCarPath,
//    delimiter: String = SEMICOLON,
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    columns: CarColumns = CarColumns(),
//    carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
//    filter: CarColumns.(Row, LoadPrivateCarsContext) -> Boolean = defaultFilter
//) {
//    val (_, step) = privateCarsFromCsvStep(path) {
//        this.delimiter = delimiter
//        this.errorHandling = errorHandling
//        this.columns = columns
//        this.carEngineStatistics = carEngineStatistics
//        this.filter = filter
//    }
//    this.runStep(step)
//}
//
//fun LoadPrivateCarsContext.finishPrivateCars() = runStep {
//    SealStep(carRepository)
//}
//
//fun LoadPrivateCarsContext.loadPrivateCars() {
//    this.preparePrivateCars()
//    this.finishPrivateCars()
//}
