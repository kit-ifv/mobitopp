package domain.synthesis.results.fastcsv.writers

import domain.synthesis.results.OpportunityOutput
import domain.synthesis.results.fastcsv.writeCsv
import java.io.Writer
fun Collection<OpportunityOutput>.writeOpportunities(writer: Writer) {
    writeCsv(writer, header = listOf("zoneId", "attractiveness", "activityType")) {
        buildList {
            add(it.location.zoneId.value.toString())
            add(it.attractiveness.value.toString())
            add(it.activityType.code.toString())
        }
    }
}
