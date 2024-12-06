package usecases.steps.legacyData

import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EngineType
import domain.data.Household
import domain.data.HouseholdId
import domain.data.Person
import domain.data.PersonId
import domain.data.PrivateCar
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.id
import utils.csv.withFilter
import java.io.File

interface LoadPrivateCarsContext : Context {
    val carRepository: MutableRepository<PrivateCar, CarId>
    val engineCodes: CodePlan<EngineType>
    val carSegmentCodes: CodePlan<CarSegment>

    val householdRepository: Repository<Household, HouseholdId>
    val personRepository: Repository<Person, PersonId>

    val defaultCarFile: File
        get() = File(demandFolder.path + "\\demand-data\\car.csv")
}

data class CarColumns(
    val ownerColumn: String = "ownerId",
    val mainUserColumn: String = "mainUserId",
    val engineTypeColumn: String = "carType",
    val segmentColumnIndex: Int = 7,
    val seatsColumnIndex: Int = 8,
)

@Suppress("LongParameterList", "UnusedParameter")
fun <S, C> S.preparePrivateCars(
    file: File = context.defaultCarFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: CarColumns = CarColumns(),
    // carSegmentCode: CodePlan<CarSegment>? = null, should be consistent within project -> only define in context
    carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    filter: CarColumns.(Row, C) -> Boolean = { _, _ -> true }

) where S : ModelExecution<C>, C : LoadPrivateCarsContext {
    val householdRepo = context.householdRepository // }
    val personRepo = context.personRepository // }

    val csvParser = CsvParser<PrivateCar>(errorHandling) { row ->
        null
//        PrivateCarBuilder( // TODO
//            segment = row.decodeName(columns.segmentColumnIndex, context.carSegmentCodes),
//            engine = row(columns.engineTypeColumn, ::parseEngineType),
//            seats = row.int(columns.seatsColumnIndex),
//            owner = getOwnerHousehold(householdRepo, row, columns.ownerColumn),
//            mainUser = getMainUser(personRepo, row, columns.mainUserColumn),
//            carEngineStatistics = carEngineStatistics
//        )
    }

    this.preparePrivateCarsFile(csvParser.withFilter { columns.filter(it, context) }, file, delimiter)
}

fun <S, C> S.preparePrivateCarsFile(
    parser: CsvParser<PrivateCar>,
    file: File = context.defaultCarFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadPrivateCarsContext {
    this.addStep(
        LoadCsvStep<PrivateCar, CarId>(
            file = file,
            name = "Load private cars from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.carRepository,
            dependentRepositories = context.let {
                setOf(
                    it.householdRepository,
                    it.personRepository
                )
            },
            validationMock = listOf() // TODO
        )
    )
}

fun <S, C> S.finishPrivateCars() where S : ModelExecution<C>, C : LoadPrivateCarsContext {
    this.addStep(SealStep(context.carRepository))
}

fun <S, C> S.loadPrivateCars()
    where S : ModelExecution<C>,
          C : LoadPrivateCarsContext {
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

internal fun getOwnerHousehold(
    householdRepo: Repository<Household, HouseholdId>, // () ->
    row: Row,
    ownerColumn: String
) = requireNotNull(
    householdRepo.getById(row.id(ownerColumn))
) {
    "Referenced household id ${row(ownerColumn)} could not be found in householdRepo:" +
        " ${householdRepo.elements.map { it.id }.toList()}"
}

internal fun getMainUser(
    personRepo: Repository<Person, PersonId>, // () ->
    row: Row,
    mainUserColumn: String
) = requireNotNull(
    personRepo.getById(row.id(mainUserColumn))
) {
    "Referenced person id ${row(mainUserColumn)} could not be found in personRepo:" +
        " ${personRepo.elements.map { it.id }.toList()}"
}
