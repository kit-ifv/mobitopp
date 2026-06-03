package domain.synthesis.results.fastcsv.writers

import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.fastcsv.writeCsv
import java.io.Writer

fun Collection<FixedDestinationElements>.writeFixedDestinations(writer: Writer) {
    writeCsv(writer, header = listOf("personId", "activityType", "locationZoneId")) {
        buildList {
            add(it.person.personId.toString())
            add(it.activityType.code.toString())
            add(it.location.zoneId.value.toString())
        }
    }
}
