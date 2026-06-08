package edu.kit.ifv.domain.simulation.parser
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.PointAndRoadPositionParser
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.simulation.data.ActivityLocation
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.csv.CsvParser
import edu.kit.ifv.utils.csv.decodeName
import edu.kit.ifv.utils.csv.long
import edu.kit.ifv.utils.csv.withFilter

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
