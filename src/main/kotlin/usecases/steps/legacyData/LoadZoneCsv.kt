package usecases.steps.legacyData

import domain.data.LegacyZoneBuilder
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.steps.AddCsvStep
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.FilterStep
import modeling.steps.ModelExecution
import units.DistanceUnit
import usecases.steps.LegacyZonesContext
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.decode
import utils.csv.distance
import utils.csv.double
import utils.csv.id
import utils.csv.int
import utils.csv.long
import java.io.File

@Suppress("LongParameterList")
fun <S, C> S.prepareZones(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    idColumn: String = "id",
    nameColumn: String = "name",
    areaTypeColumn: String = "areaType",
    areaTypeCodes: CodePlan<AreaType>? = null,
    regionTypeColumn: String = "regionType",
    classificationColumn: String = "classification",
    parkingPlacesColumn: String = "parkingPlaces",
    centroidColumn: String = "centroidLocation",
    centroidParser: (String) -> RoadPosition = String::parseRoadPosition,
    isDestinationColumn: String = "isDestination",
    reliefColumn: String = "relief",
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext {
    val areaTypeCodePlan = areaTypeCodes ?: this.context.areaTypeCodes

    val csvParser = CsvParser(errorHandling) { row ->
        LegacyZoneBuilder().apply {
            id = row.id(idColumn)
            visumId = row.long(idColumn)
            matrixColumn = row.index
            name = row(nameColumn)
            areaType = row.decode(areaTypeColumn, areaTypeCodePlan)
            regionType = row.int(regionTypeColumn)
            classification = row(classificationColumn).toZoneClassification()
            parkingPlaces = row.int(parkingPlacesColumn)
            centroid = row(centroidColumn, centroidParser)
            isDestination = row.boolean(isDestinationColumn)
            relief = row.double().distance(reliefColumn, reliefUnit)
            id = ZoneId(row.long(idColumn))
        }
    }

    this.prepareZoneFile(csvParser, file, delimiter)
}

fun <S, C> S.prepareZoneFile(
    parser: CsvParser<LegacyZoneBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext {
    val path = this.context.demandFolder.path + "\\zone-repository\\zones.csv"
    val zonesFile = file ?: File(path)

    val resource = CsvResource(zonesFile, parser, delimiter)

    this.addStep(
        AddCsvStep(
            name = "load zone csv",
            csv = resource,
            repository = context.zoneRepository
        )
    )
}

fun <S, C> S.filterZones() where S : ModelExecution<C>, C : LegacyZonesContext {
    this.addStep(FilterStep("filter zones", context.zoneRepository) { it.visumId == 1L })
}

fun <S, C> S.finishZones() where S : ModelExecution<C>, C : LegacyZonesContext {
    this.addStep(BuildStep("finish zones", context.zoneRepository))
}

fun <S, C> S.loadZones(
    errorHandling: ErrorHandling = ErrorHandling.WARNING
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext {
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
