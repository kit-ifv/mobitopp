package edu.kit.ifv.domain.synthesis.results.fastcsv.writers
import edu.kit.ifv.domain.synthesis.results.FixedDestinationElements
import edu.kit.ifv.domain.synthesis.results.fastcsv.writeCsv
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
