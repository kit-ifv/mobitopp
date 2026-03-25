package domain.synthesis.results.fastcsv

import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.SynthesisPerson
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path


fun <T: MinimumPersonAttributes> Collection<SynthesisPerson<*, T>>.write(path: Path, config: FastCsvConfig =
FastCsvConfig.DEFAULT) =
    Files.newBufferedWriter(path).use { writer ->
        this.write(writer, config)

    }

fun <T: MinimumPersonAttributes> Collection<SynthesisPerson<*, T>>.write(writer: Writer, config: FastCsvConfig =
    FastCsvConfig.DEFAULT) {
    writeCsvWithAttributeProjection(
        writer, config,
        attributeExtractor = {it.attributes},
        headerPrefix = listOf("personID", "householdID"),
        outputPrefix = {
            buildList {
                add(it.personId.toString())
                add(it.householdID.toString())
            }
        },
    )
}