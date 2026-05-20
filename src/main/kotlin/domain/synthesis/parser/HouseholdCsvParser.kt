package domain.synthesis.parser

import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasRoadAccess
import domain.shared.location.parseRoadPositionWGS
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
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
    var getZone: (ZoneId) -> Zone, // TODO maybe Row.() -> Zone instead to be more flexible
    var roadPositionParser: (String) -> HasRoadAccess = String::parseRoadPositionWGS,
    var incomeUnit: CurrencyUnit,
    var economicStatusCodes: CodePlan<EconomicStatus>,
    var filter: HouseholdColumns.(Row) -> Boolean = { true },
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val seed: Long,
)

fun createHouseholdCsvParser(
    householdCsvConfig: HouseholdCsvConfig,
) = householdCsvConfig.run {
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
            val temp = row(columns.locationColumn, roadPositionParser)
            location = StandardLocation.Companion(
                position = temp.position,
                zone = getZone(ZoneId(row.long(columns.zoneColumn))),
                roadAccess = temp.roadAccess
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
