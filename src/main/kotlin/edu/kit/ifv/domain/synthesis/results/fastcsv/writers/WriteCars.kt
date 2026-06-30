package edu.kit.ifv.domain.synthesis.results.fastcsv.writers
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.results.fastcsv.writeCsvUnrolled
import java.io.Writer

fun Collection<SynthesisHousehold<*, *>>.writeCars(writer: Writer) {
    writeCsvUnrolled(writer, header = listOf("householdId", "carId"), unroll = { it.cars }) { household, car ->
        buildList {
            add(household.id.toString())
            add(car.id.toString())
        }
    }
}
