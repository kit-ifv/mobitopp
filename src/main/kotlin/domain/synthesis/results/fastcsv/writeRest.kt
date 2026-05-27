package domain.synthesis.results.fastcsv

import domain.shared.datastructure.schedule.Activity
import domain.synthesis.SynthesisHousehold
import domain.synthesis.SynthesisPerson
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.OpportunityOutput
import java.io.Writer

fun Collection<SynthesisHousehold<*, *>>.writeCars(writer: Writer) {
    writeCsvUnrolled(writer, header = listOf("householdId", "carId"), unroll = {it.cars}) { household, car ->
        buildList {
            add(household.id.toString())
            add(car.id.toString())
        }

    }
}

fun Map<SynthesisPerson<*, *>, Collection<Activity>>.writeActivities(
    writer: Writer,
) {

    writeToCsv(
        writer = writer,
        header = listOf("personId", "startTimeMinutes", "durationMinutes", "activityType")
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

fun Collection<FixedDestinationElements>.writeFixedDestinations(writer: Writer) {
    writeCsv(writer, header = listOf("personId", "activityType", "locationZoneId")) {
        buildList {
            add(it.person.personId.toString())
            add(it.activityType.code.toString())
            add(it.location.zoneId.value.toString())
        }
    }
}
fun Collection<OpportunityOutput>.writeOpportunities(writer: Writer): Nothing =
    error(writer)
