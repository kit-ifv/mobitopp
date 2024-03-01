//package usecases
//
//import domain.data.ZoneDataBuilder
//import domain.enums.ZoneClassification
//import domain.location.RoadPosition
//import domain.location.parseRoadPosition
//import modeling.synthesis.BaseContext
//import modeling.synthesis.CsvResource
//import modeling.synthesis.Synthesis
//import utils.csv.CsvParser
//import utils.csv.DefaultRowCsvParser
//import utils.csv.SEMICOLON
//import utils.csv.boolean
//import utils.csv.decode
//import utils.csv.distance
//import utils.csv.int
//import utils.csv.long
//import utils.units.DistanceUnit
//import java.io.File
//
//
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
//
//@Suppress("LongParameterList")
//fun <C: BaseContext> zoneParser(
//    idColumn: String = "id",
//    nameColumn: String = "name",
//    areaTypeColumn: String = "areaType",
//    regionTypeColumn: String = "regionType",
//    classificationColumn: String = "classification",
//    parkingPlacesColumn: String = "parkingPlaces",
//    centroidColumn: String = "centroidLocation",
//    centroidParser: (String) -> RoadPosition = String::parseRoadPosition,
//    isDestinationColumn: String = "isDestination",
//    reliefColumn: String = "relief",
//    reliefUnit: DistanceUnit = DistanceUnit.METERS
//) : (Synthesis<C>) -> CsvParser<ZoneDataBuilder> = { synthesis ->
//
//    val areaTypeCodePlan = synthesis.context.areaTypCodes
//
//    DefaultRowCsvParser { row ->
//        ZoneDataBuilder(
//            visumId = row.long()[idColumn],
//            name = row[nameColumn],
//            areaType = row.decode(areaTypeCodePlan)[areaTypeColumn],
//            regionType = row.int()[regionTypeColumn],
//            classification = row[classificationColumn].toZoneClassification(),
//            parkingPlaces = row.int()[parkingPlacesColumn],
//            centroid = row(centroidColumn, centroidParser),
//            isDestination = row.boolean()[isDestinationColumn],
//            relief = row.int().distance(reliefUnit)[reliefColumn]
//        )
//    }
//
//}
//
//fun String.toZoneClassification() = when (this) {
//    "studyArea" -> ZoneClassification.STUDY_AREA
//    "outlyingArea" -> ZoneClassification.OUTLYING_AREA
//    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
//    else -> throw UnsupportedOperationException("Sorry this is not existing")
//}
