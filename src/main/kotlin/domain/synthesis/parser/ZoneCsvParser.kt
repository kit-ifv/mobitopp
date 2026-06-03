package domain.synthesis.parser

import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.PointAndRoadPositionParser
import domain.shared.location.PointParser
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.MaximumZoneAttributesImpl
import edu.kit.ifv.units.DistanceUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultCsvParser
import utils.csv.boolean
import utils.csv.decode
import utils.csv.distance
import utils.csv.double
import utils.csv.int
import utils.csv.long

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

data class ZoneCsvConfig(
    var columns: ZoneColumns = ZoneColumns(),
    var centroidParser: PointParser = PointAndRoadPositionParser.parseWGS,
    var reliefUnit: DistanceUnit = DistanceUnit.METERS,
    var regionTypeCodes: CodePlan<RegionType> = RegioStaR17,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val seed: Long,
)

@Suppress("LongParameterList")
fun createZoneCsvParser(csvConfig: ZoneCsvConfig): DefaultCsvParser<MaximalZone> = csvConfig.run {
    val csvParser = CsvParser(errorHandling) { row ->
        val attributes = MaximumZoneAttributesImpl(

            visumId = row.long(columns.idColumn),
            name = row(columns.nameColumn),
            regionType = row.decode(columns.regionTypeColumn, regionTypeCodes),
            classification = row(columns.classificationColumn).toZoneClassification(),
            parkingPlaces = row.int(columns.parkingPlacesColumn),
            isDestination = row.boolean(columns.isDestinationColumn),
            relief = row.double().distance(columns.reliefColumn, reliefUnit),
            centroid = row(columns.centroidColumn, centroidParser::parsePoint),
        )

        MaximalZone(
            zoneId = ZoneId(row.long(columns.idColumn)),
            attributes = attributes,

        )
    }

    return csvParser
}

fun String.toZoneClassification() = when (this) { // TODO config option in csv config
    "studyArea" -> ZoneClassification.STUDY_AREA

    "outlyingArea" -> ZoneClassification.OUTLYING_AREA

    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA

    else -> throw UnsupportedOperationException(
        "String '$this' cannot be parsed as a ZoneClassification! " +
            "Expected: 'studyArea', 'outlyingArea' or 'extendedStudyArea'",
    )
}
