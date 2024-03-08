package usecases

import CodePlan
import domain.data.ZoneData
import domain.data.ZoneDataBuilder
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.synthesis.BuilderRepository
import modeling.synthesis.Context
import modeling.synthesis.CsvResource
import modeling.synthesis.PrepareCsvStep
import modeling.synthesis.Synthesis
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.decode
import utils.csv.distance
import utils.csv.int
import utils.csv.long
import utils.units.DistanceUnit
import java.io.File


//fun <S, C> S.loadZoneCsv(
//    parser: (Synthesis<C>) -> CsvParser<ZoneDataBuilder>,
//    file: File? = null,
//    delimiter: String = SEMICOLON
//) where S: Synthesis<C>, C: BaseContext {
//
//    val zoneFile = file ?: File(this.context.demandFolder.path + "\\zone-repository\\zones.csv")
//
//    this.addIdResource(zoneFile.name, CsvResource(zoneFile, parser(this), delimiter)) {
//            c, r -> c.zoneBuilders = r
//    }
//
//}
@Suppress("LongParameterList")
fun <S, C> S.loadZones(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    parser: CsvParser<ZoneDataBuilder>? = null,
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

    val csvParser = parser ?: CsvParser(errorHandling) { row ->
        ZoneDataBuilder(
            visumId = row.long(idColumn),
            name = row(nameColumn),
            areaType = row.decode(areaTypeColumn, areaTypeCodePlan),
            regionType = row.int(regionTypeColumn),
            classification = row(classificationColumn).toZoneClassification(),
            parkingPlaces = row.int(parkingPlacesColumn),
            centroid = row(centroidColumn, centroidParser),
            isDestination = row.boolean(isDestinationColumn),
            relief = row.int().distance(reliefColumn, reliefUnit)
        )
    }

    this.parseZones(csvParser, file, delimiter)

}

fun <S, C> S.parseZones(
    parser: CsvParser<ZoneDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S: Synthesis<C>, C: Context {
    val zonesFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\household.csv")

//    CsvResource<ZoneDataBuilder>(zonesFile, )
}

class LoadZoneCsv(
    repository: BuilderRepository<ZoneDataBuilder, ZoneData>,
    csv: CsvResource<ZoneDataBuilder>
): PrepareCsvStep<ZoneDataBuilder, ZoneData>(
    name="load legacy zone csv",
    csv,
    repository
) {

}


fun String.toZoneClassification() = when (this) {
    "studyArea" -> ZoneClassification.STUDY_AREA
    "outlyingArea" -> ZoneClassification.OUTLYING_AREA
    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
    else -> throw UnsupportedOperationException("Sorry this is not existing")
}
