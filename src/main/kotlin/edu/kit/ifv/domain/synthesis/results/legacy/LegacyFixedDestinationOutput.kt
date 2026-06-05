package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.results.CSVOutput
import edu.kit.ifv.domain.synthesis.results.FixedDestinationElements

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyFixedDestinationOutput : CSVOutput<FixedDestinationElements> {
    override val header: List<String> = listOf(
        "personOid",
        "personNumber", // Thats the number of the person in the household, no Idea why anyone would ever need that.
        "householdOid",
        "householdYear",
        "householdNumber",
        "activityType",
        "zoneId",
        "location",
        "locationX",
        "locationY",
    )

    @Suppress("MagicNumber")
    override fun convert(element: FixedDestinationElements): String = element.run {
        toCSV(
            person.personId,
            -1, // Dummy value for dummy output: This is the number in the household.
            person.householdId, // person.household.id,
            1970, // Dummy value for dumb output household year taken from survey data.
            -1, // Dummy value for dumb output: household ID from the survey data
            activityType.description,
            location.zoneId,
            location.toRecord().legacyStringRepresentation(),
            location.position.x,
            location.position.y,

        )
    }
}
