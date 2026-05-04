package domain.synthesis.parser

import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.buildEngine
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.decodeName
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter

data class CarColumns( //TODO drop support for old format
    val ownerColumn: String = "ownerId",
    val mainUserColumn: String = "mainUserId",
    val engineTypeColumn: String = "carType",
    val segmentColumnIndex: Int = 7,
    val seatsColumnIndex: Int = 8,
) {
    companion object {
        val NEW_FORMAT = CarColumns(segmentColumnIndex = 4, seatsColumnIndex = 5)
    }
}

data class PrivateCarCsvConfig(
//    var path: Path,
    var columns: CarColumns = CarColumns(),
    var householdExists: (HouseholdId) -> Boolean,
    var getOwnerHousehold: (Row, String) -> MutableHousehold,
    var getMainUser: (Row, String) -> Person?,
    var carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    var carSegmentCodes: CodePlan<CarSegment>,
//    var delimiter: String = SEMICOLON,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var filter: PrivateCarCsvConfig.(Row) -> Boolean = ownerExistsFilter //TODO allow edit filter for other entities
)

val ownerExistsFilter: PrivateCarCsvConfig.(Row) -> Boolean = { row ->
    householdExists(
        HouseholdId(row.long(columns.ownerColumn))
    )
}

fun createPrivateCarCsvParser(
    csvConfig: PrivateCarCsvConfig
): CsvParser<MutablePrivateCar> = csvConfig.run {
    return CsvParser.Companion<MutablePrivateCar>(errorHandling) { row ->

        MutablePrivateCar(
            id = CarId(row.index.toLong()),
            owner = getOwnerHousehold(row, columns.ownerColumn)
        ) {
            seats = row.int(columns.seatsColumnIndex)
            mainUser = getMainUser(row, columns.mainUserColumn)
            segment = row.decodeName(columns.segmentColumnIndex, carSegmentCodes)
            val engineType = row(columns.engineTypeColumn, EngineType.Companion::parseEngineType)
            engine = carEngineStatistics.buildEngine(segment, engineType)
//            location = owner.location
        }
    }.withFilter { row -> filter(row) }
}