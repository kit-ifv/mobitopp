package application.steps.parser.csv

/**
 * ROBIN: I think extracting a parameter object may be helpful to avoid the long parameter list, and makes the method
 * significantly more readable.
 */
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
