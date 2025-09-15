package application.steps.parser.csv

import core.modelsteps.AddResourceStep
import core.modelsteps.FileBasedAddResourceStep
import core.modelsteps.GroupedStepBuilder
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.SealStep
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.buildEngine
import domain.synthesis.parser.binary.BinaryCarReader
import domain.synthesis.parser.binary.BinaryCarWriter
import utils.CodePlan
import utils.ErrorHandling
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.csv.DefaultCsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decodeName
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter
import java.nio.file.Path

interface LoadPrivateCarsContext : DemandSimContext {
    val carRepository: MutableRepository<MutablePrivateCar, CarId>
    val engineCodes: CodePlan<EngineType>
    val carSegmentCodes: CodePlan<CarSegment>

    val householdRepository: Repository<MutableHousehold, HouseholdId>
    val personRepository: Repository<Person, PersonId>

    val defaultCarPath: Path
        get() = dataFolder.resolve("demand-data").resolve("car.csv")

    fun getOwnerHousehold(
        row: Row,
        ownerColumn: String
    ) = requireNotNull(
        householdRepository[HouseholdId(row.long(ownerColumn))]
    ) {
        "Referenced household id ${row(ownerColumn)} could not be found in householdRepo:" +
            " ${householdRepository.elements.map { it.id }.toList()}"
    }

    fun getMainUser(
        row: Row,
        mainUserColumn: String
    ) = requireNotNull(
        personRepository[PersonId(row.long(mainUserColumn))]
    ) {
        "Referenced person id ${row(mainUserColumn)} could not be found in personRepo:" +
            " ${personRepository.elements.map { it.id }.toList()}"
    }
}
fun LoadPrivateCarsContext.privateCars(lambda: PrivateCarStepBuilder.() -> Unit) {
    val lpcBuilder = PrivateCarStepBuilder(householdRepository::get, personRepository::get)
    lambda(lpcBuilder)
    lpcBuilder.executeOn(this)
    finishPrivateCars()
}

class PrivateCarStepBuilder(
    val householdConverter: (HouseholdId) -> MutableHousehold?,
    val personConverter: (PersonId) -> Person?,
) : GroupedStepBuilder<MutablePrivateCar, CarId>() {
    override val reader: BinaryReader<MutablePrivateCar> = BinaryCarReader(householdConverter, personConverter)
    override val writer: BinaryWriter<MutablePrivateCar> = BinaryCarWriter()

    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutablePrivateCar, CarId>,
    ): FileBasedAddResourceStep<MutablePrivateCar, CarId> {
        return context(source) {
            FileBasedAddResourceStep(source, lambda(source))
        }
    }
}
data class CarColumns(
    val ownerColumn: String = "ownerId",
    val mainUserColumn: String = "mainUserId",
    val engineTypeColumn: String = "carType",
    val segmentColumnIndex: Int = 7,
    val seatsColumnIndex: Int = 8,
)
fun LoadPrivateCarsContext.privateCarCsvParser(
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: CarColumns = CarColumns(),
    carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
): DefaultCsvParser<MutablePrivateCar> {
    return CsvParser<MutablePrivateCar>(errorHandling) { row ->

        MutablePrivateCar(
            id = CarId(row.index.toLong()),
            owner = getOwnerHousehold(row, columns.ownerColumn)
        ) {
            seats = row.int(columns.seatsColumnIndex)
            mainUser = getMainUser(row, columns.mainUserColumn)
            segment = row.decodeName(columns.segmentColumnIndex, carSegmentCodes)
            val engineType = row(columns.engineTypeColumn, ::parseEngineType)
            engine = carEngineStatistics.buildEngine(segment, engineType)
//            location = owner.location
        }
    }
}
fun LoadPrivateCarsContext.runStep(
    step: AddResourceStep<MutablePrivateCar, CarId>
) = runStep {
    step
}
fun LoadPrivateCarsContext.privateCarsFromCsvStep(
    path: Path = defaultCarPath,
    lambda: PrivateCarCsvConfig.() -> Unit
): FileBasedAddResourceStep<MutablePrivateCar, CarId> {
    val config = PrivateCarCsvConfig(path)
    config.apply(lambda)
    return config.run {
        val rawParser = privateCarCsvParser(errorHandling, columns, carEngineStatistics)
        val parser = rawParser.withFilter { columns.filter(it, this@privateCarsFromCsvStep) }
        val step = LoadCsvStep<MutablePrivateCar, CarId>(
            path = path,
            name = "Load private cars from csv",
            parser = parser,
            delimiter = delimiter,
            repository = carRepository,
            dependentRepositories = setOf(
                householdRepository,
                personRepository
            ),
            validationMock = listOf() // TODO
        )
        FileBasedAddResourceStep(path, step)
    }
}
data class PrivateCarCsvConfig(
    var path: Path,
    var delimiter: String = SEMICOLON,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var columns: CarColumns = CarColumns(),
    var carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    var filter: CarColumns.(Row, LoadPrivateCarsContext) -> Boolean = { _, _ -> true }
)

@Suppress("LongParameterList", "UnusedParameter")
fun LoadPrivateCarsContext.preparePrivateCars(
    path: Path = defaultCarPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: CarColumns = CarColumns(),
    carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    filter: CarColumns.(Row, LoadPrivateCarsContext) -> Boolean = { _, _ -> true }
) {
    val (_, step) = privateCarsFromCsvStep(path) {
        this.delimiter = delimiter
        this.errorHandling = errorHandling
        this.columns = columns
        this.carEngineStatistics = carEngineStatistics
    }
    this.runStep(step)
}

fun LoadPrivateCarsContext.finishPrivateCars() = runStep {
    SealStep(carRepository)
}

fun LoadPrivateCarsContext.loadPrivateCars() {
    this.preparePrivateCars()
    this.finishPrivateCars()
}

internal fun parseEngineType(string: String): EngineType = when (string) {
    "conventional" -> EngineType.COMBUSTION
    "bev" -> EngineType.ELECTRIC
    "erev" -> EngineType.HYBRID
    else -> throw IllegalArgumentException(
        "Cannot parse string $string to EngineType: expected 'conventional', 'bev' or 'erev'!"
    )
}
