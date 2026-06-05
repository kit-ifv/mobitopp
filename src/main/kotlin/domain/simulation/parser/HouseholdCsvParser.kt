package domain.simulation.parser

import domain.shared.data.household.HouseholdId
import domain.shared.enums.household.EconomicStatus
import domain.shared.location.PointAndRoadPositionParser
import domain.shared.location.StandardLocation
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.units.CurrencyUnit
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.currency
import utils.csv.decode
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter

// household csv parsers
data class HouseholdCsvConfig(
    var columns: HouseholdColumns = HouseholdColumns(),
    var getZone: (ZoneId) -> Zone<HasRegionType>, // TODO maybe Row.() -> Zone instead to be more flexible
    var roadPositionParser: PointAndRoadPositionParser = PointAndRoadPositionParser.parseWGS,
    var incomeUnit: CurrencyUnit,
    var economicStatusCodes: CodePlan<EconomicStatus>,
    var filter: HouseholdColumns.(Row) -> Boolean = { true },
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val seed: Long,
)

fun createHouseholdCsvParser(householdCsvConfig: HouseholdCsvConfig) = householdCsvConfig.run {
    val parser = CsvParser.Companion(errorHandling) { row ->

        MutableHousehold(
            id = HouseholdId(row.long(columns.hhIdColumn)),
            seed,
        ) {
            householdNumber = row.long(columns.hhNumberColumn)
            surveyYear = row.int(columns.yearColumn)
            domCode = row.int(columns.domCodeColumn)
            type = row.int(columns.typeColumn)
            incomePerMonth = row.currency(columns.incomeColumn, incomeUnit)
            economicStatus = row.decode(columns.economicalStatusColumn, economicStatusCodes)

            // Robin: I converted this builder call to the location as found in [Household]
            val (point, roadAccess) = row(columns.locationColumn, roadPositionParser::parse)
            location = StandardLocation(
                position = point,
                zone = getZone(ZoneId(row.long(columns.zoneColumn))),
                roadAccess = roadAccess,
            )
        }
    }
    val filterWrap: (Row) -> Boolean = { columns.filter(it) }
    parser.withFilter(filterWrap)
}

data class HouseholdColumns(
    val hhNumberColumn: String = "householdNumber",
    val hhIdColumn: String = "householdId",
    val yearColumn: String = "year",
    val zoneColumn: String = "homeZone",
    val locationColumn: String = "homeLocation",
    val domCodeColumn: String = "domCode",
    val typeColumn: String = "type",
    val incomeColumn: String = "income",
    val economicalStatusColumn: String = "economicalStatus",
)
