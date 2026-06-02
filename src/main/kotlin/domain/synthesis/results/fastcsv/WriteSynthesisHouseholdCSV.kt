package domain.synthesis.results.fastcsv

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

fun <S : MinimumHouseholdAttributes> Collection<SynthesisHousehold<S, *>>.write(path: Path) = Files.newBufferedWriter(
    path,
).use { writer ->
    this.write(writer)
}

fun <S> Collection<SynthesisHousehold<S, *>>.write(writer: Writer) where S : MinimumHouseholdAttributes {
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
