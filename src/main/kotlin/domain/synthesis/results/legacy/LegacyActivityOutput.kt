package domain.synthesis.results.legacy

import domain.shared.datastructure.schedule.action.Activity
import domain.synthesis.SynthesisPerson
import domain.synthesis.results.CSVOutput

// TODO the synthesis activity will probably not match with the simulation activity.
@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyActivityOutput : CSVOutput<Pair<SynthesisPerson<*, *>, Collection<Activity>>> {
    override val header: List<String> = listOf(
        "personId",
        "activityType",
        "observedTripDuration",
        "startTime",
        "duration",
        "tournr",
        "isMainActivity",
        "isSupertour",
    )

    override fun convert(element: Pair<SynthesisPerson<*, *>, Collection<Activity>>): String {
        val (person, activities) = element
        var tournr = 0
        return activities.joinToString(separator = "\n") { activity: Activity ->
            activity.run {
                toCSV(
                    person.personId,
                    type.code,
                    -1, // TODO this was originally the observed trip duration from actitopp, which we cannot access
                    startTime.minutesSinceStart,
                    duration.inWholeMinutes,
                    tournr++,
                    "false", // TODO this is an actitopp information, that does not pass through
                    "false", // TODO this is also actitopp specific.
                )
            }
        }
    }
}
