package domain.synthesis.results.fastcsv

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.SynthesisHousehold
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

fun <S : MinimumHouseholdAttributes> Collection<SynthesisHousehold<S, *>>.write(path: Path) =
    Files.newBufferedWriter(path).use { writer ->
        this.write(writer)

    }

fun <S : MinimumHouseholdAttributes> Collection<SynthesisHousehold<S, *>>.write(writer: Writer) {


    this.writeCsvWithAttributeProjection(
        writer,
        attributeExtractor = { it.attributes },
        headerPrefix = listOf("id", "economicStatus", "amountOfCars"),
        outputPrefix = {
            buildList {
                add(it.id.toString())
                add(it.economicStatus.name)
                add(it.amountOfCars.toString())
            }
        }
    )
}