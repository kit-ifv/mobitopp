package edu.kit.ifv.domain.synthesis.results.fastcsv.writers
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.results.fastcsv.writeCsvWithGenericAttributes
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
