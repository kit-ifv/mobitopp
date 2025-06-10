package application.steps.parser.csv

import core.modelsteps.Context
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.SealStep
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
import utils.CodePlan
import utils.ErrorHandling
import utils.ID
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decodeName
import utils.csv.id
import utils.csv.int
import utils.csv.withFilter
import java.nio.file.Path

interface LoadPrivateCarsContext : Context {
    val carRepository: MutableRepository<MutablePrivateCar, CarId>
    val engineCodes: CodePlan<EngineType>
    val carSegmentCodes: CodePlan<CarSegment>

    val householdRepository: Repository<MutableHousehold, HouseholdId>
    val personRepository: Repository<Person, PersonId>

    val defaultCarPath: Path
        get() = demandFolder.resolve("demand-data").resolve("car.csv")

    fun getOwnerHousehold(
        row: Row,
        ownerColumn: String
    ) = requireNotNull(
        householdRepository[row.id(ownerColumn)]
    ) {
        "Referenced household id ${row(ownerColumn)} could not be found in householdRepo:" +
            " ${householdRepository.elements.map { it.id }.toList()}"
    }

    fun getMainUser(
        row: Row,
        mainUserColumn: String
    ) = requireNotNull(
        personRepository[row.id(mainUserColumn)]
    ) {
        "Referenced person id ${row(mainUserColumn)} could not be found in personRepo:" +
            " ${personRepository.elements.map { it.id }.toList()}"
    }
}

data class CarColumns(
    val ownerColumn: String = "ownerId",
    val mainUserColumn: String = "mainUserId",
    val engineTypeColumn: String = "carType",
    val segmentColumnIndex: Int = 7,
    val seatsColumnIndex: Int = 8,
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
    val csvParser = CsvParser<MutablePrivateCar>(errorHandling) { row ->

        MutablePrivateCar(
            id = ID(row.index.toLong()),
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

    this.preparePrivateCarsFile(csvParser.withFilter { columns.filter(it, this) }, path, delimiter)
}

fun LoadPrivateCarsContext.preparePrivateCarsFile(
    parser: CsvParser<MutablePrivateCar>,
    path: Path = defaultCarPath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<MutablePrivateCar, CarId>(
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
