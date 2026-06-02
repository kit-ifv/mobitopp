package domain.synthesis.parser

import domain.shared.enums.ActivityType
import domain.shared.location.PointAndRoadPositionParser
import domain.shared.location.StandardLocation
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.Zone
import domain.synthesis.data.PersonId
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.decodeName
import utils.csv.long
import utils.csv.withFilter

data class FixedDestinationColumns(
    val personOid: String = "personOid",
    val activityType: String = "activityType",
    val location: String = "location",
    val zone: String = "zoneId",
)

data class FixedDestinationCsvConfig(
    var columns: FixedDestinationColumns = FixedDestinationColumns(),
    var personsExists: (PersonId) -> Boolean,
    var zoneConverter: (ZoneId) -> Zone<HasRegionType>,
    var activityTypes: CodePlan<ActivityType>,
    var pointAndRoadPositionParser: PointAndRoadPositionParser = PointAndRoadPositionParser.parseWGS,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
)

fun createFixedDestinationCsvParser(csvConfig: FixedDestinationCsvConfig): CsvParser<ActivityLocation> = csvConfig.run {
    CsvParser(errorHandling) { row ->
        val personId = PersonId(row.long(columns.personOid))
        val activityType = row.decodeName( // TODO switch to decode by int
            columns.activityType,
            activityTypes,
        )
        val zone = zoneConverter(ZoneId(row.long(columns.zone)))

        val (position, roadAccess) =
            row(columns.location, pointAndRoadPositionParser::parse)
        val location = StandardLocation(position, zone, roadAccess)

        ActivityLocation(personId, activityType, location)
    }.withFilter { row ->
        personsExists(PersonId(row.long(columns.personOid)))
    }
}
