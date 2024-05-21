package usecases.legacyData

import domain.data.CarEngineStatistics
import domain.data.CarSegment
import domain.data.EngineType
import domain.data.HouseholdData
import domain.data.HouseholdId
import domain.data.PersonData
import domain.data.PersonId
import domain.data.PrivateCarBuilder
import modeling.steps.AddCsvStep
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.ModelExecution
import modeling.steps.Repository
import usecases.BasePrivateCarContext
import usecases.HouseholdContext
import usecases.PersonContext
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decodeName
import utils.csv.id
import utils.csv.int
import java.io.File

@Suppress("LongParameterList")
fun <S, C> S.preparePrivateCars(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    ownerColumn: String = "ownerId",
    mainUserColumn: String = "mainUserId",
    engineTypeColumn: String = "carType",
    segmentColumnIndex: Int = 7,
    carSegmentCode: CodePlan<CarSegment>? = null,
    seatsColumnIndex: Int = 8,
    carEngineStatistics: CarEngineStatistics = CarEngineStatistics()
) where S : ModelExecution<C>, C : Context, C : BasePrivateCarContext, C : HouseholdContext, C : PersonContext<*, *> {
    val segmentCodePlan = carSegmentCode ?: this.context.carSegmentCodes
    val householdRepo = { context.householdRepository }
    val personRepo = { context.personRepository }

    val csvParser = CsvParser(errorHandling) { row ->
        PrivateCarBuilder(
            segment = row.decodeName(segmentColumnIndex, segmentCodePlan),
            engine = row(engineTypeColumn, ::parseEngineType),
            seats = row.int(seatsColumnIndex),
            owner = getOwnerHousehold(householdRepo, row, ownerColumn),
            mainUser = getMainUser(personRepo, row, mainUserColumn),
            carEngineStatistics = carEngineStatistics
        )
    }

    this.preparePrivateCarsFile(csvParser, file, delimiter)
}

fun <S, C> S.preparePrivateCarsFile(
    parser: CsvParser<PrivateCarBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : BasePrivateCarContext {
    val carFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\car.csv")
    val resource = CsvResource(carFile, parser, delimiter)

    this.addStep(
        AddCsvStep(
            name = "load car csv",
            csv = resource,
            repository = context.carRepository
        )
    )
}

fun <S, C> S.finishPrivateCars() where S : ModelExecution<C>, C : Context, C : BasePrivateCarContext {
    this.addStep(BuildStep("finish cars", context.carRepository))
}

fun <S, C> S.loadPrivateCars()
    where S : ModelExecution<C>, C : Context, C : HouseholdContext, C : PersonContext<*, *>, C : BasePrivateCarContext {
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
    householdRepo: () -> Repository<HouseholdData, HouseholdId>,
    row: Row,
    ownerColumn: String
) = requireNotNull(
    householdRepo().getById(row.id(ownerColumn))
) {
    "Referenced household id ${row(ownerColumn)} could not be found in householdRepo:" +
        " ${householdRepo().elements.map { it.id }.toList()}"
}

internal fun getMainUser(
    personRepo: () -> Repository<PersonData, PersonId>,
    row: Row,
    mainUserColumn: String
) = requireNotNull(
    personRepo().getById(row.id(mainUserColumn))
) {
    "Referenced person id ${row(mainUserColumn)} could not be found in personRepo:" +
        " ${personRepo().elements.map { it.id }.toList()}"
}
