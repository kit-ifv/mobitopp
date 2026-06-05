package edu.kit.ifv.domain.synthesis.results.fastcsv.writers
import edu.kit.ifv.domain.synthesis.SynthesisPerson
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.results.fastcsv.FastCsvConfig
import edu.kit.ifv.domain.synthesis.results.fastcsv.writeCsvWithGenericAttributes
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

fun <T : MinimumPersonAttributes> Collection<SynthesisPerson<*, T>>.writePersons(
    path: Path,
    config: FastCsvConfig =
        FastCsvConfig.Companion.DEFAULT,
) = Files.newBufferedWriter(path).use { writer ->
    this.writePersons(writer, config)
}

fun <T : MinimumPersonAttributes> Collection<SynthesisPerson<*, T>>.writePersons(
    writer: Writer,
    config: FastCsvConfig =
        FastCsvConfig.Companion.DEFAULT,
) {
    writeCsvWithGenericAttributes(
        writer,
        config,
        attributeExtractor = { it.attributes },
        headerPrefix = listOf("personId", "householdId"),
        outputPrefix = {
            buildList {
                add(it.personId.toString())
                add(it.householdID.toString())
            }
        },
    )
}
