package usecases

import CodePlan
import domain.data.ZoneDataBuilder
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.synthesis.BuildStep
import modeling.synthesis.Context
import modeling.synthesis.CsvResource
import modeling.synthesis.PrepareCsvStep
import modeling.synthesis.Synthesis
import units.DistanceUnit
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.decode
import utils.csv.distance
import utils.csv.double
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
    reliefUnit: DistanceUnit = DistanceUnit.METERS
) where S: Synthesis<C>, C: Context {

    val areaTypeCodePlan = areaTypeCodes ?: this.context.areaTypeCodes

    val csvParser = CsvParser(errorHandling) { row ->
        ZoneDataBuilder(
            visumId = row.long(idColumn),
            matrixColumn = row.index,
            name = row(nameColumn),
            areaType = row.decode(areaTypeColumn, areaTypeCodePlan),
            regionType = row.int(regionTypeColumn),
            classification = row(classificationColumn).toZoneClassification(),
            parkingPlaces = row.int(parkingPlacesColumn),
            centroid = row(centroidColumn, centroidParser),
            isDestination = row.boolean(isDestinationColumn),
            relief = row.double().distance(reliefColumn, reliefUnit)
        )
    }

    this.prepareZoneFile(csvParser, file, delimiter)

}

fun <S, C> S.prepareZoneFile(
    parser: CsvParser<ZoneDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S: Synthesis<C>, C: Context {
    val zonesFile = file ?: File(this.context.demandFolder.path + "\\zone-repository\\zones.csv")

    val resource = CsvResource(zonesFile, parser, delimiter)

    this.addStep(
        PrepareCsvStep(
            name = "load zone csv",
            csv=resource,
            repository = context.zoneRepository
        )
    )
}

fun <S, C> S.finishZones() where S: Synthesis<C>, C: Context {
    this.addStep(BuildStep("finish zones", context.zoneRepository))
}

fun <S, C> S.loadZones() where S: Synthesis<C>, C: Context {
    this.prepareZones()
    this.finishZones()
}


fun String.toZoneClassification() = when (this) {
    "studyArea" -> ZoneClassification.STUDY_AREA
    "outlyingArea" -> ZoneClassification.OUTLYING_AREA
    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
    else -> throw UnsupportedOperationException("String '$this' cannot be parsed as a ZoneClassification! " +
            "Expected: 'studyArea', 'outlyingArea' or 'extendedStudyArea'")
}
