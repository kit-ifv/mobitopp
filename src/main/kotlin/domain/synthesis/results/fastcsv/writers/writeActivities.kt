package domain.synthesis.results.fastcsv.writers

import domain.shared.datastructure.schedule.action.Activity
import domain.synthesis.SynthesisPerson
import domain.synthesis.results.fastcsv.writeToCsv
import java.io.Writer

fun Map<SynthesisPerson<*, *>, Collection<Activity>>.writeActivities(writer: Writer) {
    writeToCsv(
        writer = writer,
        header = listOf("personId", "startTimeMinutes", "durationMinutes", "activityType"),
    )
    { person, activity ->
        buildList {
            add(person.personId.toString())
            add(activity.startTime.minutesSinceStart.toString())
            add(activity.duration.inWholeMinutes.toString())
            add(activity.type.code.toString())
        }
    }
}
