package edu.kit.ifv.domain.synthesis.results.legacy
import edu.kit.ifv.domain.synthesis.results.CSVOutput
import edu.kit.ifv.domain.synthesis.results.OpportunityOutput

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyOpportunitiesOutput : CSVOutput<OpportunityOutput> {
    override val header: List<String> =
        listOf("zoneId", "activityType", "location", "attractivity", "locationX", "locationY")

    override fun convert(element: OpportunityOutput): String = element.run {
        toCSV(
            location.zoneId.value,
            activityType,
            location.legacyStringRepresentation(),
            attractiveness.value,
            location.position.x,
            location.position.y,

        )
    }
}
