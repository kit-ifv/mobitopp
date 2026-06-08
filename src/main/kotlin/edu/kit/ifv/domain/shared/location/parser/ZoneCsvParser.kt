package edu.kit.ifv.domain.shared.location.parser
import edu.kit.ifv.domain.shared.enums.ZoneClassification
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.location.PointAndRoadPositionParser
import edu.kit.ifv.domain.shared.location.PointParser
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.MaximumZoneAttributesImpl
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.csv.CsvParser
import edu.kit.ifv.utils.csv.DefaultCsvParser
import edu.kit.ifv.utils.csv.boolean
import edu.kit.ifv.utils.csv.decode
import edu.kit.ifv.utils.csv.distance
import edu.kit.ifv.utils.csv.double
import edu.kit.ifv.utils.csv.int
import edu.kit.ifv.utils.csv.long

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
