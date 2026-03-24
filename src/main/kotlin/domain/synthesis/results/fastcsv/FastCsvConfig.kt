package domain.synthesis.results.fastcsv

import de.siegmar.fastcsv.writer.CsvWriter
import java.io.Writer

/**
 * To convert the java builder pattern into a more kotlin style builder
 */
data class FastCsvConfig(
    var separator: Char = ';',
) {
    fun build(writer: Writer): CsvWriter {
        return CsvWriter.builder().fieldSeparator(separator).build(writer)
    }


    companion object {
        val DEFAULT = FastCsvConfig()
    }
}