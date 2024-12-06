package usecases.steps.legacyData

import domain.data.LegacyZone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.SealStep
import units.DistanceUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import java.io.File

interface LoadZonesContext : Context {
    val zoneRepository: MutableRepository<LegacyZone, ZoneId>
    val areaTypeCodes: CodePlan<AreaType>

    val defaultZoneFile: File
        get() = File(demandFolder.path + "\\zone-repository\\zones.csv")
}

data class ZoneColumns(
    val idColumn: String = "id",
    val nameColumn: String = "name",
    val areaTypeColumn: String = "areaType",
    val regionTypeColumn: String = "regionType",
    val classificationColumn: String = "classification",
    val parkingPlacesColumn: String = "parkingPlaces",
    val centroidColumn: String = "centroidLocation",
    val isDestinationColumn: String = "isDestination",
    val reliefColumn: String = "relief",
)

@Suppress("LongParameterList", "UnusedParameter")
fun <S, C> S.prepareZones(
    file: File = context.defaultZoneFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    // areaTypeCodes: CodePlan<AreaType>? = null, -> should be consistent within project, only specify in context
    centroidParser: (String) -> Location = String::parseRoadPosition,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
) where S : ModelExecution<C>, C : LoadZonesContext {
    val csvParser = CsvParser<LegacyZone>(errorHandling) { row ->
        null
//        LegacyZoneBuilder().apply {
//            id = row.id(columns.idColumn)
//            visumId = row.long(columns.idColumn)
//            matrixColumn = row.index
//            name = row(columns.nameColumn)
//            areaType = row.decode(columns.areaTypeColumn, context.areaTypeCodes)
//            regionType = row.int(columns.regionTypeColumn)
//            classification = row(columns.classificationColumn).toZoneClassification()
//            parkingPlaces = row.int(columns.parkingPlacesColumn)
//            centroid = row(columns.centroidColumn, centroidParser)
//            isDestination = row.boolean(columns.isDestinationColumn)
//            relief = row.double().distance(columns.reliefColumn, reliefUnit)
//            id = ZoneId(row.long(columns.idColumn))
//        }
    }

    this.prepareZoneFile(csvParser, file, delimiter) // TODO
}

fun <S, C> S.prepareZoneFile(
    parser: CsvParser<LegacyZone>,
    file: File = context.defaultZoneFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadZonesContext {
    this.addStep(
        LoadCsvStep<LegacyZone, ZoneId>(
            file = file,
            name = "Load zones from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.zoneRepository,
            dependentRepositories = setOf(),
            validationMock = listOf() // TODO
        )
    )
}

// fun <S, C> S.filterZones() where S : ModelExecution<C>, C : LoadZonesContext {
//    this.addStep(FilterStep("filter zones", context.zoneRepository) { it.visumId == 1L })
// } //TODO

fun <S, C> S.finishZones() where S : ModelExecution<C>, C : LoadZonesContext {
    this.addStep(SealStep(context.zoneRepository))
}

fun <S, C> S.loadZones(
    errorHandling: ErrorHandling = ErrorHandling.WARNING
) where S : ModelExecution<C>, C : LoadZonesContext {
    this.prepareZones(errorHandling = errorHandling)
//    this.filterZones()
    this.finishZones()
}

fun String.toZoneClassification() = when (this) {
    "studyArea" -> ZoneClassification.STUDY_AREA
    "outlyingArea" -> ZoneClassification.OUTLYING_AREA
    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
    else -> throw UnsupportedOperationException(
        "String '$this' cannot be parsed as a ZoneClassification! " +
            "Expected: 'studyArea', 'outlyingArea' or 'extendedStudyArea'"
    )
}
