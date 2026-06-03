package domain.synthesis.results.fastcsv.writers

import domain.synthesis.SynthesisHousehold
import domain.synthesis.results.fastcsv.writeCsvUnrolled
import java.io.Writer

fun Collection<SynthesisHousehold<*, *>>.writeCars(writer: Writer) {
    writeCsvUnrolled(writer, header = listOf("householdId", "carId"), unroll = { it.cars }) { household, car ->
        buildList {
            add(household.id.toString())
            add(car.id.toString())
        }
    }
}
