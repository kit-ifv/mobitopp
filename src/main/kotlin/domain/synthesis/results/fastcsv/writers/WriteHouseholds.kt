package domain.synthesis.results.fastcsv.writers

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.results.fastcsv.writeCsvWithGenericAttributes
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

fun <S : MinimumHouseholdAttributes> Collection<SynthesisHousehold<S, *>>.writeHouseholds(path: Path) =
    Files.newBufferedWriter(
        path,
    ).use { writer ->
        this.writeHouseholds(writer)
    }

fun <S> Collection<SynthesisHousehold<S, *>>.writeHouseholds(writer: Writer) where S : MinimumHouseholdAttributes {
    this.writeCsvWithGenericAttributes(
        writer,
        attributeExtractor = { it.attributes },
        headerPrefix = listOf("id"),
        outputPrefix = {
            buildList {
                add(it.id.toString())
            }
        },
    )
}
