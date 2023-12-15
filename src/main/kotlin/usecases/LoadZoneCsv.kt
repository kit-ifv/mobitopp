package usecases

import domain.data.ZoneDataBuilder
import domain.enums.ZoneClassification
import domain.location.RoadPosition
import domain.location.parseRoadPosition
import modeling.synthesis.BaseContext
import modeling.synthesis.CsvResource
import modeling.synthesis.Synthesis
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvParserBuilder
import utils.csv.SEMICOLON
import utils.csv.property
import utils.units.DistanceUnit
import utils.units.toDistance
import java.io.File


fun <S, C> S.loadZoneCsv(
    parser: (Synthesis<C>) -> CsvParser<ZoneDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON
) where S: Synthesis<C>, C: BaseContext {

    val zoneFile = file ?: File(this.context.demandFolder.path + "\\zone-repository\\zones.csv")

    this.addIdResource(zoneFile.name, CsvResource(zoneFile, parser(this), delimiter)) {
            c, r -> c.zoneBuilders = r
    }

}






@Suppress("LongParameterList")
fun <C: BaseContext> zoneParser(
    idColumn: String = "id",
    nameColumn: String = "name",
    areaTypeColumn: String = "areaType",
    regionTypeColumn: String = "regionType",
    classificationColumn: String = "classification",
    parkingPlacesColumn: String = "parkingPlaces",
    centroidColumn: String = "centroidLocation", //TODO allow different coord formats?
    centroidParser: (String) -> RoadPosition = String::parseRoadPosition,
    isDestinationColumn: String = "isDestination",
    reliefColumn: String = "relief",
    reliefUnit: DistanceUnit = DistanceUnit.METERS
) : (Synthesis<C>) -> CsvParser<ZoneDataBuilder> = { synthesis ->

    val areaTypeCodePlan = synthesis.context.areaTypCodes

    CsvParserBuilder { ZoneDataBuilder() }
        .long.property(idColumn) { e, l -> e.visumId = l }
        .string.property(nameColumn) { e, s -> e.name = s }
        .int.property(areaTypeColumn) { e, i -> e.areaType = areaTypeCodePlan.decode(i) }
        .int.property(regionTypeColumn) { e, i -> e.regionType = i }
        .string.property(classificationColumn) { e, s ->
            e.classification = when (s) {
                "studyArea" -> ZoneClassification.STUDY_AREA
                "outlyingArea" -> ZoneClassification.OUTLYING_AREA
                "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
                else -> throw UnsupportedOperationException("Sorry this is not existing")
            }
        }
        .int.property(parkingPlacesColumn) { e, i -> e.parkingPlaces = i }
        .string.property(centroidColumn) { e, s -> e.centroid = centroidParser(s) }
        .boolean.property(isDestinationColumn) { e, b -> e.isDestination = b }
        .double.property(reliefColumn) { e, d -> e.relief = d.toDistance(reliefUnit) }
        .onErrorUse(ErrorHandling.WARN_DROP)
        .build()
}
